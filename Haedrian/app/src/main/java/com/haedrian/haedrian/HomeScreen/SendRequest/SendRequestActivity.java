package com.haedrian.haedrian.HomeScreen.SendRequest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SendRequestActivity extends ActionBarActivity {
    private String TAG = SendRequestActivity.class.getSimpleName();
    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonDot, buttonSend, buttonRequest;
    private ImageButton buttonBackSpace;
    private TextView displayNumber;
    private TextView dolarSignView;
    private Context context;
    private LinearLayout errorLayout;
    private RequestQueue queue;
    private TextView bitcoinAmount, currencySign;
    private int currentBitcoinPriceBuy = 0;
    private static final int REQUEST_CODE = 1;

    // Data for all views
    private String displayNumberText = "0";
    private String bitcoinAmountText = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        context = getApplication();

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        currencySign = (TextView) findViewById(R.id.currency_sign);

        Currency currency = Currency.getInstance(Locale.getDefault());
        currencySign.setText(currency.getSymbol());

        getCurrencyInfo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initializeViews();

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
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    private void updateDisplay(){
//        displayNumber.setText(displayNumberText);
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
        buttonBackSpace = (ImageButton) findViewById(R.id.backspace_button);
//        buttonRequest = (Button) findViewById(R.id.buttonRequest);
        displayNumber = (TextView) findViewById(R.id.currency_amount_view);
        dolarSignView = (TextView) findViewById(R.id.currency_sign);
        buttonSend = (Button) findViewById(R.id.buttonSend);
        bitcoinAmount = (TextView) findViewById(R.id.bitcoin_amount);

        errorLayout = (LinearLayout) findViewById(R.id.error_layout);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure the request was successful
        if (resultCode == RESULT_OK)
        {
            if (requestCode == REQUEST_CODE) {
                Bundle extras = data.getExtras();

                if (extras != null) {
                    String message = extras.getString("data");
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                }
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
        if (displayNumberText.length() > 1) {
//            String amount = displayNumber.getText().toString();
            displayNumberText = displayNumberText.substring(0, displayNumberText.length() - 1);

            Double newAmount = Double.parseDouble(displayNumberText);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            // To strip out the currency symbol
            DecimalFormatSymbols symbols = ((DecimalFormat)format).getDecimalFormatSymbols();
            symbols.setCurrencySymbol("");
            ((DecimalFormat)format).setDecimalFormatSymbols(symbols);

            String formattedAmount = format.format(newAmount).replaceAll(" ", "");

            displayNumber.setText(formattedAmount);
        } else {

            displayNumberText = "0";
            bitcoinAmountText = "0";

            Double newAmount = Double.parseDouble(displayNumberText);
            NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
            // To strip out the currency symbol
            DecimalFormatSymbols symbols = ((DecimalFormat)format).getDecimalFormatSymbols();
            symbols.setCurrencySymbol("");
            ((DecimalFormat)format).setDecimalFormatSymbols(symbols);

            String formattedAmount = format.format(newAmount).replaceAll(" ", "");

            displayNumber.setText(formattedAmount);
        }
    }

    private void setDisplayNumberText(String number) {

        int displayLength = displayNumber.getText().length();

        if (displayLength < 8) {
            if ( ! displayNumber.getText().toString().equals(".")) {

                if (displayNumber.getText().toString().equals("0")) {
                    displayNumberText = "";
                }

                displayNumberText = displayNumberText + number;

                Double amount = Double.parseDouble(displayNumberText);
                NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
                // To strip out the currency symbol
                DecimalFormatSymbols symbols = ((DecimalFormat)format).getDecimalFormatSymbols();
                symbols.setCurrencySymbol("");
                ((DecimalFormat)format).setDecimalFormatSymbols(symbols);

                String newAmount = format.format(amount).replaceAll(" ", "");

                displayNumber.setText(newAmount);
            }
        }
        else {
            vibrateErrorAnimation();
        }

    }

    private void addNumberToDisplay(View selectedButton) {
        Intent intent;

        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_fade_out));
            errorLayout.postOnAnimation(new Runnable() {
                @Override
                public void run() {
                    errorLayout.setVisibility(View.GONE);
                }
            });
        }

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
            case R.id.backspace_button:
                backspace();
                return;
            case R.id.back_space_container:
                backspace();
                return;
            case R.id.buttonSend:
                if (Double.parseDouble(displayNumber.getText().toString()) <= 0) {
                    errorLayout.setVisibility(View.VISIBLE);
                    errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
                    return;
                }

                intent = new Intent(SendRequestActivity.this, SendActivity.class);
                intent.putExtra("send_amount", displayNumber.getText().toString());
                intent.putExtra("send_amount_bitcoin", bitcoinAmount.getText().toString());
                intent.putExtra("bitcoin_buy", currentBitcoinPriceBuy);
                startActivityForResult(intent, REQUEST_CODE);
                clear();
                return;
//            case R.id.buttonRequest:
//                intent = new Intent(SendRequestActivity.this, RequestActivity.class);
//                intent.putExtra("request_amount", displayNumber.getText().toString());
//                intent.putExtra("request_amount_bitcoin", bitcoinAmount.getText().toString());
//                startActivityForResult(intent, REQUEST_CODE);
//                clear();
//                return;
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
        if (Locale.getDefault().equals(Locale.US)) {
            // Creating volley request obj
            String url = "https://blockchain.info/ticker";
            JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            // Parsing json
                            try {
                                JSONObject currentCurrency = response.getJSONObject("USD");
                                currentBitcoinPriceBuy = currentCurrency.getInt("buy");
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
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            final String URL = ApplicationConstants.BASE + "exchange-rate/?currency=PHP";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("TEST", "exchange-rate: " + response.toString());
                            try {
                                JSONObject currentCurrency = response.getJSONObject("market");
                                currentBitcoinPriceBuy = currentCurrency.getInt("ask");
                                getConvertedRateInstantly(displayNumber.getText().toString());
                                updateDisplay();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Test", "Error: " + error.toString());

                }

            }) {
                @Override
                public HashMap<String, String> getHeaders() {
                    String token = ApplicationController.getToken();
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Authorization", "Token " + token);
                    params.put("Content-Type", "application/json;charset=UTF-8");
                    params.put("Accept", "application/json");
                    return params;
                }
            };

            jsonObjectRequest.setRetryPolicy(new TimeoutRetryPolicy());

            // Adds request to the request queue
            ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    public void getConvertedRateInstantly(String sendAmount) {
        String cleanSendAmount = removeCommas(sendAmount);
        if (currentBitcoinPriceBuy == 0){
            bitcoinAmountText = getResources().getString(R.string.dialog_loading);
        }
        else{
            double balance = (Double.parseDouble(cleanSendAmount) * 1.000000) / currentBitcoinPriceBuy;
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




