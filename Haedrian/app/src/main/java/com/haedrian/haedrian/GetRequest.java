package com.haedrian.haedrian;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;

/**
 * Created by James on 3/21/2015.
 */
public class GetRequest {
//    // pass second argument as "null" for GET requests
//    JsonObjectRequest req = new JsonObjectRequest(ApplicationConstants.serverUrl, null,
//            new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    try {
//                        VolleyLog.v("Response:%n %s", response.toString(4));
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new Response.ErrorListener() {
//        @Override
//        public void onErrorResponse(VolleyError error) {
//            VolleyLog.e("Error: ", error.getMessage());
//        }
//    });
//
//// add the request object to the queue to be executed
//    ApplicationController.getInstance().addToRequestQueue(req);
}
