package com.haedrian.haedrian;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.haedrian.haedrian.CustomDialogs.RequestConfirmationDialog;
import com.haedrian.haedrian.CustomDialogs.SendConfirmationDialog;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.haedrian.haedrian.Scanner.CaptureActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;

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
                getUserId(finalFrom);
                dialog.dismiss();
            }
        });
        dialog.getCancelButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    // This method gets the userid from the person you are requesting from
    private void getUserId(final String email) {

        // This method will have to do some serious regex to determine what to query off of. Right now it just assumes email

        ParseQuery<ParseObject> emailQuery = ParseQuery.getQuery("_User");
        emailQuery.whereEqualTo("email", email);

        emailQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> results, ParseException e) {
                if (e == null) {
                    if (results.size() > 0) {
                        userId = results.get(0).getObjectId();
                        createRequestTransaction(true, "Request money from " + email + " successful.");
                    }
                    else {
                        // TODO: Call to fire off email
                        progressDialog.hide();
                        returnToPreviousActivitySuccess("Sent email to: " + email + " to request funds.");
                    }
                }
                else {
                    progressDialog.hide();
                    createRequestTransaction(false, "Request money from " + userId + " failed.");
                }
            }
        });
    }

    private void createRequestTransaction(boolean wasSuccessful, String message) {

        ParseObject parseRequest = new ParseObject("Request");
        parseRequest.put("requestorId", parseId);
        parseRequest.put("requesteeId", userId);
        parseRequest.put("amountBitcoin", requestAmountBitcoinNumber);
        parseRequest.put("amountCurrency", requestAmountNumber);
        parseRequest.put("currentBuyRate", bitcoinBuy);
        parseRequest.put("currentSellRate", bitcoinSell);
        parseRequest.put("fulfillmentStatusId", PENDING_STATUS);
        parseRequest.saveInBackground();

        progressDialog.hide();
        returnToPreviousActivitySuccess(message);
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