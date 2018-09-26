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
 * Rappresenta un beacon
 */
@Entity(tableName = "beacons")
public class Beacon {
    /**
     * Inizializza una nuova istanza di questa class
     * @param address indirizzo mac del beacon
     * @param uuid uuid del beacon
     * @param rssi rssi del beacon
     * @param major major
     * @param minor minor
     */
    public Beacon(String address, String uuid, int rssi, int major, int minor) {
        this.address = address;
        this.uuid = uuid;
        this.rssi = rssi;
        this.major = major;
        this.minor = minor;
    }

    /**
     * Restituisce l'id univoco di questa classe nel database
     * (detto in altre parole, questo campo rappresenta una chiave primaria auto incrementante.
     * Non è il massimo per grossi progetti ma in questo caso è perfetta)
     * @return id univoco di questa classe nel database
     */
    public int getId() {
        return id;
    }

    /**
     * Imposta l'id univoco di questa classe nel database
     * @param id id univoco di questa classe nel database
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce l'indirizzo del beacon
     * @return indirizzo del beacon
     */
    public String getAddress() {
        return address;
    }

    /**
     * Restituisce l'uuid del beacon
     * @return uuid del beacon
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Restituisce l'rssi del beacon
     * @return rssi del beacon
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Restituisce il major del beacon
     * @return major del beacon
     */
    public int getMajor() {
        return major;
    }

    /**
     * Restituisce il minor del beacon
     * @return
     */
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
