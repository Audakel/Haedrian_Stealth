package com.haedrian.haedrian;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


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
        getMenuInflater().inflate(R.menu.menu_signup, menu);
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

        // If everything passes
        final ParseUser user = new ParseUser();
        user.setUsername(email);
        user.setEmail(email);
        user.setPassword(password);
        user.put("firstName", firstName);
        user.put("lastName", lastName);
        user.put("phoneNumber", phoneNumber);
        user.put("handle", username);
        user.put("creditScore", 0);

        final String tempPass = password;
        final String tempEmail = email;
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sp.edit();
                    editor.putString("secret", tempPass);
                    editor.putString("email", tempEmail);
                    editor.commit();

                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    intent.putExtra("parse_id", user.getObjectId());
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
