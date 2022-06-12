package com.cindea.pothub;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;

public class GPSCheck extends BroadcastReceiver {

    private final LocationCallBack locationCallBack;

    public GPSCheck(LocationCallBack iLocationCallBack) {
        this.locationCallBack = iLocationCallBack;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            locationCallBack.turnedOff();
    }

    public interface LocationCallBack {
        void turnedOff();
    }
}