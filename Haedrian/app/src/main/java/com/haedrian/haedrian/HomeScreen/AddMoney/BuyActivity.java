package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.ConfirmOrderDialog;
import com.haedrian.haedrian.Models.BuyOrderModel;
import com.haedrian.haedrian.Network.JsonUTF8Request;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;


public class BuyActivity extends ActionBarActivity {
    private EditText currencyEditText, bitcoinEditText;
    private TextView amountCurrency, currencySign, subtotalTV, haedrianFeeTV, paymentMethodFeeTV, totalDueTV;
    private Spinner locationSpinner, outletSpinner;
    private String buyRate = "0";
    private Button submitButton;
    private ArrayList<String> paymentMethods;
    private ArrayList<String> depositLocations = new ArrayList<>();
    private ArrayList<ArrayList<String>> outletLocations = new ArrayList<>();
    private ArrayList<ArrayList<String>> outletIds = new ArrayList<>();
    private ArrayList<ArrayList<String>> fees = new ArrayList<>();

    private Double subtotal = 0.00;
    private Double haedrianFee = 0.00;
    private Double paymentMethodFee = 0.00;
    private Double total = 0.00;
    private String groupTotal = "";
    private String groupRepaymentId = "";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        currencySign = (TextView) findViewById(R.id.currency_sign_buy);
        Currency currency = Currency.getInstance(Locale.getDefault());
        currencySign.setText(currency.getSymbol());

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        getExchangeRate();
        getExchangeTypes();

        locationSpinner = (Spinner) findViewById(R.id.method_spinner);
        outletSpinner = (Spinner) findViewById(R.id.outlet_spinner);
        currencyEditText = (EditText) findViewById(R.id.currency_edittext);
        bitcoinEditText = (EditText) findViewById(R.id.bitcoin_edittext);
        amountCurrency = (TextView) findViewById(R.id.amount_currency);
        submitButton = (Button) findViewById(R.id.submit_button);

        subtotalTV = (TextView) findViewById(R.id.subtotal);
        haedrianFeeTV = (TextView) findViewById(R.id.haedrian_fee);
        paymentMethodFeeTV = (TextView) findViewById(R.id.payment_method_fee);
        totalDueTV = (TextView) findViewById(R.id.total_due);

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        subtotalTV.setText(currencyFormatter.format(subtotal));
        haedrianFeeTV.setText(currencyFormatter.format(haedrianFee));
        paymentMethodFeeTV.setText(currencyFormatter.format(paymentMethodFee));
        totalDueTV.setText(currencyFormatter.format(total));

