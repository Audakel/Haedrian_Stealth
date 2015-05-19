package com.haedrian.haedrian.HomeScreen;

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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.ConfirmOrderDialog;
import com.haedrian.haedrian.CustomDialogs.PaymentMethodDialog;
import com.haedrian.haedrian.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;


public class BuyActivity extends ActionBarActivity {
    private EditText currencyEditText, bitcoinEditText;
    private TextView amountCurrency;
    private Spinner methodSpinner;
    private String buyRate = "0";
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getExchangeRate();

        methodSpinner = (Spinner) findViewById(R.id.method_spinner);
        currencyEditText = (EditText) findViewById(R.id.currency_edittext);
        bitcoinEditText = (EditText) findViewById(R.id.bitcoin_edittext);
        amountCurrency = (TextView) findViewById(R.id.amount_currency);
        submitButton = (Button) findViewById(R.id.submit_button);

        currencyEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString() != "") {
                    try {
                        BigDecimal buyRateDecimal = new BigDecimal(buyRate);
                        BigDecimal amount = new BigDecimal(currencyEditText.getText().toString());
                        BigDecimal newAmount = amount.divide(buyRateDecimal, 6, RoundingMode.HALF_UP);
                        if (currencyEditText.isFocused()) {
                            bitcoinEditText.setText(String.valueOf(newAmount));
                        }
                    } catch (Exception e) {

                    }
                }
                else {
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
                if (s.toString() != "") {
                    try {
                        BigDecimal buyRateDecimal = new BigDecimal(buyRate);
                        BigDecimal amount = new BigDecimal(bitcoinEditText.getText().toString());
                        BigDecimal newAmount = amount.multiply(buyRateDecimal);
                        if (bitcoinEditText.isFocused()) {
                            currencyEditText.setText(String.valueOf(newAmount));
                        }
                    } catch (Exception e) {

                    }
                }
                else {
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

                    ArrayList<String> paymentMethods = new ArrayList<String>();
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
                            setFinancials();
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
                final ConfirmOrderDialog dialog = new ConfirmOrderDialog(BuyActivity.this, bitcoinEditText.getText().toString(), currencyEditText.getText().toString());
                dialog.show();
                Button confirmButton = dialog.getConfirmButton();
                confirmButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });

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

    private void setFinancials() {

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

}
