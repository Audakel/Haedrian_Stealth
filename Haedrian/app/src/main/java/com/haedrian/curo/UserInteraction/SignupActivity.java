package com.haedrian.curo.UserInteraction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.curo.Application.ApplicationConstants;
import com.haedrian.curo.Application.ApplicationController;
import com.haedrian.curo.R;
import com.haedrian.curo.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignupActivity extends ActionBarActivity {

    private EditText emailET, usernameET, phoneNumberET, microfinanceIdET, passwordET, reenterPasswordET;
    private Spinner microfinanceSpinner, countrySpinner;
    private Button submitButton;
    private LinearLayout microfinanceIdContainer;

    private ArrayList<String> microfinanceInstitutions = new ArrayList<>();
    private ArrayList<String> microfinanceAbbr = new ArrayList<>();

    private ArrayList<String> countryNames = new ArrayList<>();
    private ArrayList<String> countryAbbr = new ArrayList<>();
    private ArrayList<String> countryCodes = new ArrayList<>();

    private TextView countryCodeTV;
    private ProgressDialog progressDialog;

    private String countryCode;
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.setCancelable(false);

        scrollView = (ScrollView) findViewById(R.id.scrollview);
        emailET = (EditText) findViewById(R.id.email_edit_text);
        usernameET = (EditText) findViewById(R.id.username_edit_text);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_edit_text);
        passwordET = (EditText) findViewById(R.id.password_edit_text);
        reenterPasswordET = (EditText) findViewById(R.id.reenter_password_edit_text);
        countryCodeTV = (TextView) findViewById(R.id.country_code);
        microfinanceIdET = (EditText) findViewById(R.id.microfinance_id);
        microfinanceSpinner = (Spinner) findViewById(R.id.microfinance_institution);

        microfinanceIdContainer = (LinearLayout) findViewById(R.id.microfinance_id_container);

        countrySpinner = (Spinner) findViewById(R.id.country_spinner);

        fillCountryInfo();
        fillMicrofinanceInfo();

        ArrayAdapter<String> countryAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, countryNames);
        countrySpinner.setAdapter(countryAdapter);
        countrySpinner.setSelection(0);
        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update country code on the phone number
                countryCodeTV.setText(countryCodes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submitButton = (Button) findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.show();
                submitFormData();
            }
        });

        ArrayAdapter<String> mfiAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, microfinanceInstitutions);
        microfinanceSpinner.setAdapter(mfiAdapter);
        microfinanceSpinner.setSelection(0);
        microfinanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    microfinanceIdContainer.setVisibility(View.VISIBLE);
                    microfinanceIdET.setHint(microfinanceInstitutions.get(position) + " " + getString(R.string.id) + "*");
                }
                else {
                    microfinanceIdContainer.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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

        String email = emailET.getText().toString();
        String username = usernameET.getText().toString();
        String phoneNumber = phoneNumberET.getText().toString();
        String password = passwordET.getText().toString();
        countryCode = countryAbbr.get(countrySpinner.getSelectedItemPosition());

        String application = "";
        String appExternalId = "";
        if (microfinanceSpinner.getSelectedItemPosition() != 0) {
            application = microfinanceAbbr.get(microfinanceSpinner.getSelectedItemPosition());
            appExternalId = microfinanceIdET.getText().toString();
            if (appExternalId.equals("")) {
                microfinanceIdET.setError(getString(R.string.no_mfi_id));
                progressDialog.dismiss();
                scrollView.pageScroll(View.FOCUS_UP);
                return;
            }
        }

        Matcher matcher;

        /*
         * Username validation
         */

        if (username.equals("")) {
//            Toast.makeText(this, getResources().getString(R.string.username_required), Toast.LENGTH_LONG).show();
            usernameET.setError(getString(R.string.username_required));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }
        Pattern usernamePattern = Pattern.compile("^[a-z0-9_-]{3,30}$");
        matcher = usernamePattern.matcher(username);
        if ( ! matcher.matches()) {
            usernameET.setError(getString(R.string.invalid_username));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        /*
         * Email validation
         */

        // Empty password
        if (email.equals("")) {
//            Toast.makeText(this, getResources().getString(R.string.email_address_required), Toast.LENGTH_LONG).show();
            emailET.setError(getString(R.string.email_address_required));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }
        // Valid email
        Pattern pattern = Pattern.compile(getString(R.string.email_validation));
        matcher = pattern.matcher(email);
        if ( ! matcher.matches()) {
            emailET.setError(getString(R.string.invalid_email));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        /*
         * Country code validation
         */

        // Just makes sure that something was selected
        if (countryCode.equals("")) {
            Toast.makeText(this, getResources().getString(R.string.country_code_required), Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        /*
         * Phone number validation
         */

        // Not empty
        if (phoneNumber.equals("")) {
//            Toast.makeText(this, getResources().getString(R.string.phone_number_required), Toast.LENGTH_LONG).show();
            phoneNumberET.setError(getString(R.string.phone_number_required));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        phoneNumber = phoneNumber.replaceAll("[^\\d]", "");
        phoneNumberET.setText(phoneNumber);
        phoneNumber = countryCodes.get(countrySpinner.getSelectedItemPosition()) + phoneNumber;
        if ( ! PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber)) {
            phoneNumberET.setError(getString(R.string.invalid_phone_number));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        /*
         * Password validation
         */

        // Short password
        if (password.length() < 8) {
//            Toast.makeText(this, getResources().getString(R.string.short_password), Toast.LENGTH_LONG).show();
            passwordET.setError(getString(R.string.short_password));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        if ( ! password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])([a-zA-Z0-9]+)$")) {
            passwordET.setError(getString(R.string.password_char_number));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        // Not matching password
        if ( ! password.equals(reenterPasswordET.getText().toString())) {
//            Toast.makeText(this, getResources().getString(R.string.passwords_dont_match), Toast.LENGTH_LONG).show();
            reenterPasswordET.setError(getString(R.string.passwords_dont_match));
            progressDialog.dismiss();
            scrollView.pageScroll(View.FOCUS_UP);
            return;
        }

        JSONObject jsonBody = new JSONObject();

        try {
            jsonBody.put("username", username);
            jsonBody.put("email", email);
            jsonBody.put("phone", String.valueOf(phoneNumber));
            jsonBody.put("country", countryCode);
            jsonBody.put("password1", password);

            if (microfinanceSpinner.getSelectedItemPosition() != 0) {
                jsonBody.put("application", application);
                jsonBody.put("app_id", appExternalId);
            }
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
                        Log.v("TEST", "Signup: " + response.toString());
                        // {"success":true,"token":"41dd3b67f49511f7e6b95f85686b2882dc875709"}

                        try {
                            if (response.getString("success").equals("true")) {

                                String token = response.getString("token");
                                ApplicationController.setToken(token);

                                progressDialog.dismiss();
                                Intent intent = new Intent(SignupActivity.this, PinActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else if (response.getString("success").equals("false")) {
                                String error = response.getString("error");
                                progressDialog.dismiss();
                                Toast.makeText(SignupActivity.this, error, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(SignupActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignupActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_LONG).show();
                progressDialog.dismiss();

            }
        });

        jsonObjectRequest.setRetryPolicy(new TimeoutRetryPolicy());

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

        countryCodes.add("");
        countryCodes.add("+1");
        countryCodes.add("+63");
    }

    private void fillMicrofinanceInfo() {
        microfinanceInstitutions.add(getString(R.string.microfinance_institution_optional));
        microfinanceInstitutions.add(getString(R.string.mentors_international));

        microfinanceAbbr.add("");
        microfinanceAbbr.add("MENTORS");
    }
}
