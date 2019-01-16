/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2018 Francesco Tonini <francescoantoniotonini@gmail.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package me.francescotonini.beaconservice.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.francescotonini.beaconservice.AppExecutors;
import me.francescotonini.beaconservice.BeaconServiceApp;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.R;
import me.francescotonini.beaconservice.db.AppDatabase;
import me.francescotonini.beaconservice.views.MainActivity;

/**
 * Servizio in foreground per analisi beacon
 */
public class BeaconService extends Service implements BeaconConsumer, RangeNotifier {
    // Possibili azioni che il servizio può intraprendere
    // START: avvia il polling
    // STOP: arresta il polling
    public enum ACTIONS {
        START,
        STOP
    }

    // Questo servizio non è disponibile in modalità "bind", pertanto il metodo restituisce null
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(BeaconService.class.getSimpleName(), "Service created");

        lastUpdateTimestamp = System.currentTimeMillis();
        appExecutors = ((BeaconServiceApp)getApplication()).getDataRepository().getAppExecutors();
        database = ((BeaconServiceApp)getApplication()).getDataRepository().getDatabase();
        beaconManager = BeaconManager.getInstanceForApplication(getApplicationContext());
        beaconManager.getBeaconParsers().add(new BeaconParser("Eddystone")
                .setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser("iBeacon")
                .setBeaconLayout("m:0-3=4c000215,i:4-19,i:20-21,i:22-23,p:24-24"));
        //noinspection deprecation
        // 😏
        BeaconManager.setDebug(true);
        beaconManager.bind(this);
        beaconRegion = new Region("beacon_region", null, null, null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ACTIONS action = ACTIONS.valueOf(intent.getAction());

        Logger.d(BeaconService.class.getSimpleName(), String.format("Received action %s", action));

        if (action == ACTIONS.START) {
            Logger.d(BeaconService.class.getSimpleName(), "Start polling");

            // Imposta una notifica così che il servizio rimanga in esecuzione anche quando l'applicazione è in background
            // Il parametro indica il numero di beacon trovati finora. Dato che il servizio è appena partito, si suppone che
            // non siano stati trovati beacon finora
            setNotification(totalBeaconsFound = 0);

            // L'avvio del monitoraggio viene demandato ad un'altra funzione, per chiarità
            startPolling();
        }
        else if (action == ACTIONS.STOP) {
            Logger.d(BeaconService.class.getSimpleName(), "Stop polling");

            // L'arresto del monitoraggio viene demandato ad un'altra funzione, per chiarità
            stopPolling();

            // Arresta il servizio e rimuove la notifica
            stopForeground(true);
            stopSelf();
        }

        // START_STICKY garantisce che se il servizio viene arresto dal SO, esso sarà riavviato
        // NOTA: l'applicazione DEVE essere attiva in background. L'arresto dell'applicazione
        // comporta l'arresto del servizio
        return Service.START_STICKY;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
        Logger.d(BeaconService.class.getSimpleName(), String.format("Found %s beacons in range", collection.size()));

        String timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS",
                Locale.getDefault()).format(new Date());

        long currentUpdateTimestamp = System.currentTimeMillis();
        if (currentUpdateTimestamp - lastUpdateTimestamp <= 100) {
            // Ho ricevuto un aggiornamento sotto la soglia minima di aggiornamento (ogni 100ms).
            // Ignoro i dati ricevuti
            Logger.v(BeaconService.class.getSimpleName(), "Got an update below the minimum threshold. Skip.");
            return;
        }
        lastUpdateTimestamp = currentUpdateTimestamp;

        // Converto l'oggetto Beacon di AltBeacon in un oggetto "pulito" da salvare nel database
        List<me.francescotonini.beaconservice.models.Beacon> listOfBeacons = new ArrayList<>();
        for (org.altbeacon.beacon.Beacon b: collection) {
            // NOTA: id1, id2 e id3 sono tre parametri generici che potrebbero variare per ogni
            // produttore di beacon. Nel caso di altbeacon, id1 è l'uuid, id2 e id3 sono major/minor

            String id1 = "null";
            try {
                id1 = b.getId1().toString();
            }
            catch (Exception ex) {
                Logger.e(BeaconService.class.getSimpleName(), ex.getMessage());
            }

            String id2 = "null";
            try {
                id2 = b.getId2().toString();
            }
            catch (Exception ex) {
                Logger.e(BeaconService.class.getSimpleName(), ex.getMessage());
            }

            String id3 = "null";
            try {
                id3 = b.getId3().toString();
            }
            catch (Exception ex) {
                Logger.e(BeaconService.class.getSimpleName(), ex.getMessage());
            }

            listOfBeacons.add(new me.francescotonini.beaconservice.models.Beacon(
                    b.getBluetoothAddress(),
                    id1,
                    id2,
                    id3,
                    b.getRssi(),
                    timestamp
            ));
        }

        // Salvo nel database
        appExecutors.diskIO().execute(() -> database.beaconDao().insert(listOfBeacons));

        // Aggiorno notifica
        setNotification(totalBeaconsFound += collection.size());
    }

    private void setNotification(int beaconsFoundSoFar) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Se il parametro della funzione è -1 visualizzo un messaggio di errore sulla notifica
        String notificationTitle = getString(R.string.beacon_service_notification_title);
        String notificationMessage = String.format(getString(R.string.beacon_service_notification_message), beaconsFoundSoFar);
        if (beaconsFoundSoFar == -1) {
            notificationMessage = getString(R.string.beacon_service_notification_message_error);
            notificationTitle = getString(R.string.beacon_service_notification_title_error);
        }

        Notification notification = new NotificationCompat.Builder(this, "miscellaneous")
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_stat_name)
                .setOngoing(true).build();

        startForeground(NOTIFICATION_ID, notification);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return getApplication().getApplicationContext().bindService(service, conn, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        getApplication().unbindService(conn);
    }

    @Override
    public Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    private void startPolling() {
        try {
            beaconManager.startRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            setNotification(-1);
            Logger.e(BeaconService.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    private void stopPolling() {
        try {
            beaconManager.stopRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            setNotification(-1);
            Logger.e(BeaconService.class.getSimpleName(), e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPolling();
        beaconManager.unbind(this);
    }

    private final int NOTIFICATION_ID = 1;
    private BeaconManager beaconManager;
    private Region beaconRegion;
    private AppDatabase database;
    private AppExecutors appExecutors;
    private int totalBeaconsFound;
    private long lastUpdateTimestamp = 0;
}
