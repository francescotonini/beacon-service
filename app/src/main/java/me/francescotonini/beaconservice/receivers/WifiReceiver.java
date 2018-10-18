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
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import java.util.List;

/**
 * Gestisce i dati ricevuti dal modulo wifi
 */
public class WifiReceiver extends BroadcastReceiver {
    /**
     * Interfaccia che notifica alla classe chiamante la ricezione di informazioni dal modulo wifi
     */
    public interface Listener {
        void onData(List<ScanResult> scanResults);
    }

    /**
     * Inizializza una nuova istanza di questa classe
     * @param context contesto dell'applicazione
     * @param listener classe che implementa {@link Listener}
     */
    public WifiReceiver(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;
        this.manager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
    }

    /**
     * Inizializza una nuova istanza di questa classe
     */
    public WifiReceiver() {
        this.listener = null;
        this.context = null;
        this.manager = null;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.listener.onData(manager.getScanResults());

        // Forza la ricerca, usare con cautela
        manager.startScan();
    }

    /**
     * Avvia la ricerca
     */
    public void start() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);

        context.registerReceiver(this, filter);
        running = true;
        manager.startScan();
    }

    /**
     * Arresta la ricerca
     */
    public void stop() {
        if (running) {
            context.unregisterReceiver(this);
            running = false;
        }
    }

    private boolean running = false;
    private final Listener listener;
    private final Context context;
    private final WifiManager manager;
}
