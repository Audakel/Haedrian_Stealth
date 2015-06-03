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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.ConfirmOrderDialog;
import com.haedrian.haedrian.CustomDialogs.PaymentMethodDialog;
import com.haedrian.haedrian.R;

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
    private Spinner methodSpinner;
    private String buyRate = "0";
    private Button submitButton;
    private ArrayList<String> paymentMethods;
    private RequestQueue queue;

    private Double subtotal = 0.00;
    private Double haedrianFee = 0.00;
    private Double paymentMethodFee = 0.00;
    private Double total = 0.00;

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

        queue = Volley.newRequestQueue(this);

        getExchangeRate();

        methodSpinner = (Spinner) findViewById(R.id.method_spinner);
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
        getExchangeLocations();


        currencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if ( ! s.toString().equals("")) {
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
                }
                else {
                    setSubtotal("0");
                    if (currencyEditText.isFocused()) {
                        bitcoinEditText.setText("");
                    }
                }
            }
        });

        bitcoinEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if ( ! s.toString().equals("")) {
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
                }
                else {
                    setSubtotal("0");
                    if (bitcoinEditText.isFocused()) {
                        currencyEditText.setText("");
                    }
                }
            }
        });

        methodSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // Do API call here to get the methods and the fees from coins.ph

                    // Show dialog
                    final PaymentMethodDialog dialog = new PaymentMethodDialog(BuyActivity.this, paymentMethods);
                    dialog.show();
                    ListView methodListView = dialog.getListView();
                    final ArrayList<String> finalPaymentMethods = paymentMethods;
                    methodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ArrayAdapter<String> methodsAdapter = new ArrayAdapter<String>(BuyActivity.this, android.R.layout.simple_spinner_item, finalPaymentMethods);
                            methodSpinner.setAdapter(methodsAdapter);
                            methodSpinner.setSelection(position);
                            setPaymentMethodFee("3.34");
                            dialog.dismiss();
                        }
                    });
                }
                return true;
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ConfirmOrderDialog dialog = new ConfirmOrderDialog(BuyActivity.this, totalDueTV.getText().toString(), bitcoinEditText.getText().toString() );
                if (currencyEditText.getText().toString().equals("") ||  bitcoinEditText.getText().toString().equals("")) {
                    Toast.makeText(BuyActivity.this, getResources().getString(R.string.please_enter_buy_amount), Toast.LENGTH_SHORT).show();
                    return;
                }

                if (methodSpinner.getSelectedItemPosition() == 0) {
                    Toast.makeText(BuyActivity.this, getResources().getString(R.string.please_select_payment_method), Toast.LENGTH_SHORT).show();
                    return;
                }

                dialog.show();
                Button confirmButton = dialog.getConfirmButton();
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        FlurryAgent.logEvent("User selected this deposit option: " + paymentMethods.get(methodSpinner.getSelectedItemPosition()));
                        Intent intent = new Intent(BuyActivity.this, OrderSummaryActivity.class);
                        intent.putExtra("buy_amount", subtotal);
                        intent.putExtra("haedrian_fee", haedrianFee);
                        intent.putExtra("payment_method_fee", paymentMethodFee);
                        intent.putExtra("total", total);
                        startActivity(intent);
                    }
                });
            }
        });

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
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    private void getExchangeLocations() {

        final String URL = ApplicationConstants.BASE + "exchanges/";

//        Log.v("TEST", ApplicationController.getToken());

        JSONObject body = new JSONObject();
        try {
            body.put("type", "ATM transfer");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, body,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            Log.v("TEST", response.toString());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST", error.toString());
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

        queue.add(jsonObjectRequest);


        paymentMethods.add("BDO 24-hour ATM Deposit");
        paymentMethods.add("BDO Online Banking");
        paymentMethods.add("BDO over-the-counter deposit");
        paymentMethods.add("BPI Express Online");
        paymentMethods.add("BPI over-the-counter deposit");
        paymentMethods.add("GCash");
        paymentMethods.add("Globe Share-a-Load");
        paymentMethods.add("Security Bank");
        paymentMethods.add("UnionBank of the Philippines");
        paymentMethods.add("UnionBank of the Philippines Online Banking");
    }

    private void getExchangeRate() {
        final String TAG = "exchange_rate";
        // Creating volley request obj
        String url = "https://blockchain.info/ticker";
        JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // Parsing json
                        try {
                            JSONObject currentCurrency = response.getJSONObject("USD");
                            amountCurrency.setText(currentCurrency.getString("buy"));
                            buyRate = currentCurrency.getString("buy");
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

}
