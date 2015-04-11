package com.lenddo.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;


import com.lenddo.data.DataManager;
import com.lenddo.data.utils.Utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by joseph on 6/16/14.
 */
public class ConnectivityChanged extends BroadcastReceiver {

    private static final String TAG = ConnectivityChanged.class.getName();

    class ResendPendingUpload extends TimerTask {

        private final Context context;

        public ResendPendingUpload(Context context) {
            this.context = context;
        }

        @Override
        public void run() {
            Utils.androidDataLog(context, TAG, "sending after 10 second delay");
            Utils.uploadPendingData(context);
        }

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.androidDataLog(context, TAG, "Connectivity status changed alarm triggered.");
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            ConnectivityManager cm =
                    (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

            Utils.androidDataLog(context, TAG, "checking network state.");
            if (activeNetwork != null) {
                if (activeNetwork.isConnected()) {
                    Utils.androidDataLog(context, TAG, "network connected. uploading data.");
                    Utils.uploadPendingData(context);
                } else if (activeNetwork.isConnectedOrConnecting()) {
                    Utils.androidDataLog(context, TAG, "network still tring to connect. delay for 10 seconds");
                    Timer timer = new Timer();
                    timer.schedule(new ResendPendingUpload(context), 10000);
                }
            }

            DataManager.checkAlarm(context);
        } else {
            Utils.androidDataLog(context, TAG, "no user logged in. aborting");
        }
    }
}
