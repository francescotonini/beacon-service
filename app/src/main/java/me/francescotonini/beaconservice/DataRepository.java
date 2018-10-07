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

package me.francescotonini.beaconservice;

import android.arch.persistence.room.Room;
import android.content.Context;
import me.francescotonini.beaconservice.db.AppDatabase;

/**
 * Wraps every repository of this project
 */
public class DataRepository {
    /**
     * Gets an instance of this class
     *
     * @param appContext application context
     * @return an instance of {@link DataRepository}
     */
    public static DataRepository getInstance(final Context appContext, final AppExecutors appExecutors) {
        if (instance == null) {
            synchronized (DataRepository.class) {
                if (instance == null) {
                    instance = new DataRepository(appContext, appExecutors);
                }
            }
        }
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }

    public AppExecutors getAppExecutors() {
        return appExecutors;
    }

    private DataRepository(final Context context, final AppExecutors appExecutors) {
        database = Room.databaseBuilder(context, AppDatabase.class, "appDatabase")
                .fallbackToDestructiveMigration().build();
        this.appExecutors = appExecutors;
    }

    private static DataRepository instance;
    private final AppExecutors appExecutors;
    private final AppDatabase database;
}
