package com.haedrian.haedrian;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.CustomDialogs.SendConfirmationDialog;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.WalletModel;
import com.haedrian.haedrian.Scanner.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static com.android.volley.Request.Method;

public class SendActivity extends ActionBarActivity {

    private final String base = "https://blockchain.info/merchant/$guid/";
    private String sendAmount, sendAmountBitcoin;
    private EditText toET, noteET;
    private WalletModel wallet;

    private static final int START_SCANNER_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sendAmount = getIntent().getStringExtra("send_amount");
        sendAmountBitcoin = getIntent().getStringExtra("send_amount_bitcoin");
        toET = (EditText) findViewById(R.id.to_edittext);
        noteET = (EditText) findViewById(R.id.note_edittext);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);

        DBHelper db = new DBHelper(this);
        wallet = db.getWalletsTable().selectByUserId(userId);

        BigDecimal amount = round(Float.parseFloat(sendAmount), 2);

        getSupportActionBar().setTitle("Send $" + amount.toString());

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
            BigDecimal amount = round(Float.parseFloat(sendAmount), 2);
            final SendConfirmationDialog dialog = new SendConfirmationDialog(this, "$" + String.valueOf(amount), toET.getText().toString());
            dialog.show();
            dialog.getSendButton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sendTransaction();
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

    // Function that does the API call to send
    public void sendTransaction() {

        String noteString = "";
        if (noteET.getText().toString() != "") {
            noteString = "&note=" + noteET.getText().toString();
        }

        String passwordString = "payment?password=" + "password";

        String recipientString = "";
        if (toET.getText().toString().equals("")) {
            // Throw Error
            Toast.makeText(this, "Please specify a recipient.", Toast.LENGTH_SHORT).show();
            return;
        }
        else {
            recipientString = "&to=" + toET.getText().toString();
        }

        String addressString = "&from=" + wallet.getAddress();
        addressString = addressString.replaceAll("\\s+", "");

        String URL = base
                + passwordString
                + recipientString
                + "&amount=" + sendAmountBitcoin
                + addressString
                + noteString;

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Payment...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("error")) {
                                progressDialog.hide();
                                Toast.makeText(SendActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
                            }
                            else {
                                progressDialog.hide();
                                returnToPreviousActivitySuccess(response.getString("message"));
                            }
                        } catch (JSONException e) {
                            Toast.makeText(SendActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.hide();
                Toast.makeText(SendActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
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

}