        paymentMethods = new ArrayList<String>();

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }


        currencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    try {
                        BigDecimal buyRateDecimal = new BigDecimal(buyRate);
                        BigDecimal amount = new BigDecimal(currencyEditText.getText().toString());
                        BigDecimal newAmount = amount.divide(buyRateDecimal, 6, RoundingMode.HALF_UP);
                        if (currencyEditText.isFocused()) {
                            bitcoinEditText.setText(String.valueOf(newAmount));
                        }
                        setSubtotal(currencyEditText.getText().toString());
                    } catch (Exception e) {

                    }
                } else {
                    setSubtotal("0");
                    if (currencyEditText.isFocused()) {
                        bitcoinEditText.setText("");
                    }
                }
            }
        });

        bitcoinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals("")) {
                    try {
                        BigDecimal buyRateDecimal = new BigDecimal(buyRate);
                        BigDecimal amount = new BigDecimal(bitcoinEditText.getText().toString());
                        BigDecimal newAmount = amount.multiply(buyRateDecimal);
                        if (bitcoinEditText.isFocused()) {
                            currencyEditText.setText(String.valueOf(newAmount));
                        }
                        setSubtotal(currencyEditText.getText().toString());
                    } catch (Exception e) {

                    }
                } else {
                    setSubtotal("0");
                    if (bitcoinEditText.isFocused()) {
                        currencyEditText.setText("");
                    }
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmOrderDialog dialog = new ConfirmOrderDialog(BuyActivity.this, currencyEditText.getText().toString(), bitcoinEditText.getText().toString(), totalDueTV.getText().toString());
                if (currencyEditText.getText().toString().equals("") || bitcoinEditText.getText().toString().equals("")) {
                    Toast.makeText(BuyActivity.this, getResources().getString(R.string.please_enter_buy_amount), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (locationSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(BuyActivity.this, getResources().getString(R.string.please_select_payment_method), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (outletSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(BuyActivity.this, getResources().getString(R.string.please_select_location), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.show();
                Button confirmButton = dialog.getConfirmButton();
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String outletId = outletIds.get(locationSpinner.getSelectedItemPosition() - 1).get(outletSpinner.getSelectedItemPosition());

                        progressDialog.show();
                        // Make buy order
                        makeBuyOrder(outletId);
                    }
                });
            }
        });

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            groupTotal = extras.getString("total");
            groupRepaymentId = extras.getString("group_repayment_id");
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
        progressDialog.dismiss();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_buy, menu);
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
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getExchangeTypes() {
        String URL = ApplicationConstants.BASE + "exchanges/";

        depositLocations.add(0, getString(R.string.no_method));

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.v("TEST", "Exchanges: " + response.toString());
                            JSONArray locations = response.getJSONArray("locations");
                            for (int i = 0; i < locations.length(); i++) {
                                depositLocations.add(locations.getJSONObject(i).getString("name"));
                                JSONArray outlets = locations.getJSONObject(i).getJSONArray("outlets");
                                ArrayList<String> outlet = new ArrayList<>();
                                outlet.add(0, getString(R.string.no_deposit_location));
                                ArrayList<String> outletId = new ArrayList<>();
                                outletId.add(0, "");
                                ArrayList<String> fee = new ArrayList<>();
                                fee.add(0,"");
                                for (int j = 0; j < outlets.length(); j++) {
                                    outlet.add(outlets.getJSONObject(j).getString("name"));
                                    outletId.add(outlets.getJSONObject(j).getString("id"));
                                    if (outlets.getJSONObject(j).has("fee_info")) {
                                        fee.add(outlets.getJSONObject(j).getJSONObject("fee_info").getString("fee_amount"));
                                    }
                                    else {
                                        fee.add("0.00");
                                    }
                                }
                                fees.add(fee);
                                outletLocations.add(outlet);
                                outletIds.add(outletId);
                            }
                            progressDialog.hide();
                            // Get locations
                            getLocations();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                        }


                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
                progressDialog.hide();
                Log.v("TEST", error.toString());
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

    public void getLocations() {
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, depositLocations);
        locationSpinner.setAdapter(locationAdapter);
        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    // Set up outlets here
                    FlurryAgent.logEvent("User searched for the locations of this bank: " + depositLocations.get(position));
                    ArrayList<String> tempOutlets = outletLocations.get(position - 1);
                    getOutlets(tempOutlets);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void getOutlets(ArrayList<String> tempOutlets) {
        progressDialog.hide();
        ArrayAdapter<String> outletAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, tempOutlets);
        outletSpinner.setAdapter(outletAdapter);
        outletSpinner.setVisibility(View.VISIBLE);
        outletSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    String fee = fees.get(locationSpinner.getSelectedItemPosition() - 1).get(position);
                    Log.v("TEST", "fee: " + fee);
                    setPaymentMethodFee(fee);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getExchangeRate() {
        if (Locale.getDefault().equals(Locale.US)) {
            final String TAG = "exchange_rate";
            // Creating volley request obj
            String url = "https://blockchain.info/ticker";
            JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Parsing json
                            try {
                                JSONObject currentCurrency = response.getJSONObject("USD");
                                amountCurrency.setText(currentCurrency.getString("buy"));
                                buyRate = currentCurrency.getString("buy");
                                setDisplay();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue
            ApplicationController.getInstance().addToRequestQueue(currencyRequest);
        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            final String URL = ApplicationConstants.BASE + "exchange-rate/?currency=PHP";

            JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                    URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("TEST", "exchange-rate: " + response.toString());
                            try {
                                JSONObject currentCurrency = response.getJSONObject("market");
                                amountCurrency.setText(currentCurrency.getString("ask"));
                                buyRate = currentCurrency.getString("ask");
                                setDisplay();
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

    private void setDisplay() {
        if ( ! groupTotal.equals("")) {
            // This makes the edit texts not editable so that they don't set the group amount and then change the buy order
            currencyEditText.setKeyListener(null);
            currencyEditText.setFocusable(false);
            bitcoinEditText.setKeyListener(null);
            bitcoinEditText.setFocusable(false);

            currencyEditText.setText(groupTotal);
            try {
                BigDecimal buyRateDecimal = new BigDecimal(buyRate);
                BigDecimal amount = new BigDecimal(currencyEditText.getText().toString());
                BigDecimal newAmount = amount.divide(buyRateDecimal, 6, RoundingMode.HALF_UP);
                bitcoinEditText.setText(String.valueOf(newAmount));
                setSubtotal(currencyEditText.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setSubtotal(String subtotalStr) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        subtotal = Double.parseDouble(subtotalStr);
        subtotalTV.setText(currencyFormatter.format(subtotal));
        setTotal();
    }

    private void setHaedrianFee(String haedrianFeeStr) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        haedrianFee = Double.parseDouble(haedrianFeeStr);
        haedrianFeeTV.setText(currencyFormatter.format(haedrianFee));
        setTotal();
    }

    private void setPaymentMethodFee(String paymentMethodFeeStr) {
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        paymentMethodFee = Double.parseDouble(paymentMethodFeeStr);
        paymentMethodFeeTV.setText(currencyFormatter.format(paymentMethodFee));
        setTotal();
    }

    private void setTotal() {
        total = subtotal + haedrianFee + paymentMethodFee;

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
        totalDueTV.setText(currencyFormatter.format(total));
    }

    private void makeBuyOrder(String outletId) {
        String url = ApplicationConstants.BASE + "buy/";

        String currency = "";
        if (Locale.getDefault().equals(Locale.US)) {
            currency = "USD";
        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            currency = "PHP";
        }

        JSONObject body = new JSONObject();
        try {
            body.put("currency", currency);
            body.put("amount_local", currencyEditText.getText().toString());
            body.put("payment_method", outletId);
            body.put("target_account_id", "");
            if ( ! groupRepaymentId.equals("")) {
                body.put("group_repayment_id", groupRepaymentId);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.POST,
                url, body,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            Log.v("TEST", "Buyorder: " + response.toString());

                            if (response.getBoolean("success")) {

                                BuyOrderModel buyOrder = new BuyOrderModel();
                                buyOrder.setStatus(response.getJSONObject("order").getString("status"));
                                buyOrder.setPaymentOutlet(response.getJSONObject("order").getString("payment_outlet_title"));
                                buyOrder.setInstructions(response.getJSONObject("order").getString("instructions"));
                                buyOrder.setBtcAmount(response.getJSONObject("order").getString("btc_amount"));
                                buyOrder.setCurrencyAmount(response.getJSONObject("order").getString("currency_amount"));
                                buyOrder.setPaymentMethodFee(paymentMethodFee.toString());
                                String id = response.getJSONObject("order").getString("id");

                                progressDialog.hide();

//                                FlurryAgent.logEvent("User selected this deposit option: " + outletLocations.get(outletSpinner.getSelectedItemPosition() - 1));
                                Intent intent = new Intent(BuyActivity.this, OrderSummaryActivity.class);
                                intent.putExtra("buy_order", buyOrder);
                                intent.putExtra("id", id);
                                startActivity(intent);
                            }
                            else {
                                progressDialog.dismiss();
                                String error = response.getString("error");
                                Toast.makeText(BuyActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            progressDialog.hide();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Log.v("TEST", "Error: " + error.getMessage());
                progressDialog.hide();
            }
        }){
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
