package com.cindea.pothub.map;

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
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cindea.pothub.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class LocationService extends Service implements SensorEventListener {

    double previous_acceleration = -99999999;
    double previous_latitude, previous_longitude;
    double threshold = 10;
    private SensorManager sensor_manager;
    double latitude, longitude;
    boolean is_first_pothole = true;

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
        throw new UnsupportedOperationException("not yet implemented");
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
                    startService();
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
                    connect();

                }
                else if(action.equals(Constants.ACTION_STP_LOCATION_SERVICE)) stopService();

            }

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessageToActivity(double latitude, double longitude) {
        Intent intent = new Intent("GPSLocationUpdates");
        // You can also include some extra data.
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
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
                    if(distance > 75)
                        reportPotHole();
                }
                else{
                    reportPotHole();
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

    public void reportPotHole(){

    }

    public void connect(){
        Log.e("ciao","ciao");
        Thread t1 = new Thread(() -> {
            Log.e("ciao1","ciao1");
            try {
                InetAddress server_address = InetAddress.getByName("20.126.123.213");
                Socket socket = new Socket(server_address,12345);

                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                out.println("Ciao come stai?");
                out.close();

            } catch (UnknownHostException e) {
                Log.e("diocane",e.getMessage());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        t1.run();
    }

}

