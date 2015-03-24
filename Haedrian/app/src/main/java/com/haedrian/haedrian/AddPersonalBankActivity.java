package com.haedrian.haedrian;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Network.MyJsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.android.volley.Request.*;


public class AddPersonalBankActivity extends ActionBarActivity  {
    Button addBankButton;
    EditText addEmailText;
    EditText addPasswordText;
    TextView bankAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_personal_bank);
        addBankButton = (Button) findViewById(R.id.addBankButton);
        addBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolleyRequest();
            }
        });

        addEmailText = (EditText) findViewById(R.id.addBankEmailEditText);
        addPasswordText = (EditText) findViewById(R.id.addBankPasswordEditText);
        bankAddress = (TextView) findViewById(R.id.newBankText);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_personal_bank, menu);
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
        }

        return super.onOptionsItemSelected(item);
    }

    public void testVolleyRequest(){
        final String URL = "https://blockchain.info/api/v2/create_wallet"
                            + "?password=thisisatestpassword"
                            + "&email=test@haedrian.io"
                            + "&api_code=5a25bea3-7f2f-4a40-acb6-3ed0497d570e";

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Wallet...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            bankAddress.setText(response.getString("address"));
                            progressDialog.hide();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.getMessage());
                progressDialog.hide();
            }
        });


//        JsonObjectRequest req = new JsonObjectRequest(Method.POST,
//                URL, null,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        Log.v("test:", response.toString());
//                        try {
//                            Toast.makeText(getApplicationContext(), "Successfully added a bank", Toast.LENGTH_LONG).show();
//                            bankAddress.setText(response.getString("address"));
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.v("test", error.toString());
//                    }
//        }){
//            @Override
//            protected Map<String,String> getParams(){
//                Map<String,String> params = new HashMap<String, String>();
//                params.put("password", "thisisatestpassword");
//                params.put("email", "thisisatestemail@gmail.com");
//                params.put("api_code", "5a25bea3-7f2f-4a40-acb6-3ed0497d570e");
//
//                return params;
//            }
//
//            @Override
//            public String getBodyContentType() {
//                return "application/x-www-form-urlencoded";
//            }
//        };

        // add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private boolean checkCredentials() {
        return addPasswordText.length() >= 10 && addEmailText.length() > 0;
    }
}
