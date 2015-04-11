package com.lenddo.data.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Telephony;
import android.util.Log;

import com.lenddo.data.models.SMSMessage;
import com.lenddo.data.models.UserCallLog;

import java.util.ArrayList;
import java.util.List;

public enum SMSManager {

	INSTANCE;

    private static final int MAX_SMS_TO_RETRIEVE = 100;
    private static final String TAG = SMSManager.class.getName();

    public List<UserCallLog> getCallLogs(Context context) {
        ArrayList<UserCallLog> callLogs = new ArrayList<UserCallLog>();
        long last_id = Utils.getLastCallIdQueried(context);
        String[] projection = new String[] {
                CallLog.Calls._ID, CallLog.Calls.NUMBER, CallLog.Calls.TYPE, CallLog.Calls.DATE,

                CallLog.Calls.DURATION, CallLog.Calls.CACHED_NAME, CallLog.Calls.NEW, CallLog.Calls.CACHED_NUMBER_LABEL, CallLog.Calls.CACHED_NUMBER_TYPE};
        Cursor query = context.getContentResolver().query(
                CallLog.Calls.CONTENT_URI, projection, "_id > ?",
                new String[] {Long.toString(last_id)}, "_id DESC");
        while(query.moveToNext()) {
            UserCallLog callLog = new UserCallLog();
            int type = query.getInt(2);
            String id = query.getString(0);
            String number = query.getString(1);
            String date = query.getString(3);
            String duration = query.getString(4);
            String name = query.getString(5);
            int isNew = query.getInt(6);
            String label = query.getString(7);
            int numbertype = query.getInt(8);

            if (Long.parseLong(id) > last_id) {
                last_id = Long.parseLong(id);
            }

            callLog.setId(id);
            callLog.setNumber(number);
            callLog.setType(type);
            callLog.setDate(date);
            callLog.setDuration(Integer.parseInt(duration));
            callLog.setName(name);
            callLogs.add(callLog);
        }
        query.close();
        Utils.saveLastCallIdQueried(context, last_id);
        return callLogs;
    };

	public List<SMSMessage> getSMSMessages(Context context) {

        long last_id = Utils.getLastSmsIdQueried(context);

		List<SMSMessage> msgs = new ArrayList<SMSMessage>();
		Uri mSmsinboxQueryUri = Uri.parse("content://sms");
		Cursor cursor = context.getContentResolver().query(mSmsinboxQueryUri,
                new String[] { "_id", "thread_id", "address", "date", "body", "type" },
                "_id > ?",
				new String[] { Long.toString(last_id) }, "_id DESC");

		String[] columns = new String[] {  "_id", "address", "thread_id", "date", "body", "type" };
		String[] columnNames = cursor.getColumnNames();
		String all = "";
		for (String s : columnNames) {
			Log.i("SMSManager", s);
			all += s + " ,";
		}
		Log.i("SMSManager", all);
        int counter = 0;
		if (cursor.getCount() > 0 && (counter++ < MAX_SMS_TO_RETRIEVE)) {
			while (cursor.moveToNext()) {
				SMSMessage smsMsg = new SMSMessage();
				String address = null, displayName = null, date = null, msg = null, type = null, threadId = null;
				Uri photoUri = null;
                long id = cursor.getLong(cursor.getColumnIndex(columns[0]));
                if (id > last_id) {
                    last_id = id;
                }
				threadId = cursor.getString(cursor.getColumnIndex(columns[2]));
				type = cursor.getString(cursor.getColumnIndex(columns[5]));

				if (Integer.parseInt(type) == 1 || Integer.parseInt(type) == 2) {

					address = cursor.getString(cursor.getColumnIndex(columns[1]));
					date = cursor.getString(cursor.getColumnIndex(columns[3]));
					msg = cursor.getString(cursor.getColumnIndex(columns[4]));

                    smsMsg.setId(id);
					smsMsg.setDisplayName(displayName);
					smsMsg.setThreadId(threadId);
					smsMsg.setAddress(address);
					smsMsg.setPhotoUri(photoUri);
					smsMsg.setDate(date);
					smsMsg.setMsg(msg);
					smsMsg.setType(smsTypeToString(type));
					msgs.add(smsMsg);
					Log.e("SMS:", "address: " + address + " displayName: " + displayName + " thread_id:+" + threadId + " date " + date + " message: "+msg);
				}
			}
		}
        cursor.close();
        Utils.saveLastIdQueried(context, last_id);

		return msgs;
	}

    private String smsTypeToString(String typeStr) {
        int type = Integer.parseInt(typeStr);
        switch (type) {
            case Telephony.Sms.MESSAGE_TYPE_INBOX:
                return "inbox";
            case Telephony.Sms.MESSAGE_TYPE_OUTBOX:
                return "outbox";
            case Telephony.Sms.MESSAGE_TYPE_SENT:
                return "sent";
            case Telephony.Sms.MESSAGE_TYPE_FAILED:
                return "failed";
            case Telephony.Sms.MESSAGE_TYPE_DRAFT:
                return "draft";
        }
        return "unknown";
    }
}
