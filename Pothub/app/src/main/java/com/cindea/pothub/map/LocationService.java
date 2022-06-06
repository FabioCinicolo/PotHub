package com.cindea.pothub.map;

import static com.cindea.pothub.map.Constants.CLOSE_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.OPEN_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.REPORT_POTHOLE;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cindea.pothub.CustomThread;
import com.cindea.pothub.R;
import com.cindea.pothub.authentication.views.fragments.OnHandlerReady;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.entities.Pothole;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class LocationService extends Service implements SensorEventListener, OnHandlerReady {

    double previous_acceleration = -99999999;
    double previous_latitude, previous_longitude;
    double threshold = 10;
    private SensorManager sensor_manager;
    double latitude, longitude;
    boolean is_first_pothole = true;

    private CustomThread thread;
    private Handler handler;

    private Geocoder geocoder;
    List<Address> addresses;


    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult!=null && locationResult.getLastLocation()!=null) {
                latitude = locationResult.getLastLocation().getLatitude();
                longitude = locationResult.getLastLocation().getLongitude();
                sendMessageToActivity(latitude,longitude);
            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //Inserisco in coda la task per chiudere la connessione con il server
        handler.sendEmptyMessage(CLOSE_CONNECTION_WITH_SERVER);
        //Chiudo il thread
        thread.quitSafely();
    }

    private void startService() {

        String channelId= "location_notification_channel";
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Running");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if(notificationManager!=null && notificationManager.getNotificationChannel(channelId) == null) {

                NotificationChannel notificationChannel = new NotificationChannel(channelId, "Location Service", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("This channel is used by location service");
                notificationManager.createNotificationChannel(notificationChannel);

            }

        }
        startForeground(Constants.LOCATION_SERVICE_ID, builder.build());

        geocoder = new Geocoder(this, Locale.getDefault());
        Log.e("LOG0", "LOG0");
        startBackgroundThread();
    }


    private void stopService() {

        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(intent != null) {

            String action = intent.getAction();
            if (action != null) {

                if(action.equals(Constants.ACTION_START_LOCATION_SERVICE)) {
                    //Get Location Updates
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(4000);
                    locationRequest.setFastestInterval(2000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationServices.getFusedLocationProviderClient(this)
                            .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
                    //Get Sensor updates
                    sensor_manager = (SensorManager) getSystemService(SENSOR_SERVICE);
                    sensor_manager.registerListener(this, sensor_manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                            SensorManager.SENSOR_DELAY_NORMAL);

                    startService();
                }
                else if(action.equals(Constants.ACTION_STP_LOCATION_SERVICE)) stopService();

            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double acceleration_x, acceleration_y, acceleration_z;
        double current_acceleration;
        double current_latitude = latitude, current_longitude = longitude;
        double distance;

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration_x = event.values[0];
            acceleration_y = event.values[1];
            acceleration_z = event.values[2];
            current_acceleration = Math.sqrt(acceleration_x * acceleration_x + acceleration_y * acceleration_y + acceleration_z * acceleration_z);

            //SE PRENDO UNA BUCA
            if (current_acceleration - previous_acceleration >= threshold) {

                if(!is_first_pothole)
                {
                    //Se la distanza tra l ultima buca e la recente è abbastanza grande allora la segnaliamo
                    distance = SphericalUtil.computeDistanceBetween(new LatLng(previous_latitude, previous_longitude), new LatLng(current_latitude, current_longitude));
                    if(distance > 75){
                        //INSERT GEOCODING + CALCULATE INTENSITY
                        try {
                            //TODO: Prendere indirizzo (Come stringa separata da virgola <Pozzuoli#IT>)
                            addresses = geocoder.getFromLocation(latitude, longitude, 1);
                            String country_code = addresses.get(0).getCountryCode();
                            String name2 = addresses.get(0).getLocality();
                            name2 = name2.replace('\'', ' ');
                            Pothole pothole = new Pothole(current_latitude, current_longitude, name2+"#"+country_code, SigninFragment.username, 2, "as");
                            reportPotHole(pothole);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else if(current_latitude!=0){
                    //INSERT GEOCODING + CALCULATE INTENSITY
                    //TODO: Prendere indirizzo (Come stringa separata da virgola <Pozzuoli#IT>)
                    try {
                        addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String country_code = addresses.get(0).getCountryCode();
                        String name2 = addresses.get(0).getLocality();
                        name2 = name2.replace('\'', ' ');
                        Pothole pothole = new Pothole(current_latitude, current_longitude, name2+"#"+country_code, SigninFragment.username, 2, "asd");
                        reportPotHole(pothole);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    is_first_pothole = false;
                }
                previous_latitude = current_latitude;
                previous_longitude = current_longitude;
            }

            previous_acceleration = current_acceleration;

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void reportPotHole(Pothole pothole){
        Message message;
        message = Message.obtain();
        message.obj = pothole;
        message.what = REPORT_POTHOLE;
        handler.sendMessage(message);
    }

    public void startBackgroundThread(){
        thread = new CustomThread(this);
        thread.start();

    }

    @Override
    public void onSuccess(Handler handler) {
        this.handler = handler;
        this.handler.sendEmptyMessage(OPEN_CONNECTION_WITH_SERVER);
    }

    private void sendMessageToActivity(double latitude, double longitude) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
    }

}

