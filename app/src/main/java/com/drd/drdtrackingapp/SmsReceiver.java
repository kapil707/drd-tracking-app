package com.drd.drdtrackingapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = "Bg-service";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, MyBackgroundService.class);
        context.startService(serviceIntent);
        if (intent.getAction() != null && intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu);
                        String senderNumber = smsMessage.getOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        Log.d(TAG, "Received SMS from: " + senderNumber);
                        Log.d(TAG, "Message: " + messageBody);

                        // You can handle the received SMS here
                    }
                }
            }
        }
    }
}

