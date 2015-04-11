package com.lenddo.sdk.core;

import android.view.View;

import com.lenddo.sdk.models.FormDataCollector;

/**
 * Created by joseph on 8/27/14.
 */
public interface FieldPreProcessListener {
    public boolean process(FormDataCollector collector, View v);
}
