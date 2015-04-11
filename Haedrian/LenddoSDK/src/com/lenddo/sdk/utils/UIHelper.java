package com.lenddo.sdk.utils;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.util.Log;

import com.lenddo.sdk.core.LenddoEventListener;
import com.lenddo.sdk.dialogs.WebAuthorizeFragment;
import com.lenddo.sdk.models.AuthorizationStatus;
import com.lenddo.sdk.models.FormDataCollector;
import com.lenddo.sdk.widget.LenddoButton;

/**
 * Created by joseph on 9/9/14.
 */
public class UIHelper {

    private static final String TAG = UIHelper.class.getName();
    private final Activity activity;
    FormDataCollector collector = new FormDataCollector();

    public LenddoEventListener getEventListener() {
        return eventListener;
    }

    private LenddoEventListener eventListener;

    public UIHelper(Activity activity, LenddoEventListener eventListener) {
        this.eventListener = eventListener;
        this.activity = activity;
    }

    public FormDataCollector getCollector() {
        return collector;
    }

    public void setLenddoEventListener(LenddoEventListener listener) {
        this.eventListener = listener;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult " + requestCode + " resultCode: " + resultCode);
        if (requestCode == Utils.REQUEST_AUTHORIZE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                AuthorizationStatus authorizationStatus = new AuthorizationStatus();
                authorizationStatus.setVerification(data.getStringExtra("verification"));
                authorizationStatus.setStatus(data.getStringExtra("status"));
                authorizationStatus.setTransId(data.getStringExtra("transid"));
                authorizationStatus.setUserId(data.getStringExtra("userid"));
                authorizationStatus.setPsid(data.getStringExtra("psid"));
                collector.setAuthorizationStatus(authorizationStatus);
                eventListener.onAuthorizeComplete(collector);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                eventListener.onAuthorizeCanceled(collector);
            }
        }
    }

    public boolean onBackPressed() {
        FragmentManager ft = activity.getFragmentManager();
        WebAuthorizeFragment fragment = (WebAuthorizeFragment)ft.findFragmentByTag("authorizeLenddoDialog");
        if (fragment != null) {
            fragment.onBackPressed();
            return false;
        }
        return true;
    }
}
