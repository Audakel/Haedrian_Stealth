package com.haedrian.haedrian.HomeScreen.SendRequest;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.google.gson.JsonArray;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.Contacts.ContactsListFragment;
import com.haedrian.haedrian.CustomDialogs.SendConfirmationDialog;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.HomeScreen.Wallet.TransactionDetailsActivity;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.Models.WalletModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.Scanner.CaptureActivity;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class SendActivity extends Activity implements
        ContactsListFragment.OnContactsInteractionListener {

    private final String base = "https://blockchain.info/merchant/$guid/payment";
    private String sendAmount, sendAmountBitcoin, walletAddress, parseId, receiverId, userId;
    private EditText toET, noteET;
    private WalletModel wallet;
    private BigDecimal sendAmountNumber, sendAmountBitcoinNumber;
    private int bitcoinBuy, bitcoinSell;
    private ProgressDialog progressDialog;
    private CheckBox checkbox;
    private Menu menu;

    private static final int START_SCANNER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Set up ActionBar
        getActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        sendAmount = getIntent().getStringExtra("send_amount");
        sendAmountBitcoin = getIntent().getStringExtra("send_amount_bitcoin");
        bitcoinBuy = getIntent().getIntExtra("bitcoin_buy", 0);
        bitcoinSell = getIntent().getIntExtra("bitcoin_sell", 0);

        toET = (EditText) findViewById(R.id.to_edittext);
        noteET = (EditText) findViewById(R.id.note_edittext);
        checkbox = (CheckBox) findViewById(R.id.checkbox);

        toET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (toET.length() > 0) {
                    menu.getItem(0).setEnabled(true);
                    menu.getItem(0).setIcon(R.drawable.ic_action_send_now);
                }
                else {
                    menu.getItem(0).setEnabled(false);
                    menu.getItem(0).setIcon(R.drawable.ic_action_send_now_disabled);
                }
            }
        });

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        parseId = sp.getString("parse_id", "");

        DBHelper db = new DBHelper(this);
        wallet = db.getWalletsTable().selectByUserId(userId);

        sendAmountNumber = round(Float.parseFloat(sendAmount), 2);
        sendAmountBitcoinNumber = new BigDecimal(sendAmountBitcoin);
        Currency currency = Currency.getInstance(Locale.getDefault());


        getActionBar().setTitle(getResources().getString(R.string.send) + " " + currency.getSymbol() + sendAmountNumber.toString());

//        ContactsListFragment mContactsListFragment = (ContactsListFragment)
//                getSupportFragmentManager().findFragmentById(R.id.contact_list);


        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toET.setText(getString(R.string.mentors_international_handle));
                }
                else {
                    toET.setText("");
                }
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
        progressDialog.dismiss();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        progressDialog.dismiss();
    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_send, menu);
        menu.getItem(0).setEnabled(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send) {
            BigDecimal amount = round(Float.parseFloat(sendAmount), 2);
            Currency currency = Currency.getInstance(Locale.getDefault());
            final SendConfirmationDialog dialog = new SendConfirmationDialog(this, currency.getSymbol() + String.valueOf(amount), toET.getText().toString());
            dialog.show();
            dialog.getSendButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    progressDialog.setMessage(getResources().getString(R.string.sending_payment));
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
            return true;
        }
//        else if (id == R.id.action_scan) {
//            callScanner();
//            return true;
//        }
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        JSONObject body = new JSONObject();
        try {
            body.put("receiver", toET.getText().toString());
            body.put("amount_local", sendAmount);
            body.put("currency", currency);
//            body.put("target_address", "");
            body.put("note", noteET.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, body,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "send: " + response.toString());
                        try {
                            progressDialog.dismiss();
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

                                Intent intent = new Intent(SendActivity.this, TransactionDetailsActivity.class);
                                intent.putExtra("transaction", transaction);
                                startActivity(intent);
                            }
                            else {
                                JSONArray errors = response.getJSONArray("error");
                                String error = "";
                                for (int i = 0; i < errors.length(); i++) {
                                    error = error + " " + errors.get(i);
                                }

                                Toast.makeText(SendActivity.this, error, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("TEST", "Error: " + error.getMessage());
                Toast.makeText(SendActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
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
                new Intent(this, SendActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }


    private void setWalletAddress(String walletAddress) {
        this.walletAddress = walletAddress;
    }

    private String getWalletAddress() {
      return this.walletAddress;
    }

    private void setReceiverId(String receiverId) { this.receiverId = receiverId; }

    private String getReceiverId() { return this.receiverId; }

    private void returnToPreviousActivitySuccess(String data) {
        Intent i = new Intent();
        Bundle extras = new Bundle();
        extras.putString("data", data);
        i.putExtras(extras);

        setResult(RESULT_OK, i);
        finish();
    }

    private void callScanner() {
        Intent i = new Intent(this, CaptureActivity.class);
        startActivityForResult(i, START_SCANNER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == START_SCANNER_REQUEST)
        {
            // Make sure the request was successful
            if (resultCode == RESULT_OK)
            {
                Bundle extras = data.getExtras();

                if (extras != null)
                {
                    if(extras.containsKey("DecodedData"))
                    {
                        String decodedData = extras.getString("DecodedData");
                        toET.setText(decodedData);
                    }
                    else if(extras.containsKey("Error"))
                    {
                        String error = extras.getString("Error");
                    }


                }
            }
        }
    }

    @Override
    public void onContactSelected(Cursor cursor, int position) {

        if (cursor.moveToPosition(position)) {
            String name = cursor.getString(ContactsListFragment.ContactsQuery.DISPLAY_NAME);
            String email = cursor.getString(ContactsListFragment.ContactsQuery.EMAIL_ADDRESS);
            // To load the photoUri, create an Image Loader and then call .loadImage(photoUri, imageview)
            String photoUri = cursor.getString(ContactsListFragment.ContactsQuery.PHOTO_THUMBNAIL_DATA);
            String phoneNumber = cursor.getString(ContactsListFragment.ContactsQuery.PHONE_NUMBER);

            toET.setText(email);
        }

    }

    @Override
    public void onSelectionCleared() {

    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.your_mfi_container:
                if (checkbox.isChecked()) {
                    checkbox.setChecked(false);
                    toET.setText("");
                }
                else {
                    checkbox.setChecked(true);
                    toET.setText(getString(R.string.mentors_international_handle));
                    toET.setSelection(toET.getText().length());
                }
                break;
        }
    }
}