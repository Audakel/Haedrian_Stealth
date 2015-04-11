package com.lenddo.sdk.http;

import com.lenddo.sdk.core.LenddoHttpOptions;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by joseph on 8/11/14.
 */
public interface LenddoHttpInterface {

    public void query(String url, HashMap<String, String> headers, HashMap<String, String> params,
                      String rawRequest,
                      int method, LenddoHttpOptions options, OnLenddoQueryCompleteListener listener);

}
