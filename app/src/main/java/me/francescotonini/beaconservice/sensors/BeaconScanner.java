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

package me.francescotonini.beaconservice.sensors;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.models.Beacon;

/**
 * Handles beacons comunication
 */
public class BeaconScanner implements BeaconConsumer, RangeNotifier, LifecycleObserver {
    /**
     * Initializes a new instance of this class
     * @param context app context
     * @param manager instance of {@link BeaconManager}
     */
    public BeaconScanner(@NonNull Context context, @NonNull BeaconManager manager) {
        this.manager = manager;
        this.context = context;
        this.beaconRegion = new Region("beacon_region", null, null, null);
        beacons = new MutableLiveData<>();

        this.manager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        this.manager.bind(this);
    }

    /**
     * Gets a live data with the beacons in range
     * @return live data with the beacons in range
     */
    public LiveData<List<Beacon>> getBeacons() {
        return beacons;
    }

    /**
     * Starts beacons scan
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void start() {
        Logger.v(BeaconScanner.class.getSimpleName(), "Start beacons scan");

        try {
            manager.startRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            Logger.e(BeaconScanner.class.getSimpleName(), e.getMessage());
        }
    }

    /**
     * Stops beacons scan
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void stop() {
        Logger.v(BeaconScanner.class.getSimpleName(), "Stop beacons scan");

        try {
            manager.stopRangingBeaconsInRegion(beaconRegion);
        } catch (RemoteException e) {
            Logger.e(BeaconScanner.class.getSimpleName(), e.getMessage());
        }
    }

    @Override public void onBeaconServiceConnect() {
        manager.addRangeNotifier(this);
    }

    @Override public Context getApplicationContext() {
        return context;
    }

    @Override public void unbindService(ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    @Override public boolean bindService(Intent intent, ServiceConnection serviceConnection, int i) {
        return context.bindService(intent, serviceConnection, i);
    }

    @Override public void didRangeBeaconsInRegion(Collection<org.altbeacon.beacon.Beacon> collection, Region region) {
        Logger.d(BeaconScanner.class.getSimpleName(), String.format("Found %s beacons in range", collection.size()));

        List<Beacon> listOfBeacons = new ArrayList<>();
        for (org.altbeacon.beacon.Beacon b: collection) {
            listOfBeacons.add(new Beacon(
                    b.getBluetoothAddress(),
                    b.getId1().toUuid().toString(),
                    b.getRssi(),
                    b.getId2().toInt(),
                    b.getId3().toInt()
            ));

            Beacon lastBeacon = listOfBeacons.get(listOfBeacons.size() - 1);
            Logger.d(BeaconScanner.class.getSimpleName(), String.format("Found beacon: %s - %s - %d - %d - %d", lastBeacon.getAddress(), lastBeacon.getUuid(), lastBeacon.getRssi(), lastBeacon.getMajor(), lastBeacon.getMinor()));
        }

        beacons.setValue(listOfBeacons);
    }

    private final MutableLiveData<List<Beacon>> beacons;
    private final BeaconManager manager;
    private final Context context;
    private final Region beaconRegion;
}
