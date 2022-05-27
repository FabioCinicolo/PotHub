package com.cindea.pothub.map;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cindea.pothub.R;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements SensorEventListener {

    double previous_acceleration = -99999999;
    double threshold = 0;
    private SensorManager sensor_manager;

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if(locationResult!=null && locationResult.getLastLocation()!=null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longitude = locationResult.getLastLocation().getLongitude();

                sendMessageToActivity(latitude,longitude);

                Log.d("TAG", latitude + " " + longitude);

            }
        }
    };


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("not yet implemented");
    }


    private void startService() {

        String channelId= "location_notiication_channel";
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


    private void stopLocationService() {

        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();
    }

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

                }
                else if(action.equals(Constants.ACTION_STP_LOCATION_SERVICE)) stopLocationService();

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

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            acceleration_x = event.values[0];
            acceleration_y = event.values[1];
            acceleration_z = event.values[2];
            current_acceleration = Math.sqrt(acceleration_x*acceleration_x + acceleration_y*acceleration_y + acceleration_z*acceleration_z);

            if(current_acceleration - previous_acceleration >= threshold)
            {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Current acceleration is: "+current_acceleration+" Previous acceleration is: "+previous_acceleration,
                        Toast.LENGTH_SHORT);

                toast.show();
            }

            previous_acceleration = current_acceleration;

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

