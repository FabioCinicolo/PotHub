package com.cindea.pothub.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.cindea.pothub.R;
import com.cindea.pothub.map.LiveMapActivity;

public class MiddleHomeFragment extends Fragment {

    private View view;
    private Button button_start;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.fragment_middle_home, container, false);
        button_start = view.findViewById(R.id.middleHome_start);
        listeners();

        return view;
    }

    private void listeners() {

        button_start.setOnClickListener(v -> {

            //TODO: inserire anche isconnected nell'if per farlo funzionare
            if(isGPSEnabled()) {

                getActivity().startActivity(
                        new Intent(getActivity(), LiveMapActivity.class));

            }else Log.e("TAG", "CIAO");



        });

    }

    public boolean isGPSEnabled() {

        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        boolean GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GpsStatus;
    }

    @SuppressLint("MissingPermission")
    public boolean isConnected() {

        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if(cm.getActiveNetworkInfo() != null) {

            final boolean[] is_connected = {true};

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String command = "ping -c 1 google.com";
                        Log.e("FALSE1", "FALSE1");
                        is_connected[0] =  (Runtime.getRuntime().exec(command).waitFor() == 0);
                        Log.e("INFO", String.valueOf(is_connected[0]));
                    } catch (Exception e) {
                        Log.e("FALSE2", "FALSE2");
                        is_connected[0] = false;
                    }
                }
            });
            thread.run();


        }else {

            Log.e("FALSE", "FALSE");
            return false;

        }
        return false;

    }

}