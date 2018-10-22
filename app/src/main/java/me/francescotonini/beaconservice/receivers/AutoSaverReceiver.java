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

package me.francescotonini.beaconservice.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import com.google.gson.Gson;
import com.pixplicity.easyprefs.library.Prefs;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import me.francescotonini.beaconservice.BeaconServiceApp;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.models.AP;
import me.francescotonini.beaconservice.models.Beacon;

public class AutoSaverReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Prefs.getBoolean("autoSaver", false)) {
            Logger.v(AutoSaverReceiver.class.getSimpleName(), "Options is off");

            return;
        }

        BeaconServiceApp application = ((BeaconServiceApp)context.getApplicationContext());
        Gson gson = new Gson();

        application.getDataRepository().getAppExecutors().diskIO().execute(() -> {
            try {
                // Save aps to file
                List<AP> aps = application.getDataRepository().getDatabase().apDao().getAllSync();
                writeToFile("ap", gson.toJson(aps));
                application.getDataRepository().getDatabase().apDao().delete(aps);

                Logger.v(AutoSaverReceiver.class.getSimpleName(), "Ap save completed");
            }
            catch (Exception ex) {
                Logger.e(AutoSaverReceiver.class.getSimpleName(), ex.getMessage());
            }
        });

        application.getDataRepository().getAppExecutors().diskIO().execute(() -> {
            try {
                // Save beacons to file
                List<Beacon> beacons = application.getDataRepository().getDatabase().beaconDao().getAllSync();
                writeToFile("beacon", gson.toJson(beacons));
                application.getDataRepository().getDatabase().beaconDao().delete(beacons);

                Logger.v(AutoSaverReceiver.class.getSimpleName(), "Beacon save completed");
            }
            catch (Exception ex) {
                Logger.e(AutoSaverReceiver.class.getSimpleName(), ex.getMessage());
            }
        });
    }

    private void writeToFile(String filename, String json) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Collected data");
            if (!root.exists()) {
                //noinspection ResultOfMethodCallIgnored
                root.mkdirs();
            }
            File file = new File(root, String.format(Locale.getDefault(),
                    "%s_%d.json", filename, System.currentTimeMillis()));
            FileWriter writer = new FileWriter(file);
            writer.append(json);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
