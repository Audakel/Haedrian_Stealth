package com.lenddo.sdk.http;

import android.os.AsyncTask;
import android.util.Log;

import com.lenddo.sdk.core.LenddoConstants;
import com.lenddo.sdk.core.LenddoHttpOptions;
import com.lenddo.sdk.utils.Utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joseph on 8/11/14.
 */
public class LenddoBasicHttp implements LenddoHttpInterface {

    public static class LenddoRequestTask extends AsyncTask<Void, Void, LenddoHttpResponseHandler> {

        private static final String TAG = LenddoRequestTask.class.getName();
        private final HashMap<String, String> params;
        private final String url;
        private final LenddoHttpOptions options;
        private final HashMap<String, String> headers;
        private final OnLenddoQueryCompleteListener listener;
        private final String rawPostData;
        int method;

        public LenddoRequestTask(int method, String url, HashMap<String,String> headers,
                                 HashMap<String, String> params,
                                 String rawPostData,
                                 OnLenddoQueryCompleteListener listener, LenddoHttpOptions options) {
            this.method = method;
            this.headers = headers;
            this.params = params;
            this.rawPostData = rawPostData;
            this.url = url;

            if (options == null) {
                this.options = new LenddoHttpOptions();
            } else {
                this.options = options;
            }
            this.listener = listener;
        }

        @Override
        protected LenddoHttpResponseHandler doInBackground(Void... voids) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpUriRequest request = null;
            BasicHttpParams basicParams = new BasicHttpParams();
            if (method == LenddoConstants.HTTP_GET) {
                String fullurl = url;
                if (params!=null) {
                    ArrayList<String> urlparams = Utils.createQueryString(params);
                    String stringtype[] = new String[0];
                    fullurl = url + "?"
                            + StringUtils.join(urlparams.toArray(stringtype), '&');
                }
                request = new HttpGet(fullurl);
                Log.v(this.getClass().toString(), "Request: GET " + fullurl);

            } else {
                request = new HttpPost(url);
                HttpPost postRequest = (HttpPost) request;

                if (options!=null && options.usePostData) {

                    List<NameValuePair> nvps = new ArrayList<NameValuePair>();
                    for (String key : params.keySet()) {
                        String value = params.get(key);
                        if (value != null) {
                            nvps.add(new BasicNameValuePair(key, value));
                            Log.v(this.getClass().toString(), key + " = " + value);
                        }
                    }

                    try {
                        postRequest
                                .setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
                    } catch (UnsupportedEncodingException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                } else {
                    if (rawPostData!=null) {
                        try {
                            Log.d(TAG,"Sending raw post data = " + rawPostData);
                            postRequest.setEntity(new StringEntity(rawPostData, HTTP.UTF_8));
                            postRequest.setHeader("Content-Type","application/json; charset=utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    } else {
                        if (params!=null) {
                            String queryString = StringUtils.join(Utils.createQueryString(params), "&");
                            try {
                                postRequest.setEntity(new StringEntity(queryString, HTTP.UTF_8));
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                Log.d(this.getClass().toString(), "Request: POST " + url);
            }

            if (headers!=null) {
                for (String key : headers.keySet()) {
                    Log.d(TAG, "Setting header " + key + " = " + headers.get(key));
                    request.addHeader(key, headers.get(key));
                }
            }

            HttpClientParams.setRedirecting(basicParams, options.followRedirects);
            request.setParams(basicParams);
            LenddoHttpResponseHandler responseHandler = new LenddoHttpResponseHandler();
            try {
                String responseString = httpclient
                        .execute(request, responseHandler);
                Log.v(this.getClass().toString(), "Response: "
                        + responseString);
            } catch (HttpResponseException e) {

            } catch (ClientProtocolException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return responseHandler;
        }

        @Override
        protected void onPostExecute(LenddoHttpResponseHandler lenddoHttpResponseHandler) {
            if (lenddoHttpResponseHandler.getStatusCode() == 200) {
                listener.onComplete(lenddoHttpResponseHandler.getRawResponse());
            } else {
                listener.onError(lenddoHttpResponseHandler.getStatusCode(), lenddoHttpResponseHandler.getRawResponse());
            }
        }
    }

    @Override
    public void query(String url, HashMap<String, String> headers, HashMap<String, String> params,
                      String rawRequest,
                      int method, LenddoHttpOptions options, OnLenddoQueryCompleteListener listener) {
        LenddoRequestTask requestTask = new LenddoRequestTask(method, url, headers, params, rawRequest, listener, options);
        requestTask.execute();
    }
}
