package com.cindea.pothub.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.cindea.pothub.R;
import com.cindea.pothub.map.LiveMapActivity;

public class MiddleHomeFragment extends Fragment {

    private View view;
    private Button button_start;

    public boolean s;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_middle_home, container, false);
        button_start = view.findViewById(R.id.middleHome_start);
        listeners();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void listeners() {

        button_start.setOnClickListener(v -> {

            if(isGPSEnabled() && isConnected())
                getActivity().startActivity(
                    new Intent(getActivity(), LiveMapActivity.class));
                     });

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public boolean isGPSEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return GpsStatus;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    public boolean isConnected() {
        final boolean[] is_connected = {false};
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String command = "ping -c 1 google.com";
                        Log.e("FALSE1", "FALSE1");
                        is_connected[0] =  (Runtime.getRuntime().exec(command).waitFor() == 0);
                        Log.e("INFO", String.valueOf(is_connected[0]));
                        s = is_connected[0];
                    } catch (Exception e) {
                        Log.e("FALSE2", "FALSE2");
                        is_connected[0] = false;
                    }
                }
            });
            thread.start();
        }else {
            Log.e("FALSE", "FALSE");
            return false;
        }

        synchronized (this){
            try {
                wait(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return s;

    }

}