package com.lenddo.sdk.dialogs;

/**
 * Created by joseph on 12/11/14.
 */
public interface AuthorizeCallbackCollector {
    void onCallbackInitiated(String url);

    void onCancel();
}
