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

package me.francescotonini.beaconservice.models;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Represents a Beacon
 */
@Entity(tableName = "beacons")
public class Beacon {
    /**
     * Initializes a new instance of this class
     * @param address beacon's mac address
     * @param uuid beacon's uuid
     * @param rssi beacon's rssi
     * @param major beacon's major
     * @param minor beacon's minor
     */
    public Beacon(String address, String uuid, int rssi, int major, int minor) {
        this.address = address;
        this.uuid = uuid;
        this.rssi = rssi;
        this.major = major;
        this.minor = minor;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public String getUuid() {
        return uuid;
    }

    public int getRssi() {
        return rssi;
    }

    public int getMajor() {
        return major;
    }

    public int getMinor() {
        return minor;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String address;
    private final String uuid;
    private final int rssi;
    private final int major;
    private final int minor;
}
