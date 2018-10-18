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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Rappresenta un beacon
 */
@Entity(tableName = "beacons")
public class Beacon {
    /**
     * Inizializza una nuova istanza di questa class
     * @param address indirizzo mac del beacon
     * @param id1 id1 del beacon
     * @param id2 id2 del beacon
     * @param id3 id3 del beacon
     * @param rssi rssi del beacon
     */
    public Beacon(String address, String id1, String id2, String id3, int rssi) {
        this.address = address;
        this.id1 = id1;
        this.id2 = id2;
        this.id3 = id3;
        this.rssi = rssi;
        this.timestamp = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss.SSS",
                Locale.getDefault()).format(new Date());
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
     * Restituisce l'id1 del beacon
     * @return id1 del beacon
     */
    public String getId1() {
        return id1;
    }

    /**
     * Restituisce l'id2 del beacon
     * @return id2 del beacon
     */
    public String getId2() {
        return id2;
    }

    /**
     * Restituisce l'id3 del beacon
     * @return id3 del beacon
     */
    public String getId3() {
        return id3;
    }

    /**
     * Restituisce l'rssi del beacon
     * @return rssi del beacon
     */
    public int getRssi() {
        return rssi;
    }

    /**
     * Restituisce il timestamp di creazione dell'oggetto
     * @return timestamp di creazione dell'oggetto
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Imposta il timestamp di creazione dell'oggetto
     * @param timestamp timestamp di creazione dell'oggetto
     */
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {return TYPE;}

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String address;
    private String id1;
    private String id2;
    private String id3;
    private int rssi;
    private String timestamp;
    private final static String TYPE = "BLE";
}
