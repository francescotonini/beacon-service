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

import java.util.Calendar;

/**
 * Rappresenta un access point
 */
@Entity(tableName = "aps")
public class AP {
    /**
     * Inizializza una nuova istanza di questa classe
     * @param ssid ssid
     * @param bssid bssid
     * @param capabilities capabilities
     * @param freq0 freq0
     * @param freq1 freq1
     * @param channelWidth channelWidth
     * @param frequency frequency
     * @param level level
     */
    public AP(String ssid, String bssid, String capabilities, int freq0, int freq1, int channelWidth, int frequency, int level) {
        this.ssid = ssid;
        this.bssid = bssid;
        this.capabilities = capabilities;
        this.freq0 = freq0;
        this.freq1 = freq1;
        this.channelWidth = channelWidth;
        this.frequency = frequency;
        this.level = level;
        this.timestamp = Calendar.getInstance().getTimeInMillis();
    }

    public int getId() {
        return id;
    }

    public String getSsid() {
        return ssid;
    }

    public String getBssid() {
        return bssid;
    }

    public String getCapabilities() {
        return capabilities;
    }

    public int getFreq0() {
        return freq0;
    }

    public int getFreq1() {
        return freq1;
    }

    public int getChannelWidth() {
        return channelWidth;
    }

    public int getFrequency() {
        return frequency;
    }

    public int getLevel() {
        return level;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * Restituisce il timestamp di creazione dell'oggetto
     * @return timestamp di creazione dell'oggetto
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Imposta il timestamp di creazione dell'oggetto
     * @param timestamp timestamp di creazione dell'oggetto
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;
    private final String ssid;
    private final String bssid;
    private final String capabilities;
    private final int freq0;
    private final int freq1;
    private final int channelWidth;
    private final int frequency;
    private final int level;
    private long timestamp;
}
