package com.haedrian.haedrian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class RequestActivity extends ActionBarActivity {

    private final String base = "https://blockchain.info/merchant/$guid/";
    private String requestAmount, requestAmountBitcoin, fromUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestAmount = getIntent().getStringExtra("request_amount");
        requestAmountBitcoin = getIntent().getStringExtra("request_amount_bitcoin");
        fromUser = getIntent().getStringExtra("from_user");


        BigDecimal amount = round(Float.parseFloat(requestAmount), 2);

        getSupportActionBar().setTitle("Request $" + amount.toString());

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_request) {
            requestTransaction();
            return true;
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestTransaction() {
        String URL = base;


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Requesting Payment...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                progressDialog.hide();
                                Toast.makeText(RequestActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressDialog.hide();
                                returnToPreviousActivitySuccess(response.getString("message"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(RequestActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(RequestActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_req");
    }

    private void returnToPreviousActivitySuccess(String data) {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putString("data", data);
        i.putExtras(extras);

        setResult(RESULT_OK, i);
        finish();
    }
}