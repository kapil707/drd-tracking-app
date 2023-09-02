package com.drd.drdtrackingapp;

import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

            String id = remoteMessage.getData().get("id");
            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("message");

            Log.e("Bg-service", "MyFirebaseMessagingService - "+title);
            Log.e("Bg-service", "MyFirebaseMessagingService - "+message);
            // Create and show a notification
            // ...

        /*Intent serviceIntent = new Intent(this, MyBackgroundService.class);
        startService(serviceIntent);

        Intent serviceIntent1 = new Intent(this, LocationService.class);
        startService(serviceIntent1);

        Intent serviceIntent2 = new Intent(this, GPSTrackerService.class);
        startService(serviceIntent2);*/

        //startService(new Intent(this, BackgroundLocationUpdateService.class));

        Intent serviceIntent = new Intent(this, BackgroundLocationUpdateService.class);
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        }*/
        startService(serviceIntent);
    }
}
