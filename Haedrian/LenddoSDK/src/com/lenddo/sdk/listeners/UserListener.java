package com.lenddo.sdk.listeners;

import com.lenddo.sdk.models.User;

/**
 * Created by joseph on 9/15/14.
 */
public interface UserListener {
    void onSuccess(User user);
    void onError(int statusCode, String rawResponse);
}
