package com.lenddo.sdk.listeners;

/**
 * Created by joseph on 1/8/15.
 */
public interface RawEventListener {
    void onAuthorizeComplete(String url);

    void onAuthorizeCanceled();
}
