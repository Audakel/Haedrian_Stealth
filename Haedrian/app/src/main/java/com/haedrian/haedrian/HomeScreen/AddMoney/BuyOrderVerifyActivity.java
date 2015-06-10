package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.Models.BuyOrderHistoryModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class BuyOrderVerifyActivity extends ActionBarActivity {

    private TextView buyOrderTV, statusTV, amountTV, orderTimeTV, expirationDateTV, locationTV;
    private BuyOrderHistoryModel buyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_order_verify);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            buyOrder = extras.getParcelable("buy_order");
        }

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        buyOrderTV = (TextView) findViewById(R.id.buy_order_title);
        statusTV = (TextView) findViewById(R.id.status);
        amountTV = (TextView) findViewById(R.id.amount);
        orderTimeTV = (TextView) findViewById(R.id.order_time);
        expirationDateTV = (TextView) findViewById(R.id.expiration_date);
        locationTV = (TextView) findViewById(R.id.location);

//        Log.v("TEST", "buyorder: " + buyOrder.toString());

        buyOrderTV.setText(getString(R.string.buy_order) + buyOrder.getId());

        String buyOrderStatusTemp = buyOrder.getStatus();
        String buyOrderStatus = "";
        String[] parts = buyOrderStatusTemp.split("_");
        for (int i = 0; i < parts.length; i++) {
            buyOrderStatus += parts[i].toUpperCase() + " ";
        }
        statusTV.setText(buyOrderStatus);

        Double currencyAmount = Double.parseDouble(buyOrder.getCurrencyAmount());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        amountTV.setText(currencyFormatter.format(currencyAmount));


        Calendar cal = Calendar.getInstance(Locale.getDefault());
        String orderTime = getString(R.string.not_available);
        if ( ! buyOrder.getCreatedAt().equals("null")) {
            cal.setTimeInMillis(Long.parseLong(buyOrder.getCreatedAt()));
            orderTime = DateFormat.format("MM-dd-yyyy HH:mm", cal).toString();
        }

        orderTimeTV.setText(orderTime);

        cal.setTimeInMillis(Long.parseLong(buyOrder.getExpirationTime()));
        String expirationTime = DateFormat.format("MM-dd-yyyy HH:mm", cal).toString();
        expirationDateTV.setText(expirationTime);
        locationTV.setText(buyOrder.getOutletTitle());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_buy_order_verify, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.mark_as_paid_button:
                verifyPayment();
                break;
            default:
                break;
        }
    }

    private void verifyPayment() {
        final String URL = ApplicationConstants.BASE + "buy/?order_id=" + buyOrder.getId();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "buy-verify: " + response.toString());
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
            }

        }) {
            @Override
            public HashMap<String, String> getHeaders() {
                String token = ApplicationController.getToken();
                HashMap<String, String> params = new HashMap<>();
                params.put("Authorization", "Token " + token);
                params.put("Content-Type", "application/json;charset=UTF-8");
                params.put("Accept", "application/json");
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new TimeoutRetryPolicy());

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }
}
