package com.haedrian.haedrian.UserInteraction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.haedrian.haedrian.Application.AESHelper;
import com.haedrian.haedrian.HomeScreen.SendRequest.SendActivity;
import com.haedrian.haedrian.R;
import com.squareup.picasso.Picasso;

import java.util.concurrent.AbstractExecutorService;

public class PinActivity extends ActionBarActivity {

    private String pin, token;
    private int pinState = 0;
    private String enteredPin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        setContentView(R.layout.activity_pin);

        Bundle extras = getIntent().getExtras();

        this.token = "";
//        this.token = extras.getString("token");

        ImageView appLogo = (ImageView) findViewById(R.id.app_logo);

        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics ();
        display.getMetrics(outMetrics);
        float density  = getResources().getDisplayMetrics().density;

        int width = Math.round((outMetrics.widthPixels / density) - 50);


        Picasso.with(this)
                .load(R.drawable.logobird_black)
                .centerCrop()
                .resize(width, width)
                .into(appLogo);

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
                changePinState();
                return;
            case R.id.button1:
                return;
            case R.id.button2:
                return;
            case R.id.button3:
                return;
            case R.id.button4:
                return;
            case R.id.button5:
                return;
            case R.id.button6:
                return;
            case R.id.button7:
                return;
            case R.id.button8:
                return;
            case R.id.button9:
                return;
            case R.id.buttonDot:
//                addNumberToDisplay(v)
                return;
            case R.id.backspace_button:
                backspace();
                return;


        }
    }

    private void changePinState() {
        switch (pinState) {
            case 0:
                View circle1 = (View) findViewById(R.id.circle1);
                circle1.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 1:
                View circle2 = (View) findViewById(R.id.circle2);
                circle2.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 2:
                View circle3 = (View) findViewById(R.id.circle3);
                circle3.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
            case 3:
                View circle4 = (View) findViewById(R.id.circle4);
                circle4.setBackgroundResource(R.drawable.circle_filled);
                pinState++;
                break;
        }
    }

    private void backspace() {

    }

    private void savePin() {
        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String encryptedToken = null;
        try {
            encryptedToken = AESHelper.encrypt(pin, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        editor.putString("token", encryptedToken);
        editor.commit();
    }
}
