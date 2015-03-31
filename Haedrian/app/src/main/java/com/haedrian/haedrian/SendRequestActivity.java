package com.haedrian.haedrian;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.DecimalFormat;


public class SendRequestActivity extends ActionBarActivity {

    private Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    private Button buttonDot, buttonBack, buttonSend, buttonRequest;
    private TextView displayNumber;
    private TextView dolarSignView;
    private Context context;
    private LinearLayout errorLayout;
    private RequestQueue queue;
    private TextView bitcoinAmount;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_request);
        context = getApplication();

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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



//        errorLayout = (LinearLayout) findViewById(R.id.error_layout);
//
//        buttonSend.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Hides the error message with an animation
//                if (errorLayout.getVisibility() == View.VISIBLE) {
//                    if (userET.getText().toString().equals("")) {
//
//                    } else {
//                        Intent intent;
//                        intent = new Intent(SendRequestActivity.this, SendActivity.class);
//                        intent.putExtra("send_amount", displayNumber.getText().toString());
//                        intent.putExtra("to_user", userET.getText().toString());
//                        startActivity(intent);
//                        errorLayout.setVisibility(View.GONE);
//                        errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
//                    }
//                } else {
//                    if (userET.getText().toString().equals("")) {
//                        errorLayout.setVisibility(View.VISIBLE);
//                        errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
//                    } else {
//                        // pull up send stuff
//                        Intent intent;
//                        intent = new Intent(SendRequestActivity.this, SendActivity.class);
//                        intent.putExtra("send_amount", displayNumber.getText().toString());
//                        intent.putExtra("to_user", userET.getText().toString());
//                        startActivity(intent);
//                    }
//                }
//
//
//                //clear();
//            }
//        });


        buttonSend.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SendRequestActivity.this, SendActivity.class);
                intent.putExtra("send_amount", displayNumber.getText().toString());
                intent.putExtra("send_amount_bitcoin", bitcoinAmount.getText().toString());
                startActivity(intent);
            }
        });

        buttonDot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNumberToDisplay(v);
            }
        });
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backspace();
            }
        });

//        buttonRequest.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                // Hides the error message with an animation
//                if (errorLayout.getVisibility() == View.VISIBLE) {
//                    if (userET.getText().toString().equals("")) {
//
//                    } else {
//                        // Pull up request stuff
//                        Intent intent;
//                        intent = new Intent(SendRequestActivity.this, RequestActivity.class);
//                        intent.putExtra("request_amount", displayNumber.getText().toString());
//                        intent.putExtra("from_user", userET.getText().toString());
//                        startActivity(intent);
//                    }
//                } else {
//                    if (userET.getText().toString().equals("")) {
//                        errorLayout.setVisibility(View.VISIBLE);
//                        errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
//                    } else {
//                        // pull up request stuff
//                        Intent intent;
//                        intent = new Intent(SendRequestActivity.this, RequestActivity.class);
//                        intent.putExtra("request_amount", displayNumber.getText().toString());
//                        intent.putExtra("from_user", userET.getText().toString());
//                        startActivity(intent);
//                    }
//                }
//
//                clear();
//            }
//        });

        displayNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateFontAttributes();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_send_request, menu);
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

    public void onClick(View view) {
        addNumberToDisplay(view);
        getConvertedRate(displayNumber.getText().toString());
    }

    private void addNumberToDisplay(View selectedButton) {

        switch (selectedButton.getId()) {
            case R.id.button0:
                updateText("0");
                return;
            case R.id.button1:
                updateText("1");
                return;
            case R.id.button2:
                updateText("2");
                return;
            case R.id.button3:
                updateText("3");
                return;
            case R.id.button4:
                updateText("4");
                return;
            case R.id.button5:
                updateText("5");
                return;
            case R.id.button6:
                updateText("6");
                return;
            case R.id.button7:
                updateText("7");
                return;
            case R.id.button8:
                updateText("8");
                return;
            case R.id.button9:
                updateText("9");
                return;
            case R.id.buttonDot:
                updateText(".");
                return;
            case R.id.buttonBack:
                backspace();
                return;
            case R.id.buttonSend:
                clear();
                return;
            case R.id.buttonRequest:
                clear();
                return;
        }
    }

    private void clear() {
        displayNumber.setText("0");
    }

    private void backspace() {
        String string = displayNumber.getText().toString();
        if (string.length() > 1) {
            displayNumber.setText(string.substring(0, string.length() - 1));
        } else {
            displayNumber.setText("0");
        }
    }

    private void updateText(String number) {
        if (displayNumber.getText().toString().equals("0")) {
            displayNumber.setText(number);
        } else if (displayNumber.getText().length() < 6) {
            String newNumber = displayNumber.getText() + number;
            newNumber = removeCommas(newNumber);
            DecimalFormat formatter = new DecimalFormat("#,###,###");
            String yourFormattedString = formatter.format(Double.parseDouble(newNumber));

            displayNumber.setText(yourFormattedString);
        }

        if (displayNumber.getText().length() == 6) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            Vibrator v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(100);

            displayNumber.startAnimation(shake);
        }

    }

    // Number formatter has issues with the commas.... needs just the numbers
    private String removeCommas(String number) {
        String newNumber = number.replaceAll(",", "");
        return newNumber;
    }

    private void updateFontAttributes() {
//
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
        }
    }

    public void getConvertedRate(String sendAmount) {
        final String URL = "https://blockchain.info/tobtc?currency=USD&value=" + sendAmount;


        queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        float balance = Float.parseFloat(response);
                        balance = (balance / (float) 100000000);
                        bitcoinAmount.setText(response);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}
