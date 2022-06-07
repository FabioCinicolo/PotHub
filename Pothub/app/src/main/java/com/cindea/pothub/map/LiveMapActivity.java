package com.cindea.pothub.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cindea.pothub.GPSCheck;
import com.cindea.pothub.R;
import com.cindea.pothub.databinding.ActivityLiveMapBinding;
import com.cindea.pothub.networkutil.NetworkUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

public class LiveMapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap map;
    private ActivityLiveMapBinding binding;
    private Button button_exit;
    private GPSCheck gps_receiver;
    private BroadcastReceiver mMessageReceiver;
    private BroadcastReceiver networkReceiver;
    private BroadcastReceiver reportedPotholesReceiver;
    double latitude, longitude;

    private static final int REQUEST_CODE_LOCATION_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLiveMapBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        networkReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = NetworkUtil.getConnectivityStatusString(context);
                switch (status) {

                    case 0:
                        stopLocationService();
                        break;
                    case 1:
                        stopLocationService();
                        startLocationService();
                        break;

                }
            }
        };


        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(LiveMapActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSION);

        }
        else {

            registerReceiver(networkReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }

        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latitude = intent.getDoubleExtra("latitude", 0);
                longitude = intent.getDoubleExtra("longitude", 0);
                if(latitude!=0 && longitude!=0) {
                    LatLng latlng = new LatLng(latitude, longitude);
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(
                            latlng, 17f);
                    map.animateCamera(cameraUpdate);
                }

            }
        };

        reportedPotholesReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                latitude = intent.getDoubleExtra("latitude", 0);
                longitude = intent.getDoubleExtra("longitude", 0);
                int intensity = intent.getIntExtra("intensity", 1);

                LatLng position = new LatLng(latitude, longitude);

                if(latitude!=0 && longitude!=0) {

                    int customMarker = 1;

                    switch (intensity) {

                        case 1:
                            customMarker = R.drawable.ic_green_alert;
                            break;
                        case 2:
                            customMarker = R.drawable.ic_yellow_alert;
                            break;
                        case 3:
                            customMarker = R.drawable.ic_red_alert;
                            break;
                    }

                    map.addMarker(new MarkerOptions().position(position)
                            .icon(BitmapFromVector(getApplicationContext(), customMarker)));
                }

            }
        };
        button_exit = findViewById(R.id.liveMap_exit);
        listeners();
        checkGPS();
    }



    private void listeners() {

        button_exit.setOnClickListener(v -> {finish();});

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(gps_receiver);
        unregisterReceiver(networkReceiver);
        stopLocationService();
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(reportedPotholesReceiver, new IntentFilter("ReportedLatLng"));
        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter("GPSLocationUpdates"));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));
        map.setMyLocationEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == REQUEST_CODE_LOCATION_PERMISSION && grantResults.length > 0) {

            startLocationService();

        }else Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();

    }

    private boolean isLocationServiceRunning() {

        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if(activityManager!=null) {

            for(ActivityManager.RunningServiceInfo service : activityManager.getRunningServices(Integer.MAX_VALUE)) {

                if(LocationService.class.getName().equals(service.service.getClassName())) {

                    if(service.foreground) return true;

                }

            }
            return false;

        }

        return false;

    }

    private void startLocationService() {

        if(!isLocationServiceRunning()) {

            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_START_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service started", Toast.LENGTH_LONG).show();

        }

    }

    private void stopLocationService() {

        if(isLocationServiceRunning()) {

            Intent intent = new Intent(getApplicationContext(), LocationService.class);
            intent.setAction(Constants.ACTION_STP_LOCATION_SERVICE);
            startService(intent);
            Toast.makeText(this, "Location service stopped", Toast.LENGTH_LONG).show();

        }

    }

    private void checkGPS() {

        registerReceiver(gps_receiver = new GPSCheck(() -> finish()), new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    private BitmapDescriptor BitmapFromVector(Context context, int drawable) {
        // below line is use to generate a drawable.
        Drawable vectorDrawable = ContextCompat.getDrawable(context, drawable);

        // below line is use to set bounds to our vector drawable.
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());

        // below line is use to create a bitmap for our
        // drawable which we have added.
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);

        // below line is use to add bitmap in our canvas.
        Canvas canvas = new Canvas(bitmap);

        // below line is use to draw our
        // vector drawable in canvas.
        vectorDrawable.draw(canvas);

        // after generating our bitmap we are returning our bitmap.
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
