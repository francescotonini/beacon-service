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
import java.util.Date;
import java.util.Locale;

/**
 * Rappresenta un access point
 */
@Entity(tableName = "aps")
public class AP {
    /**
     * Inizializza una nuova istanza di questa classe
     * @param bssid bssid
     * @param level level
     * @param timestamp timestamp
     */
    public AP(String bssid, int level, String timestamp) {
        this.bssid = bssid;
        this.level = level;
        this.type = "WIFI";
        this.timestamp = timestamp;
    }

    public int getId() {
        return id;
    }

    public String getBssid() {
        return bssid;
    }

    public int getLevel() {
        return level;
    }

    public String getType() {return type;}

    public void setId(int id) {
        this.id = id;
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

    /**
     * Imposta il tipo di AP
     * @param type tipo di AP
     */
    public void setType(String type) {
        this.type = type;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String bssid;
    private final int level;
    private String timestamp;
    private String type;
}
