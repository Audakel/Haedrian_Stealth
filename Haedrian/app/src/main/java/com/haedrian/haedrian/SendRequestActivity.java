package com.haedrian.haedrian;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SendRequestActivity extends ActionBarActivity {
    private String TAG = SendRequestActivity.class.getSimpleName();
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonDot, buttonBack, buttonSend, buttonRequest;
    private TextView displayNumber;
    private TextView dolarSignView;
    private Context context;
    private LinearLayout errorLayout;
    private RequestQueue queue;
    private TextView bitcoinAmount;
    private int currentBitcoinPrice = 0;

    // Data for all views
    String displayNumberText = "0";
    String bitcoinAmountText = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        context = getApplication();

        getCurrencyInfo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeViews();



    }

    private void updateDisplay(){
        displayNumber.setText(displayNumberText);
        bitcoinAmount.setText(bitcoinAmountText);
        updateFontAttributes();
    }

    public void onClick(View view) {
        addNumberToDisplay(view);
        getConvertedRateInstantly(displayNumberText);
        updateDisplay();
    }


    // ================ Init functions ================
    private void initializeViews() {
        button0 = (Button) findViewById(R.id.button0);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button6 = (Button) findViewById(R.id.button6);
        button7 = (Button) findViewById(R.id.button7);
        button8 = (Button) findViewById(R.id.button8);
        button9 = (Button) findViewById(R.id.button9);
        buttonDot = (Button) findViewById(R.id.buttonDot);
        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonRequest = (Button) findViewById(R.id.buttonRequest);
        displayNumber = (TextView) findViewById(R.id.displayNumberView);
        dolarSignView = (TextView) findViewById(R.id.dollarSignView);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        bitcoinAmount = (TextView) findViewById(R.id.bitcoin_amount);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK)
        {
            Bundle extras = data.getExtras();

            if (extras != null)
            {

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
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
    // ================ End Init functions ================



    // ================ Screen update functions ================
    private void clear() {
        displayNumberText = "0";
        bitcoinAmountText = "0";
    }

    private void backspace() {
        String string = displayNumber.getText().toString();
        if (string.length() > 1) {
            formatBackspaceNormalNumber(string.substring(0, string.length() - 1));
        } else {

            displayNumberText = "0";
            bitcoinAmountText = "0";
        }
    }

    private void setDisplayNumberText(String number) {
        int displayLength = displayNumber.getText().length();

        if (displayNumber.getText().toString().equals("0")) {
            if(number.equals(".")){
                displayNumberText = "0" + number;
                return;
            }
            displayNumberText = number;
        }
        else if (number.equals(".")) {
            formatDecimalNumber(number);
        }
        else if (displayLength < 6) {
            formatNormalNumber(number);
        }
        else if (displayLength == 6 && number.equals(".")) {
            displayNumberText = displayNumberText + number;
        }
        else if (displayLength > 6 && displayLength <= 8) {
            formatNormalNumber(number);
        }
        else if (displayLength == 6 || displayLength > 8) {
            vibrateErrorAnimation();
        }

    }

    private void addNumberToDisplay(View selectedButton) {

        switch (selectedButton.getId()) {
            case R.id.button0:
                setDisplayNumberText("0");
                return;
            case R.id.button1:
                setDisplayNumberText("1");
                return;
            case R.id.button2:
                setDisplayNumberText("2");
                return;
            case R.id.button3:
                setDisplayNumberText("3");
                return;
            case R.id.button4:
                setDisplayNumberText("4");
                return;
            case R.id.button5:
                setDisplayNumberText("5");
                return;
            case R.id.button6:
                setDisplayNumberText("6");
                return;
            case R.id.button7:
                setDisplayNumberText("7");
                return;
            case R.id.button8:
                setDisplayNumberText("8");
                return;
            case R.id.button9:
                setDisplayNumberText("9");
                return;
            case R.id.buttonDot:
                setDisplayNumberText(".");
//                addNumberToDisplay(v)
                return;
            case R.id.buttonBack:
                backspace();
                return;
            case R.id.buttonSend:
                Intent intent = new Intent(SendRequestActivity.this, SendActivity.class);
                intent.putExtra("send_amount", displayNumber.getText().toString());
                intent.putExtra("send_amount_bitcoin", bitcoinAmount.getText().toString());
                startActivity(intent);
                return;
            case R.id.buttonRequest:
                clear();
                return;
        }
    }

    private void updateFontAttributes() {
        if (displayNumber.getText().toString().equals("0")) {
            displayNumber.setTextColor(getResources().getColor(R.color.light_blue));
        } else {
            displayNumber.setTextColor(getResources().getColor(R.color.blue));
        }
        switch (displayNumber.length()) {
            case 1:
                displayNumber.setTextSize(120);
                dolarSignView.setTextSize(70);
                return;
            case 2:
                displayNumber.setTextSize(100);
                dolarSignView.setTextSize(50);
                return;
            case 3:
                displayNumber.setTextSize(80);
                dolarSignView.setTextSize(50);
                return;
            case 4:
                displayNumber.setTextSize(70);
                dolarSignView.setTextSize(40);
                return;
            case 5:
                displayNumber.setTextSize(60);
                dolarSignView.setTextSize(30);
                return;

        }
    }
    // ================ End Screen update functions ================



    // ================ Helper functions ================
    private void getCurrencyInfo() {
        // Creating volley request obj
        String url = "https://blockchain.info/ticker";
        JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // Parsing json
                        try {
                            JSONObject currentCurrency = response.getJSONObject("USD");
                            currentBitcoinPrice = currentCurrency.getInt("buy");
                            getConvertedRateInstantly(displayNumber.getText().toString());
                            updateDisplay();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(currencyRequest);
    }

    public void getConvertedRateInstantly(String sendAmount) {
        String cleanSendAmount = removeCommas(sendAmount);
        if (currentBitcoinPrice == 0){
            bitcoinAmountText = "Loading...";
        }
        else{
            double balance = (Double.parseDouble(cleanSendAmount) * 1.000000) / currentBitcoinPrice;
            Double roundedBalance = new BigDecimal(balance).setScale(6, BigDecimal.ROUND_HALF_UP).doubleValue();
            bitcoinAmountText = roundedBalance + "";
        }

    }

    private String removeCommas(String number) {
        String newNumber = number.replaceAll(",", "");
        return newNumber;
    }

    private boolean checkForDoublePeriods(String number) {
        Pattern p = Pattern.compile("\\.\\d*\\.");
        Matcher m = p.matcher(number);
        return m.find();
    }

    // Fringe case of .0 getting formated as 0 so you can never procede with the number
    private boolean checkForDotZeroCase(String number) {
        Pattern p = Pattern.compile("\\.0$");
        Matcher m = p.matcher(number);
        return m.find();
    }

    // Fringe case of .0 getting formated as 0 so you can never procede with the number
    private boolean checkForDoubleDotZeroCase(String number) {
        Pattern p = Pattern.compile("\\.00$");
        Matcher m = p.matcher(number);
        return m.find();
    }

    // Fringe case of .XXX getting rounded up so if you keep hitting it, it will add the number
    private boolean checkForThreeDigitDecimalCase(String number) {
        Pattern p = Pattern.compile("\\.\\d\\d\\d");
        Matcher m = p.matcher(number);
        return m.find();
    }

    private void formatNormalNumber(String number) {
        boolean dotZeroFringeCase = false;
        boolean doubleDotZeroFringeCase = false;

        String newNumber = displayNumber.getText() + number;
        newNumber = removeCommas(newNumber);
        dotZeroFringeCase = checkForDotZeroCase(newNumber);
        doubleDotZeroFringeCase = checkForDoubleDotZeroCase(newNumber);
        if(checkForThreeDigitDecimalCase(newNumber)) {
            vibrateErrorAnimation();
            return;
        }

        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        newNumber = formatter.format(Double.parseDouble(newNumber));
        if (dotZeroFringeCase) newNumber += ".0";
        if (doubleDotZeroFringeCase) newNumber += ".00";
        displayNumberText = newNumber;
    }

    private void formatBackspaceNormalNumber(String number) {
        boolean dotZeroFringeCase = false;
        boolean doubleDotZeroFringeCase = false;

        String newNumber = number;
        newNumber = removeCommas(newNumber);
        dotZeroFringeCase = checkForDotZeroCase(newNumber);
        doubleDotZeroFringeCase = checkForDoubleDotZeroCase(newNumber);
        if(checkForThreeDigitDecimalCase(newNumber)) {
            vibrateErrorAnimation();
            return;
        }

        DecimalFormat formatter = new DecimalFormat("#,###,###.##");
        newNumber = formatter.format(Double.parseDouble(newNumber));
        if (dotZeroFringeCase) newNumber += ".0";
        if (doubleDotZeroFringeCase) newNumber += ".00";
        displayNumberText = newNumber;
    }

    private void vibrateErrorAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(100);
        displayNumber.startAnimation(shake);
    }

    private void formatDecimalNumber(String number) {
        String test = displayNumberText + number;
        if(checkForDoublePeriods(test)){
            vibrateErrorAnimation();
            return;
        }
        displayNumberText = displayNumberText + number;
    }
    // ================ End Helper functions ================
}




