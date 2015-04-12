package com.lenddo.sdk.widget;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import com.lenddo.sdk.R;
import com.lenddo.sdk.models.FormDataCollector;
import com.lenddo.sdk.utils.UIHelper;
import com.lenddo.sdk.utils.Utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Created by joseph on 8/18/14.
 */
public class LenddoButton extends Button implements View.OnClickListener {

    private static final String TAG = LenddoButton.class.getName();

    private UIHelper uiHelper;

    public LenddoButton(Context context) {
        super(context);
        initStyle(null);
    }

    public LenddoButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStyle(attrs);
    }

    public LenddoButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initStyle(attrs);
    }

    protected void initStyle(AttributeSet attrs) {
        //setBackgroundResource(Color.parseColor("#00BE90"));
        setGravity(Gravity.CENTER);
        setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.img_logo_button_bar), null, null, null);
        setText(getResources().getString(R.string.verify_with_haedrian));
        setTextColor(getResources().getColor(R.color.lenddo_white));
        if (!isInEditMode()) {
            setOnClickListener(this);
        }
    }

    public void setUiHelper(UIHelper uiHelper) {
        this.uiHelper = uiHelper;
    }

    @Override
    public void onClick(View view) {
        if (uiHelper.getEventListener()!=null) {
            Activity activity = (Activity) getContext();
            ApplicationInfo ai = null;
            FormDataCollector collector = uiHelper.getCollector();
            try {
                ai = activity.getPackageManager().getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);

                Bundle bundle = ai.metaData;
                String myApiKey = null;

                if (bundle!=null) {
                    myApiKey = bundle.getString("lenddoAppId", null);
                }

                if (bundle == null || myApiKey == null) {
                    Log.e(TAG, "meta tag <meta-data android:name=\"lenddoAppId\" " +
                            " android:value=\"your_lenddo_app_id\" /> is missing from AndroidManifest.xml");
                } else {

                    collector.setPartnerScriptId(myApiKey);
                    if (uiHelper.getEventListener().onButtonClicked(collector)) {
                        if (Utils.isLenddoInstalled(getContext())) {
                            Intent intent = new Intent(Intent.ACTION_SEND, Uri.parse("lenddo://authorize"));
                            intent.putExtra("partnerScriptId", collector.getPartnerScriptId());
                            intent.putExtra("userId", URLEncoder.encode(collector.getUserId(), "utf-8"));
                            intent.putExtra("formData", URLEncoder.encode(collector.toJson(),"utf-8"));
                            intent.putExtra("verificationData",  URLEncoder.encode(collector.toVerificationJson(),"utf-8"));
                            intent.putExtra("packageName", getContext().getApplicationContext().getPackageName());
                            ((Activity) getContext()).startActivityForResult(intent, Utils.REQUEST_AUTHORIZE);
                        } else {
                            Utils.showAuthorizeDialog(activity, collector, uiHelper.getEventListener());
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }


}
