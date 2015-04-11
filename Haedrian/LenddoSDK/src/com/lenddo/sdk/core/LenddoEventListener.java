package com.lenddo.sdk.core;

import com.lenddo.sdk.models.FormDataCollector;

/**
 * Created by joseph on 8/19/14.
 */
public interface LenddoEventListener {
    public boolean onButtonClicked(FormDataCollector collector);

    public void onAuthorizeComplete(FormDataCollector collector);

    public void onAuthorizeCanceled(FormDataCollector collector);
}
