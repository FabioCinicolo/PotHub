package com.cindea.pothub.home.views;

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
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.cindea.pothub.R;
import com.cindea.pothub.map.LiveMapActivity;

public class MiddleHomeFragment extends Fragment {

    private View view;
    private Button button_start;

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

            if(isGPSEnabled()) {

                if(isConnected()) {

                    getActivity().startActivity(
                            new Intent(getActivity(), LiveMapActivity.class));

                }
                else
                    Toast.makeText(getContext(), "Connection not available", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getContext(), "GPS is not enabled", Toast.LENGTH_SHORT).show();

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
            Thread thread = new Thread(() -> {
                try {
                    String command = "ping -c 1 google.com";
                    is_connected[0] =  (Runtime.getRuntime().exec(command).waitFor() == 0);
                } catch (Exception e) {
                    is_connected[0] = false;
                }
                synchronized(is_connected) {
                    is_connected.notifyAll();
                }
            });
            thread.start();
            synchronized (is_connected){
                try {
                    is_connected.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        return is_connected[0];
    }

}