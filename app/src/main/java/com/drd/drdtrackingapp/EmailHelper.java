package com.drd.drdtrackingapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

public class EmailHelper {
    public static String getUserEmail(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.google");

        if (accounts.length > 0) {
            // Assuming the first account is the primary Google account on the device
            return accounts[0].name;
        } else {
            return null; // No Google account found
        }
    }
}
