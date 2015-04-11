package com.lenddo.sdk.http;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.impl.client.BasicResponseHandler;

import java.io.IOException;
import java.io.InputStreamReader;

/**
* Created by joseph on 8/11/14.
*/
public class LenddoHttpResponseHandler extends BasicResponseHandler {

    String rawResponse;
    private int statusCode;

    public LenddoHttpResponseHandler() {
    }

    public String getRawResponse() {
        return rawResponse;
    }

    @Override
    public String handleResponse(HttpResponse response)
            throws ClientProtocolException, IOException {

        long content_length = response.getEntity().getContentLength();
        Log.d(this.getClass().toString(), "status = "
                + response.getStatusLine().getStatusCode());
        Log.d(this.getClass().toString(), "reason = "
                + response.getStatusLine().getReasonPhrase());
        Log.d(this.getClass().toString(), "content length = " + content_length);

        setStatusCode(response.getStatusLine().getStatusCode());

        if (response.getStatusLine().getStatusCode() == 302) {
            Header[] headers = response.getHeaders("Location");
            rawResponse = headers[0].getValue();
            return rawResponse;
        } else if (response.getStatusLine().getStatusCode() != 200) {
            InputStreamReader reader = new InputStreamReader(response
                    .getEntity().getContent());
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < content_length; i++) {
                buffer.append((char) reader.read());
            }
            rawResponse = buffer.toString();
            return rawResponse;
        } else {
            String responseBody = super.handleResponse(response);
            rawResponse = responseBody;
            return responseBody;
        }
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
