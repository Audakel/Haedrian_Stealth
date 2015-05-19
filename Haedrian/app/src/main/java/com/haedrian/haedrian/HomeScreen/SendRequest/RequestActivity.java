package com.haedrian.haedrian.HomeScreen.SendRequest;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.Contacts.ContactsListFragment;
import com.haedrian.haedrian.CustomDialogs.RequestConfirmationDialog;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.Scanner.CaptureActivity;

import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class RequestActivity extends ActionBarActivity implements
        ContactsListFragment.OnContactsInteractionListener {

    private final String base = "https://blockchain.info/merchant/$guid/";
    private String requestAmount, requestAmountBitcoin, fromUser, userId,  walletAddress, parseId, receiverId;
    private EditText fromET;
    private BigDecimal requestAmountNumber, requestAmountBitcoinNumber;
    private int bitcoinBuy, bitcoinSell;
    private ProgressDialog progressDialog;
    private final static int START_SCANNER_REQUEST = 1;
    private final static String  PENDING_STATUS = "m5jO6Rz54h";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestAmount = getIntent().getStringExtra("request_amount");
        requestAmountBitcoin = getIntent().getStringExtra("request_amount_bitcoin");
        fromUser = getIntent().getStringExtra("from_user");
        bitcoinBuy = getIntent().getIntExtra("bitcoin_buy", 0);
        bitcoinSell = getIntent().getIntExtra("bitcoin_sell", 0);

        fromET = (EditText) findViewById(R.id.from_edittext);

        progressDialog = new ProgressDialog(this);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        parseId = sp.getString("parse_id", "");


        requestAmountNumber = round(Float.parseFloat(requestAmount), 2);
        requestAmountBitcoinNumber = new BigDecimal(requestAmountBitcoin);

        getSupportActionBar().setTitle("Request $" + requestAmountNumber.toString());

    }

    public static BigDecimal round(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send_request, menu);
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
            progressDialog.show();
            requestTransaction();
            return true;
        }
        else if (id == R.id.action_scan) {
            callScanner();
            return true;
        }
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestTransaction() {
        BigDecimal amount = round(Float.parseFloat(requestAmount), 2);
        final RequestConfirmationDialog dialog = new RequestConfirmationDialog(this, "$" + String.valueOf(amount), fromET.getText().toString());
        final String finalFrom = fromET.getText().toString();
        dialog.show();
        dialog.getSendButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                progressDialog.setMessage("Sending Payment...");
                progressDialog.show();
                requestPayment();
            }
        });
        dialog.getCancelButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    private void requestPayment() {
        Map<String, String> params = new HashMap<>();
        params.put("requestor", "recipient");
        params.put("requestee", "requestee");
        params.put("amount", "amount");
        params.put("note", "note");

        requestMoney(params);
    }

    private void requestMoney(Map<String, String> params) {
        String url = ApplicationConstants.BASE + "";
        final Map<String, String> finalParams = params;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() {
                return finalParams;
            }
        };

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }


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
                        fromET.setText(decodedData);
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

            fromET.setText(email);
        }

    }

    @Override
    public void onSelectionCleared() {

    }
}