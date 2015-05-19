package com.haedrian.haedrian.UserInteraction;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class SignupActivity extends ActionBarActivity {

    private EditText emailET, firstNameET, lastNameET, usernameET, phoneNumberET, passwordET, reenterPasswordET;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        emailET = (EditText) findViewById(R.id.email_edit_text);
        firstNameET = (EditText) findViewById(R.id.first_name_edit_text);
        lastNameET = (EditText) findViewById(R.id.last_name_edit_text);
        usernameET = (EditText) findViewById(R.id.username_edit_text);
        phoneNumberET = (EditText) findViewById(R.id.phone_number_edit_text);
        passwordET = (EditText) findViewById(R.id.password_edit_text);
        reenterPasswordET = (EditText) findViewById(R.id.reenter_password_edit_text);

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

        return super.onOptionsItemSelected(item);
    }

    private void submitFormData() {
        String email = "";
        String firstName = "";
        String lastName = "";
        String username = "";
        long phoneNumber = 0;
        String password = "";

        email = emailET.getText().toString();
        firstName = firstNameET.getText().toString();
        lastName = lastNameET.getText().toString();
        username = usernameET.getText().toString();
        phoneNumber = Long.parseLong(phoneNumberET.getText().toString());
        password = passwordET.getText().toString();

        // TODO: More validation
        if ( ! password.equals(reenterPasswordET.getText().toString())) {
            Toast.makeText(this, "Passwords don't match!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email == "") {
            Toast.makeText(this, "Email address is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (firstName == "") {
            Toast.makeText(this, "First name is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (lastName == "") {
            Toast.makeText(this, "Last name is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (username == "") {
            Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 10) {
            Toast.makeText(this, "Password is too short!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("firstName", firstName);
        params.put("lastName", lastName);
        params.put("username", username);
        params.put("password", password);

        createUser(params);

    }
    public void createUser(Map<String, String> params) {
        // params.get("firstName"); etc......
        String url = ApplicationConstants.BASE + "";

        final Map<String, String> finalParams = params;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
//                        if (userId != -1) {
//                            SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
//                            SharedPreferences.Editor editor = sp.edit();
//                            editor.putString("email", email);
//                            editor.putInt("user_id", userId);
//                            editor.commit();
//
//                            Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
//                            startActivity(intent);
//                            finish();
//                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignupActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
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
}
