package com.lenddo.sdk.core;

import com.lenddo.sdk.models.User;

/**
 * Created by joseph on 8/11/14.
 */
public interface UserResponseListener {
    void onSuccess(User user);

    void onError(int statusCode, String rawResponse);
}
