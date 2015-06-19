package com.haedrian.haedrian.UserInteraction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.AESHelper;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.haedrian.haedrian.HomeScreen.SendRequest.SendActivity;
import com.haedrian.haedrian.R;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.AbstractExecutorService;

public class PinActivity extends Activity {

    private String token;
    private int pinState = 0;
    private int pinAttempts = 0;
    private String enteredPin = "";
    private String reenteredPin = "";

    private View circle1, circle2, circle3, circle4;
    private TextView userPrompt, signOut;
    private Resources res;

    // Create state is for creating, reenter is to reenter right after creating and enter is used every time after for login
    private enum State {
        Create,
        Reenter,
        Enter
    }

    private State currentPinState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().hide();
        setContentView(R.layout.activity_pin);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        String pinStateValue = sp.getString("pin_state", "");

        // If there is no overallPinstate then have them enter a new one
        if (pinStateValue.equals("")) {
            currentPinState = State.Create;
        }
        else if (pinStateValue.equals("enter")) {
            currentPinState = State.Enter;
        }

        this.token = ApplicationController.getToken();

        ImageView appLogo = (ImageView) findViewById(R.id.app_logo);
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);
        userPrompt = (TextView) findViewById(R.id.user_prompt);
        signOut = (TextView) findViewById(R.id.sign_out);

        res = getResources();
        if (currentPinState.equals(State.Create)) {
            userPrompt.setText(res.getString(R.string.create_pin));
        }
        else if (currentPinState.equals(State.Reenter)) {
            userPrompt.setText(res.getString(R.string.reenter_pin));
        }
        else if (currentPinState.equals(State.Enter)) {
            userPrompt.setText(res.getString(R.string.enter_pin));
            signOut.setVisibility(View.VISIBLE);
        }


        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;

        int height = Math.round((outMetrics.heightPixels / density) / 4);


        Picasso.with(this)
                .load(R.drawable.logo)
                .centerCrop()
                .resize(height, height)
                .into(appLogo);

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
        FlurryAgent.logEvent("Incorrect pin attempts: " + pinAttempts);
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_pin, menu);
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

    public void onClick(View selectedButton) {

        switch (selectedButton.getId()) {
            case R.id.button0:
                changeEnteredPin("0");
                changePinState();
                break;
            case R.id.button1:
                changeEnteredPin("1");
                changePinState();
                break;
            case R.id.button2:
                changeEnteredPin("2");
                changePinState();
                break;
            case R.id.button3:
                changeEnteredPin("3");
                changePinState();
                break;
            case R.id.button4:
                changeEnteredPin("4");
                changePinState();
                break;
            case R.id.button5:
                changeEnteredPin("5");
                changePinState();
                break;
            case R.id.button6:
                changeEnteredPin("6");
                changePinState();
                break;
            case R.id.button7:
                changeEnteredPin("7");
                changePinState();
                break;
            case R.id.button8:
                changeEnteredPin("8");
                changePinState();
                break;
            case R.id.button9:
                changeEnteredPin("9");
                changePinState();
                break;
            case R.id.backspace_button:
                backspace();
                if (currentPinState.equals(State.Create) || currentPinState.equals(State.Enter)) {
                    if (enteredPin.length() > 0) {
                        enteredPin = enteredPin.substring(0, enteredPin.length() - 1);
                    }
                }
                else if (currentPinState.equals(State.Reenter)) {
                    if (reenteredPin.length() > 0) {
                        reenteredPin = reenteredPin.substring(0, reenteredPin.length() - 1);
                    }
                }

                break;
            case R.id.sign_out:
                signOut();
                break;
        }
    }

    private void signOut() {
        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("token", "");
        // This is so that it will prompt the user to enter in the pin on app startup
        editor.putString("pin_state", "");

        ApplicationController.setToken("");

        editor.commit();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void changeEnteredPin(String value) {
        if (currentPinState.equals(State.Create) || currentPinState.equals(State.Enter)) {
            if (enteredPin.length() < 4) {
                enteredPin += value;
            }
        }
        else if (currentPinState.equals(State.Reenter)) {
            if (reenteredPin.length() < 4) {
                reenteredPin += value;
            }
        }
    }

    private void changePinState() {
        switch (pinState) {
            case 0:
                circle1.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 1:
                circle2.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 2:
                circle3.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 3:
                circle4.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                if (currentPinState.equals(State.Create)) {
                    changeState(State.Reenter);
                }
                else if (currentPinState.equals(State.Reenter)) {
                    validateAndSetPin();
                }
                else if (currentPinState.equals(State.Enter)) {
                    validatePin();
                }

                break;
        }
    }

    private void backspace() {
        switch (pinState) {
            case 1:
                circle1.setBackgroundResource(R.drawable.circle_default);
                pinState--;
                break;
            case 2:
                circle2.setBackgroundResource(R.drawable.circle_default);
                pinState--;
                break;
            case 3:
                circle3.setBackgroundResource(R.drawable.circle_default);
                pinState--;
                break;
            case 4:
                circle4.setBackgroundResource(R.drawable.circle_default);
                pinState--;
                break;
        }
    }

    private void changeState(State state) {
        currentPinState = state;

        // "clear" the pinpad
        circle1.setBackgroundResource(R.drawable.circle_default);
        circle2.setBackgroundResource(R.drawable.circle_default);
        circle3.setBackgroundResource(R.drawable.circle_default);
        circle4.setBackgroundResource(R.drawable.circle_default);

        pinState = 0;

        if (state.equals(State.Create)) {
            userPrompt.setText(res.getString(R.string.create_pin));
            enteredPin = "";
            reenteredPin = "";
        }
        else if (state.equals(State.Reenter)) {
            userPrompt.setText(res.getString(R.string.reenter_pin));
        }
        else if (state.equals(State.Enter)) {
            enteredPin = "";
        }

    }

    private void validateAndSetPin() {
        if (enteredPin.equals(reenteredPin)) {
            savePin();
            Intent intent = new Intent(this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            Toast.makeText(this, res.getString(R.string.pins_dont_match), Toast.LENGTH_LONG).show();
            changeState(State.Create);
        }
    }

    private void savePin() {
        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String encryptedToken = null;

        token = "token:" + token;

        try {
            encryptedToken = AESHelper.encrypt(enteredPin, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString("token", encryptedToken);
        // This is so that it will prompt the user to enter in the pin on app startup
        editor.putString("pin_state", "enter");
        editor.commit();
    }

    private void validatePin() {
        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        String storedToken = sp.getString("token", "");
        String decryptedToken = null;

        if ( ! storedToken.equals("")) {
            try {
                decryptedToken = AESHelper.decrypt(enteredPin, storedToken);
            } catch (Exception e) {
                FlurryAgent.logEvent("User incorrectly entered pin");
                pinAttempts++;
                // Incorrect pin
                Toast.makeText(this, res.getString(R.string.incorrect_pin), Toast.LENGTH_LONG).show();
                changeState(State.Enter);
            }
        }

        if (decryptedToken != null) {
            if (decryptedToken.contains("token:")) {
                String userToken = decryptedToken.substring(6, decryptedToken.length());
                ApplicationController.setToken(userToken);
                // Pin is correct
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                // Pin not correct
                Toast.makeText(this, res.getString(R.string.incorrect_pin), Toast.LENGTH_LONG).show();
                changeState(State.Enter);
            }
        }
    }

}
