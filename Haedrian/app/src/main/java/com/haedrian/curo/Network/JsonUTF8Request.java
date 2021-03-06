package com.haedrian.curo.Network;

/**
 * Created by audakel on 7/1/15.
 */
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.haedrian.curo.Application.ApplicationController;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

public class JsonUTF8Request extends JsonRequest<JSONObject> {
    public JsonUTF8Request(int method, String url, JSONObject jsonRequest,
                           Listener<JSONObject> listener, ErrorListener errorListener) {
        super(method, url, (jsonRequest == null) ? null : jsonRequest.toString(), listener,
                errorListener);
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            // solution 1:
            String jsonString = new String(response.data, "UTF-8");
            // solution 2:
//            response.headers.put(HTTP.CONTENT_TYPE,
//                    response.headers.get("content-type"));
//            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            //
            return Response.success(new JSONObject(jsonString),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }
    @Override
    public HashMap<String, String> getHeaders() {
        String token = ApplicationController.getToken();
        HashMap<String, String> params = new HashMap<>();
        params.put("Authorization", "Token " + token);
        params.put("Content-Type", "application/json;charset=UTF-8");
        params.put("Accept", "application/json");
        return params;
    }
}
