package com.lenddo.data.listeners;

import android.util.Log;

import com.lenddo.data.utils.Utils;

import org.json.JSONObject;

/**
 * Created by joseph on 9/15/14.
 */
public interface OnLocationSubmittedListener {

    public void onError();

    public void onSuccess(JSONObject response);
}
