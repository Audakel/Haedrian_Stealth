package com.haedrian.haedrian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import static com.android.volley.Request.Method;

public class SendActivity extends ActionBarActivity {

    private final String base = "https://blockchain.info/merchant/$guid/";
    private String sendAmount, toUser;
    private Button sendButton, cancelButton;
    private TextView amountTV, toUserTV, totalAmountTV, sendSuccessTV;
    private LinearLayout sendLayout;
    private RelativeLayout sendSuccessLayout;
    private ImageView sendSuccessImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sendButton = (Button) findViewById(R.id.send_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        amountTV = (TextView) findViewById(R.id.review_amount);
        toUserTV = (TextView) findViewById(R.id.to_user);
        totalAmountTV = (TextView) findViewById(R.id.total_amount);
        sendSuccessTV = (TextView) findViewById(R.id.send_success_text);

        sendLayout = (LinearLayout) findViewById(R.id.send_layout);
        sendSuccessLayout = (RelativeLayout) findViewById(R.id.send_success_layout);

        sendSuccessImage = (ImageView) findViewById(R.id.send_success_image);

        sendAmount = "$" + getIntent().getStringExtra("send_amount");
        toUser = getIntent().getStringExtra("to_user");

        amountTV.setText(sendAmount);
        totalAmountTV.setText(sendAmount);
        toUserTV.setText(toUser);

        /*  Button Listeners  */
        sendButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                sendTransaction();

                Picasso.with(SendActivity.this)
                        .load(R.drawable.success)
                        .resize(500, 500)
                        .centerCrop()
                        .into(sendSuccessImage);

                String sendString = "Successfully sent "
                        + sendAmount
                        + " to "
                        + toUser
                        + ".";

                sendSuccessTV.setText(sendString);

                sendLayout.setVisibility(View.GONE);
                sendSuccessLayout.setVisibility(View.VISIBLE);

            }
        });
        cancelButton.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Intent intent;
                intent = new Intent(SendActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_send_request, menu);
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

    // Function that does the API call to send
    public void sendTransaction() {
        String URL = base
                + "payment?password=" + "password"
                + "&second_password=" + "secondPassword"
                + "&to=" + "address"
                + "&amount=" + "amount"
                + "&from=" + "from"
                + "&fee=" + "fee"
                + "&note=" + "note";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Sending Payment...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("Test", response.toString());
                        progressDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest, "json_obj_req");
    }
}