package com.haedrian.haedrian;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


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
        final String URL = "https://blockchain.info/api/v2/create_wallet";
        HashMap<String, String> params = new HashMap<String, String>();

//        if (!checkCredentials()) {
//            Toast.makeText(getApplicationContext(), "Error on credentials", Toast.LENGTH_LONG).show();
//            return;
//        }

//        params.put("password", addPasswordText.getText().toString());
//        params.put("email", addEmailText.getText().toString());
        params.put("password", "thisisatestpassword");
        params.put("email", "thisisatestemail");
        params.put("api_code", "5a25bea3-7f2f-4a40-acb6-3ed0497d570e");
        //params.put("priv", "AbCdEfGh123456");
        //params.put("label", "AbCdEfGh123456");

        // pass second argument as "null" for GET requests
        JsonObjectRequest req = new JsonObjectRequest(URL, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Toast.makeText(getApplicationContext(), "Successfully added a bank", Toast.LENGTH_LONG).show();
                            bankAddress.setText(response.getString("address"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error yo: ", error.getMessage());
            }
        });

// add the request object to the queue to be executed
        ApplicationController.getInstance().addToRequestQueue(req);

    }

    private boolean checkCredentials() {
        return addPasswordText.length() >= 10 && addEmailText.length() > 0;
    }
}
