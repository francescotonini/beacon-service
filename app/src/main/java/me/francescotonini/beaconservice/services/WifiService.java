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
import android.net.wifi.ScanResult;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import java.util.ArrayList;
import java.util.List;
import me.francescotonini.beaconservice.AppExecutors;
import me.francescotonini.beaconservice.BeaconServiceApp;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.R;
import me.francescotonini.beaconservice.db.AppDatabase;
import me.francescotonini.beaconservice.models.AP;
import me.francescotonini.beaconservice.receivers.WifiReceiver;
import me.francescotonini.beaconservice.views.MainActivity;

/**
 * Servizio in foreground per analisi wifi
 */
public class WifiService extends Service implements WifiReceiver.Listener {
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

        Logger.d(WifiService.class.getSimpleName(), "Service created");

        appExecutors = ((BeaconServiceApp)getApplication()).getDataRepository().getAppExecutors();
        database = ((BeaconServiceApp)getApplication()).getDataRepository().getDatabase();
        wifiReceiver = new WifiReceiver(getApplicationContext(), this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        WifiService.ACTIONS action = WifiService.ACTIONS.valueOf(intent.getAction());

        Logger.d(WifiService.class.getSimpleName(), String.format("Received action %s", action));

        if (action == WifiService.ACTIONS.START) {
            Logger.d(WifiService.class.getSimpleName(), "Start polling");

            // Imposta una notifica così che il servizio rimanga in esecuzione anche quando l'applicazione è in background
            // Il parametro indica il numero di ap trovati finora. Dato che il servizio è appena partito, si suppone che
            // non siano stati trovati ap finora
            setNotification(totalApFound = 0);

            // L'avvio del monitoraggio viene demandato ad un'altra funzione, per chiarità
            startPolling();
        }
        else if (action == WifiService.ACTIONS.STOP) {
            Logger.d(WifiService.class.getSimpleName(), "Stop polling");

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
    public void onData(List<ScanResult> scanResults) {
        Logger.v(WifiService.class.getSimpleName(), "Last scan found " + scanResults.size() + " ap's");

        // Filtra ap dell'università
        List<AP> univrAPs = new ArrayList<>();
        for(ScanResult result: scanResults) {
            // result.SSID.equals("UNIVAIR-OPEN")
            if (true) {
                univrAPs.add(new AP(result.SSID,
                        result.BSSID,
                        result.capabilities,
                        result.centerFreq0,
                        result.centerFreq1,
                        result.channelWidth,
                        result.frequency,
                        result.level
                ));
            }
        }

        Logger.v(WifiService.class.getSimpleName(), "Last scan found " + univrAPs.size() + " 'UNIVAIR-OPEN' ap's");

        // Salvo nel db le ultime registrazioni
        appExecutors.diskIO().execute(() -> database.apDao().insert(univrAPs));

        // Aggiorno notifica
        setNotification(totalApFound += univrAPs.size());
    }

    private void setNotification(int apFoundSoFar) {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        // Se il parametro della funzione è -1 visualizzo un messaggio di errore sulla notifica
        String notificationTitle = getString(R.string.wifi_service_notification_title);
        String notificationMessage = String.format(getString(R.string.wifi_service_notification_message), apFoundSoFar);
        if (apFoundSoFar == -1) {
            notificationMessage = getString(R.string.wifi_service_notification_message_error);
            notificationTitle = getString(R.string.wifi_service_notification_title_error);
        }

        Notification notification = new NotificationCompat.Builder(this, "miscellaneous")
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true).build();

        startForeground(NOTIFICATION_ID, notification);
    }

    private void startPolling() {
        wifiReceiver.start();
    }

    private void stopPolling() {
        wifiReceiver.stop();
    }

    private final int NOTIFICATION_ID = 2;
    private AppDatabase database;
    private AppExecutors appExecutors;
    private WifiReceiver wifiReceiver;
    private int totalApFound;
}
