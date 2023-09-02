package com.drd.drdtrackingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            // Start the AutoStartManagerService on device boot
            Intent serviceIntent = new Intent(context, AutoStartManagerService.class);
            context.startService(serviceIntent);
        }
    }
}

