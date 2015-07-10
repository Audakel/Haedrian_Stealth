package com.haedrian.haedrian.HomeScreen;

import android.app.ActionBar;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.RepayConfirmDialog;
import com.haedrian.haedrian.HomeScreen.Wallet.TransactionDetailsActivity;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.Network.JsonUTF8Request;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.DecimalDigitsInputFilter;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;

public class RepayLoanActivity extends ActionBarActivity {

    private Spinner microfinanceSpinner, groupPaymentsSpinner;
    private LinearLayout paymentTypeContainer, individualContainer;
    private RadioGroup individualGroup;
    private Button repayLoanButton;
    private TextView currencySymbol;
    private EditText amountET, noteET;
    private ScrollView myScrollView;

    private ArrayList<String> microfinanceInstitutions = new ArrayList<>();
    private ArrayList<String> paymentAmounts = new ArrayList<>();
    private ArrayList<String> groupPayments = new ArrayList<>();
    private ArrayList<String> paymentIds = new ArrayList<>();

    private String currency;
    private Bundle extras;


    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repay_loan);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        currency = ApplicationController.getSetCurrencySign();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();

        getPendingGroupPayments();

        microfinanceInstitutions.add(getString(R.string.microfinance_institution));
        microfinanceInstitutions.add(getString(R.string.mentors_international));

        groupPayments.add(getString(R.string.pending_group_repayments));
        paymentIds.add("");
        paymentAmounts.add("");

        myScrollView = (ScrollView) findViewById(R.id.scrollview);
        microfinanceSpinner = (Spinner) findViewById(R.id.microfinance_spinner);
        groupPaymentsSpinner = (Spinner) findViewById(R.id.group_payments_spinner);
        paymentTypeContainer = (LinearLayout) findViewById(R.id.payment_type_container);
        individualContainer = (LinearLayout) findViewById(R.id.individual_payment_container);
        individualGroup = (RadioGroup) findViewById(R.id.radio_group);
        repayLoanButton = (Button) findViewById(R.id.repay_loan_button);
        currencySymbol = (TextView) findViewById(R.id.currency_sign);
        amountET = (EditText) findViewById(R.id.amount_currency);
        amountET.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(7,2)});

        noteET = (EditText) findViewById(R.id.note);

        amountET.addTextChangedListener(new TextWatcher() {
            // This flag is used so that it only shows the animation once
            boolean flag = true;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    if (flag) {
                        repayLoanButton.setVisibility(View.VISIBLE);
                        repayLoanButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                        flag = false;
                    }
                } else {
                    repayLoanButton.setVisibility(View.GONE);
                    flag = true;
                }
            }
        });

        currencySymbol.setText(currency);

        ArrayAdapter<String> mfiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, microfinanceInstitutions);
        microfinanceSpinner.setAdapter(mfiAdapter);
        microfinanceSpinner.setSelection(0);
        microfinanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    paymentTypeContainer.setVisibility(View.VISIBLE);
                    paymentTypeContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                } else {
                    individualGroup.clearCheck();
                    extras = getIntent().getExtras();
                    if (extras != null) {
                        amountET.setText(extras.getString("amount_due", ""));
                        int length = extras.getString("amount_due").length();
                        amountET.setSelection(length, length);
                    }
                    else {
                        amountET.setText("");
                    }
                    noteET.setText("");
                    paymentTypeContainer.setVisibility(View.GONE);
                    individualContainer.setVisibility(View.GONE);
                    groupPaymentsSpinner.setVisibility(View.GONE);
                    repayLoanButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        individualGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                repayLoanButton.setVisibility(View.GONE);
                if (checkedId == R.id.individual) {
                    groupPaymentsSpinner.setVisibility(View.GONE);
                    individualContainer.setVisibility(View.VISIBLE);
                    individualContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                    amountET.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(amountET, InputMethodManager.SHOW_IMPLICIT);

                } else if (checkedId == R.id.group) {
                    amountET.setText("");
                    noteET.setText("");
                    individualContainer.setVisibility(View.GONE);
                    groupPaymentsSpinner.setVisibility(View.VISIBLE);
                    groupPaymentsSpinner.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                }
            }
        });

        ArrayAdapter<String> groupPaymentsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupPayments);
        groupPaymentsSpinner.setAdapter(groupPaymentsAdapter);
        groupPaymentsSpinner.setSelection(0);
        groupPaymentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    repayLoanButton.setVisibility(View.VISIBLE);
                    repayLoanButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                } else {
                    repayLoanButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        repayLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amount = "";
                int checkedRadioId = individualGroup.getCheckedRadioButtonId();
                if (checkedRadioId == R.id.individual) {
                    amount = convertToCurrency(amountET.getText().toString());
                }
                else if (checkedRadioId == R.id.group) {
                    amount = paymentAmounts.get(groupPaymentsSpinner.getSelectedItemPosition());
                }

                String message = getString(R.string.repay_loan_of) + " "
                        + amount + " "
                        + getString(R.string.to_dialog) + " "
                        + microfinanceInstitutions.get(microfinanceSpinner.getSelectedItemPosition()) + "?";


                final RepayConfirmDialog dialog = new RepayConfirmDialog(RepayLoanActivity.this, message);
                dialog.show();

                dialog.getConfirmButton().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        progressDialog.show();
                        sendPayment();
                    }
                });
                dialog.getCancelButton().setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_repay_loan, menu);
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

    private void getPendingGroupPayments() {
        String url = ApplicationConstants.BASE + "group-payment/";


        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "group-payment: " + response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray payments = response.getJSONArray("payments");
                                int length = payments.length();
                                for (int i = 0; i < length; i++) {

                                    String formattedAmount = convertToCurrency(payments.getJSONObject(i).getString("total_payment"));
                                    paymentAmounts.add(formattedAmount);
                                    String paymentMessage = getString(R.string.group_payment_of) + " " + formattedAmount;
                                    groupPayments.add(paymentMessage);
                                    paymentIds.add(payments.getJSONObject(i).getString("payment_id"));
                                }
                                progressDialog.dismiss();
                            }
                            else {
                                progressDialog.dismiss();
                                JSONObject error = response.getJSONObject("error");
                                Toast.makeText(RepayLoanActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("TEST", "Error: " + error.getMessage());
                Toast.makeText(RepayLoanActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
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

    private String convertToCurrency(String payment) {
        Double paymentAmount = Double.parseDouble(payment);
        DecimalFormat decimalFormat = new DecimalFormat("######0.00");
        return currency + decimalFormat.format(paymentAmount);
    }

    private void sendPayment() {

        // Check for network connection. If no network connection, open up sms messaging app with prepopulated number and commands
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState()==NetworkInfo.State.CONNECTED) {
                sendMoney();
            }
            else {
                netInfo = cm.getNetworkInfo(1);

                if(netInfo != null && netInfo.getState()== NetworkInfo.State.CONNECTED){
                    sendMoney();
                }
                else {
                    sendMoneyText();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }

//        sendMoney();
    }

    public void sendMoney() {
        String url = ApplicationConstants.BASE + "send/";

        String currency = "";
        if (Locale.getDefault().equals(Locale.US)) {
            currency = "USD";
        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            currency = "PHP";
        }

        String amount = "";
        int checkedRadioId = individualGroup.getCheckedRadioButtonId();
        if (checkedRadioId == R.id.individual) {
            amount = amountET.getText().toString();
        }
        else if (checkedRadioId == R.id.group) {
            amount = paymentAmounts.get(groupPaymentsSpinner.getSelectedItemPosition());
        }

        JSONObject body = new JSONObject();
        try {
            body.put("send_method", "username");
            // Hardcoded until we get more business
            body.put("send_to", "mentors_international");
            body.put("amount_local", amount);
            body.put("currency", currency);
            // Optional Params
            body.put("note", noteET.getText().toString());
            // Used only for group repayments
            if (checkedRadioId == R.id.group) {
                body.put("payment_id", paymentIds.get(groupPaymentsSpinner.getSelectedItemPosition()));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.POST,
                url, body,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "send: " + response.toString());
                        try {
                            Log.v("TEST", response.toString());

                            if (response.getBoolean("success")) {
                                TransactionModel transaction = new TransactionModel();
                                transaction.setId(response.getString("id"));
                                transaction.setStatus(response.getString("status"));
                                transaction.setCurrency(response.getString("currency"));
                                transaction.setAmount(response.getString("amount"));
                                transaction.setTarget(response.getString("target"));
                                transaction.setFeeAmount(response.getString("fee"));
                                transaction.setEntryType(getString(R.string.outgoing));

                                ApplicationController.setHomeScreenTimestamp(0L);
                                ApplicationController.setBalanceTimestamp(0L);

                                Intent intent = new Intent(RepayLoanActivity.this, TransactionDetailsActivity.class);
                                intent.putExtra("transaction", transaction);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                String error = response.getString("error");
                                Toast.makeText(RepayLoanActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("TEST", "Error: " + error.getMessage());
                Toast.makeText(RepayLoanActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
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

    public void sendMoneyText() {
        sendSMS("ourPhoneNumber", "text commands");
    }

    //---sends an SMS message to another device---
    private void sendSMS(String phoneNumber, String message)
    {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, RepayLoanActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }
}
