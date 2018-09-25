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

package me.francescotonini.beaconservice.repositories;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.content.Context;
import android.support.annotation.Nullable;
import org.altbeacon.beacon.BeaconManager;

import java.util.ArrayList;
import java.util.List;
import me.francescotonini.beaconservice.AppExecutors;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.db.AppDatabase;
import me.francescotonini.beaconservice.models.Beacon;
import me.francescotonini.beaconservice.sensors.BeaconScanner;

/**
 * Retrieves sensors data
 */
public class SensorsRepository {
    /**
     * Initializes a new instance of this class
     * @param context app context
     * @param appExecutors app executor
     * @param database an instance of {@link AppDatabase}
     * @param lifecycle activity/fragment lifecycle, can be null
     * @param beaconManager instance of {@link BeaconManager}
     */
    public SensorsRepository(Context context, AppExecutors appExecutors, AppDatabase database, @Nullable Lifecycle lifecycle, BeaconManager beaconManager) {
        this.context = context;
        this.appExecutors = appExecutors;
        this.lifecycle = lifecycle;
        this.database = database;
        this.beaconScanner = new BeaconScanner(context, beaconManager);

        if (this.lifecycle != null) {
            this.lifecycle.addObserver(this.beaconScanner);
        }
        else {
            Logger.w(SensorsRepository.class.getSimpleName(), "Lifecycle not set! Remember to manualy start and stop sensors");
        }
    }

    /**
     * Gets the current list of beacons available
     * @return current list of beacons available
     */
    public LiveData<List<Beacon>> getBeacons() {
        return beaconScanner.getBeacons();
    }

    /**
     * Starts the sensors
     */
    public void start() {
        if (mediator == null) {
            mediator = new MediatorLiveData<>();
            mediator.addSource(getBeacons(), beacons -> {
                database.beaconDao().insert(beacons);
            });
        }

        beaconScanner.start();
    }

    /**
     * Stops the sensors
     */
    public void stop() {
        if (mediator != null) {
            mediator.removeSource(getBeacons());
        }

        beaconScanner.stop();
    }

    /**
     * Sets the lifecycle
     * @param lifecycle lifecycle
     */
    public void setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        if (this.lifecycle != null) {
            this.lifecycle.addObserver(this.beaconScanner);
        }
    }

    private final AppExecutors appExecutors;
    private final Context context;
    private final AppDatabase database;
    private BeaconScanner beaconScanner;
    private Lifecycle lifecycle;
    private MediatorLiveData<List<Beacon>> mediator;
}
