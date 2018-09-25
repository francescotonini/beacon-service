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
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import me.francescotonini.beaconservice.BeaconServiceApp;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.R;
import me.francescotonini.beaconservice.repositories.SensorsRepository;
import me.francescotonini.beaconservice.views.MainActivity;

/**
 * Foreground service for beacon polling
 */
public class BeaconService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Logger.d(BeaconService.class.getSimpleName(), "Service created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Logger.d(BeaconService.class.getSimpleName(), String.format("Command %s received", intent.getAction()));
        if (intent.getAction() == "start") {
            // Add notification so that the service is set as "Foreground process"
            startServiceAsForeground();

            // Starts the sensor "polling"
            SensorsRepository repository = ((BeaconServiceApp)getApplication()).getDataRepository().getSensorsRepository();
            repository.start();

            Logger.d(BeaconService.class.getSimpleName(), "Start polling");
        }
        else if (intent.getAction() == "stop") {
            // Stops the sensor "polling"
            SensorsRepository repository = ((BeaconServiceApp)getApplication()).getDataRepository().getSensorsRepository();
            repository.stop();

            Logger.d(BeaconService.class.getSimpleName(), "Stop polling");

            stopForeground(true);
            stopSelf();
        }

        return Service.START_STICKY;
    }

    private void startServiceAsForeground() {
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);

        Notification notification = new NotificationCompat.Builder(this, "miscellaneous")
                .setContentTitle(getString(R.string.notification_title))
                .setContentText(getString(R.string.notification_message))
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setOngoing(true).build();

        startForeground(101, notification);
    }
}
