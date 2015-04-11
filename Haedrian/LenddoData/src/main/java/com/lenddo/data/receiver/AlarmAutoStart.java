package com.lenddo.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lenddo.data.DataManager;
import com.lenddo.data.utils.Utils;


/**
 * Created by joseph on 4/29/14.
 */
public class AlarmAutoStart extends BroadcastReceiver {
    private static final String TAG = AlarmAutoStart.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.androidDataLog(context, TAG, "setting alarm after fresh boot.");
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            DataManager.configureAlarm(context);
        } else {
            Utils.androidDataLog(context, TAG, "no user logged in.");
        }
    }
}
