package com.haedrian.haedrian;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

public class RequestActivity extends ActionBarActivity{

    private String requestAmount, fromUser;
    private Button requestButton, cancelButton;
    private TextView amountTV, fromUserTV, totalAmountTV, requestSuccessTV;
    private LinearLayout requestLayout;
    private RelativeLayout successLayout;
    private ImageView requestSuccessImage;


    private final String base = "https://blockchain.info/merchant/$guid/";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        requestButton = (Button) findViewById(R.id.request_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);

        amountTV = (TextView) findViewById(R.id.review_request_amount);
        fromUserTV = (TextView) findViewById(R.id.from_user);
        totalAmountTV = (TextView) findViewById(R.id.total_request_amount);
        requestSuccessTV = (TextView) findViewById(R.id.request_success_text);

        requestLayout = (LinearLayout) findViewById(R.id.request_layout);
        successLayout = (RelativeLayout) findViewById(R.id.success_layout);

        requestSuccessImage = (ImageView) findViewById(R.id.request_success_image);


        requestAmount = "$" + getIntent().getStringExtra("request_amount");
        fromUser = getIntent().getStringExtra("from_user");

        amountTV.setText(requestAmount);
        totalAmountTV.setText(requestAmount);
        fromUserTV.setText(fromUser);

        /*  Button Listeners  */
        requestButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {

                requestTransaction();

                Picasso.with(RequestActivity.this)
                        .load(R.drawable.success)
                        .resize(500, 500)
                        .centerCrop()
                        .into(requestSuccessImage);

                String requestString = "Successfully requested "
                                        + requestAmount
                                        + " from "
                                        + fromUser
                                        + ".";

                requestSuccessTV.setText(requestString);

                requestLayout.setVisibility(View.GONE);
                successLayout.setVisibility(View.VISIBLE);

            }});
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent;
                intent = new Intent(RequestActivity.this, HomeActivity.class);
                startActivity(intent);
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_request_request, menu);
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

    public void requestTransaction() {
        String URL = base;


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Requesting Payment...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
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