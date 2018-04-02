package com.example.boyanyotov.songle.Networks;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Boyan Yotov on 11/3/2017.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {

        String status = NetworkUtil.getInstance().getConnectivityStatusString(context);

        Toast.makeText(context, status, Toast.LENGTH_LONG).show();
    }
}
