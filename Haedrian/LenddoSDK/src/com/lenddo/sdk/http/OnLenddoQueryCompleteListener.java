package com.lenddo.sdk.http;

/**
 * Created by joseph on 8/11/14.
 */
public interface OnLenddoQueryCompleteListener {
    void onComplete(String rawResponse);

    void onError(int statusCode, String rawResponse);
}
