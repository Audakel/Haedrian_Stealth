package com.haedrian.haedrian.UserInteraction;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends ActionBarActivity {

    private EditText emailET, firstNameET, lastNameET, usernameET, phoneNumberET, passwordET, reenterPasswordET;
    private Spinner countrySpinner;
    private Button submitButton;

    private ArrayList<String> countryNames = new ArrayList<>();
    private ArrayList<String> countryAbbr = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        emailET = (EditText) findViewById(R.id.email_edit_text);
        firstNameET = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameET = (EditText) findViewById(R.id.last_name_edit_text);
        usernameET = (EditText) findViewById(R.id.username_edit_text);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_edit_text);
        passwordET = (EditText) findViewById(R.id.password_edit_text);
        reenterPasswordET = (EditText) findViewById(R.id.reenter_password_edit_text);

        countrySpinner = (Spinner) findViewById(R.id.country_spinner);

        fillCountryInfo();

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countryNames);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setSelection(0);


        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitFormData();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_signup, menu);
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
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void submitFormData() {
        String email = "";
        String firstName = "";
        String lastName = "";
        String username = "";
        String phoneNumber = "";
        String password = "";
        String countryCode = "";

        email = emailET.getText().toString();
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        username = usernameET.getText().toString();
        phoneNumber = phoneNumberET.getText().toString();
        password = passwordET.getText().toString();
        countryCode = countryAbbr.get(countrySpinner.getSelectedItemPosition());


        // TODO: More validation
        if ( ! password.equals(reenterPasswordET.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.passwords_dont_match), Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.email_address_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (countryCode.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.country_code_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.first_name_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastName.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.last_name_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (phoneNumber.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.phone_number_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (username.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.username_required), Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 10) {
            Toast.makeText(this, getResources().getString(R.string.short_password), Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
//        jsonBody.put("firstName", firstName);
//        jsonBody.put("lastName", lastName);
            jsonBody.put("phone", String.valueOf(phoneNumber));
            jsonBody.put("country", countryCode);
            jsonBody.put("password", password);

//            jsonBody.put("username", "jon1");
//            jsonBody.put("email", "jon@gmail.com");
//        jsonBody.put("firstName", firstName);
//        jsonBody.put("lastName", lastName);
//            jsonBody.put("phone", "8016905609");
//            jsonBody.put("country", "US");
//            jsonBody.put("password", "testestest1");
        } catch (JSONException e) {

        }


        createUser(jsonBody);

    }
    public void createUser(JSONObject jsonBody) {
        // params.get("firstName"); etc......
        String url = ApplicationConstants.BASE + "create/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, jsonBody,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        // {"success":true,"token":"41dd3b67f49511f7e6b95f85686b2882dc875709"}

                        try {
                            if (response.getString("success").equals("true")) {

                                String token = response.getString("token");

                                Intent intent = new Intent(SignupActivity.this, PinActivity.class);
                                intent.putExtra("token", token);
                                startActivity(intent);
                                finish();
                            }
                            else if (response.getString("success").equals("false")) {
                                String errorMessage = response.getString("error");
                                Toast.makeText(SignupActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("TEST2", error.getMessage());
//                Toast.makeText(SignupActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adds request to the request queue
        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void fillCountryInfo() {
        countryNames.add(getResources().getString(R.string.country_spinner_hint));
        countryNames.add(getResources().getString(R.string.united_states));
        countryNames.add(getResources().getString(R.string.phillipines));

        countryAbbr.add("");
        countryAbbr.add(getResources().getString(R.string.united_states_abbr));
        countryAbbr.add(getResources().getString(R.string.phillipines_country_code));
    }
}
