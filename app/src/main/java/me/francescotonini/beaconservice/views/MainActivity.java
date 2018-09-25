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

package me.francescotonini.beaconservice.views;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import me.francescotonini.beaconservice.Logger;
import me.francescotonini.beaconservice.R;
import me.francescotonini.beaconservice.databinding.ActivityMainBinding;
import me.francescotonini.beaconservice.services.BeaconService;
import me.francescotonini.beaconservice.viewmodels.BaseViewModel;

public class MainActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void setToolbar() {
        setSupportActionBar((Toolbar)binding.toolbar);
    }

    @Override
    protected BaseViewModel getViewModel() {
        return null;
    }

    @Override
    protected void setBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.startService.setOnClickListener(click -> startForegroundService());
        binding.stopService.setOnClickListener(click -> stopForegroundService());
    }

    private void startForegroundService() {
        Logger.d(MainActivity.class.getSimpleName(), "Starting foreground service");

        beaconService = new Intent(this, BeaconService.class);
        beaconService.setAction("start");
        startService(beaconService);
    }

    private void stopForegroundService() {
        Logger.d(MainActivity.class.getSimpleName(), "Stopping foreground service");

        beaconService = new Intent(this, BeaconService.class);
        beaconService.setAction("stop");
        startService(beaconService);
    }

    private Intent beaconService;
    private ActivityMainBinding binding;
}
