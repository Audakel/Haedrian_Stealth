package com.haedrian.haedrian.HomeScreen.Wallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class TransactionDetailsActivity extends Activity {

    private TransactionModel transaction;
    private TextView id, btcAmount, currencyAmount, fromPerson, toPerson, note, date, statusTV;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();

        Intent intent = getIntent();
        if (intent != null) {
            transaction = intent.getParcelableExtra("transaction");
            getConversion(transaction.getAmount());
        }

        date = (TextView) findViewById(R.id.date);
        btcAmount = (TextView) findViewById(R.id.btc_amount);
        currencyAmount = (TextView) findViewById(R.id.currency_amount);
        id = (TextView) findViewById(R.id.transaction_id);
        statusTV = (TextView) findViewById(R.id.status);

        fromPerson = (TextView) findViewById(R.id.from_person);
        toPerson = (TextView) findViewById(R.id.to_person);
        note = (TextView) findViewById(R.id.note);

        if (transaction.getDate() != null) {
            String[] dateParts = transaction.getDate().split("T");
            Log.v("TEST", "date: " + dateParts[0]);
            date.setText(formatDate(dateParts[0]));
        }
        else {
            long milli = System.currentTimeMillis();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date tempDate = new Date(milli);
            String today = sdf.format(tempDate);
            date.setText(formatDate(today));
        }
        id.setText(getString(R.string.transaction_id) + transaction.getId());
        statusTV.setText(transaction.getStatus().toUpperCase());
        btcAmount.setText(transaction.getAmount());
        if (transaction.getEntryType().equals("incoming")) {
            toPerson.setText(getString(R.string.me));
            if (transaction.getSender().equals("null")){
                fromPerson.setText(getString(R.string.outside_address));
            }
            else {
                fromPerson.setText(transaction.getSender());
            }
        }
        else {
            if (transaction.getTarget().equals("null")){
                toPerson.setText(getString(R.string.outside_address));
            }
            else {
                toPerson.setText(transaction.getTarget());
            }
            fromPerson.setText(getString(R.string.me));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " opened.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_transaction_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String formatDate(String date) {
        String[] parts = date.split("-");
        String year = parts[0];
        String month = "";

        switch (parts[1]) {
            case "01":
                month = getString(R.string.january);
                break;
            case "02":
                month = getString(R.string.february);
                break;
            case "03":
                month = getString(R.string.march);
                break;
            case "04":
                month = getString(R.string.april);
                break;
            case "05":
                month = getString(R.string.may);
                break;
            case "06":
                month = getString(R.string.june);
                break;
            case "07":
                month = getString(R.string.july);
                break;
            case "08":
                month = getString(R.string.august);
                break;
            case "09":
                month = getString(R.string.september);
                break;
            case "10":
                month = getString(R.string.october);
                break;
            case "11":
                month = getString(R.string.november);
                break;
            case "12":
                month = getString(R.string.december);
                break;
        }

        int day = Integer.parseInt(parts[2]);

        return month + " " + day + ", " + year;

    }

    public void getConversion(String bitcoinAmount) {

        final String amount = bitcoinAmount;
        if (Locale.getDefault().equals(Locale.US)) {
            final String url = "https://blockchain.info/ticker";

            JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ApplicationController.cacheJSON(response, "ticker");
                            try {
                                JSONObject currentCurrency = response.getJSONObject("USD");
                                int last = currentCurrency.getInt("last");
                                setConvertedRate(last, amount);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    progressDialog.dismiss();
                }
            });

            currencyRequest.setRetryPolicy(new TimeoutRetryPolicy());
            ApplicationController.getInstance().addToRequestQueue(currencyRequest);

        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            final String URL = ApplicationConstants.BASE + "exchange-rate/?currency=PHP";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("TEST", "exchange-rate: " + response.toString());
                            ApplicationController.cacheJSON(response, "ticker");
                            try {
                                JSONObject currentCurrency = response.getJSONObject("market");
                                int last = currentCurrency.getInt("ask");
                                setConvertedRate(last, amount);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Test", "Error: " + error.toString());
                    progressDialog.dismiss();

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

    public void setConvertedRate(int rate, String bitcoinAmount) {
        float conversion = (float) rate * Float.parseFloat(bitcoinAmount);

        Double newAmount = Double.parseDouble(String.valueOf(conversion));
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();

        String formattedAmount = format.format(newAmount);

        currencyAmount.setText(formattedAmount);
        progressDialog.dismiss();
    }
}
