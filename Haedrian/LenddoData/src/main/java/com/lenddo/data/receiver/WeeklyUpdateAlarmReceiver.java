package com.lenddo.data.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lenddo.data.DataManager;
import com.lenddo.data.utils.Utils;
import com.lenddo.sdk.listeners.UserListener;
import com.lenddo.sdk.models.User;


/**
 * Created by joseph on 7/1/14.
 */
public class WeeklyUpdateAlarmReceiver  extends BroadcastReceiver {
    private static final String TAG = WeeklyUpdateAlarmReceiver.class.getName();

    @Override
    public void onReceive(final Context context, Intent intent) {
        Utils.androidDataLog(context, TAG, "weekly alarm triggered.");
        Utils.recordLastAlarmTriggered(context, WeeklyUpdateAlarmReceiver.class.getName(), System.currentTimeMillis());
        if (!TextUtils.isEmpty(Utils.getUserId(context))) {
            if (Utils.isOnline(context)) {
                DataManager.getInstance().getMemberById(Utils.getUserId(context), new UserListener() {

                    @Override
                    public void onSuccess(User user) {
                        Utils.androidDataLog(context,TAG,"services online. transmitting payload");
                        transmitPayload(context);
                    }

                    @Override
                    public void onError(int statusCode, String rawResponse) {
                        Utils.androidDataLog(context,TAG,"services offline. reschedule");
                        rescheduleAlarm(context);
                    }
                });

            } else {
                Utils.androidDataLog(context,TAG,"no network. reschedule");
                rescheduleAlarm(context);
            }
        }
    }

    public static void transmitPayload(Context context) {
        Utils.sendCalendarEvents(context);
        Utils.sendContacts(context);
    }

    public void rescheduleAlarm(Context context) {
        Utils.scheduleContactsForImmediateUpload(context, true);
    }
}
