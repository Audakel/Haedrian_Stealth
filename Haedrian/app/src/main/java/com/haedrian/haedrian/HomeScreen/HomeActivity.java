package com.haedrian.haedrian.HomeScreen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.RequestDialog;
import com.haedrian.haedrian.HomeScreen.AddMoney.BuyOptions;
import com.haedrian.haedrian.HomeScreen.Wallet.WalletActivity;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.Network.JsonUTF8Request;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.CurrencyInfoActivity;
import com.haedrian.haedrian.UserInteraction.LoginActivity;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.UserInteraction.SettingsActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;


public class HomeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    public JSONObject loanInfoJson;
    private LinearLayout loanInfoContainer, amountDueContainer;
    private TextView walletBallanceTV, loanBallanceTV, timeLeftTV, usernameTV, timeRepaymentUnit, balanceDueTV;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private int moneyBalanceTextSize = 20;


    // Nav Drawer stuff
//    private String[] mHomeButtons = {"Exchange Rates", "Settings"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        walletBallanceTV = (TextView) findViewById(R.id.wallet_balance);
        loanBallanceTV = (TextView) findViewById(R.id.loan_balance);
        timeLeftTV = (TextView) findViewById(R.id.time_left);
        usernameTV = (TextView) findViewById(R.id.username);
        timeRepaymentUnit = (TextView) findViewById(R.id.time_unit_title);
        balanceDueTV = (TextView) findViewById(R.id.balance_due);

        loanInfoContainer = (LinearLayout) findViewById(R.id.loan_info_container);
        amountDueContainer = (LinearLayout) findViewById(R.id.amount_due_container);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary_light, R.color.primary, R.color.primary_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                ApplicationController.setHomeScreenTimestamp(0L);
                getHomeScreenData();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();


        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        getHomeScreenData();
    }

    private void getHomeScreenData() {
        try {
            ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                // Check if a request was made less than a minute ago
                long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.FIVE_MINUTES;
                if (ApplicationController.getHomeScreenTimestamp() != 0L && ApplicationController.getHomeScreenTimestamp() > oneMinuteAgo) {
                    getHomeScreenDataCached();
                } else {
                    getHomeScreenDataNetwork();
                }
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    // Check if a request was made less than a minute ago
                    long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.FIVE_MINUTES;
                    if (ApplicationController.getHomeScreenTimestamp() != 0L && ApplicationController.getHomeScreenTimestamp() > oneMinuteAgo) {
                        getHomeScreenDataCached();
                    } else {
                        getHomeScreenDataNetwork();
                    }
                } else {
                    getHomeScreenDataCached();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getHomeScreenDataNetwork() {
        String url = ApplicationConstants.BASE + "home/";
        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "homeScreenInfo: " + response.toString());

                        try {
                            Log.v("TEST", response.toString());
                            if (response.getBoolean("success")) {
                                ApplicationController.cacheJSON(response, "home");
                                ApplicationController.setHomeScreenTimestamp(System.currentTimeMillis());


                                usernameTV.setText(response.getString("username"));

                                // Assuming they have only 1 loan for now ~
                                // TODO:: Fix with asking the user what loan they want to see if > 1
                                JSONArray loans = response.getJSONArray("loan_info");
                                if (loans.length() > 0) {
                                    JSONObject loan = loans.getJSONObject(0);

                                    JSONObject nextRepaymentInfo = response.getJSONObject("next_repayment_info");

                                    timeLeftTV.setText(String.valueOf(calculateTimeDifference(nextRepaymentInfo.getString("date"))));
                                    balanceDueTV.setText(nextRepaymentInfo.getString("amount_display"));

                                    // TODO:: Need to install better CurrencyInstance Backend to support other currencies
                                    walletBallanceTV.setText((response.getString("wallet_balance")));
                                    loanBallanceTV.setText((loan.getString("current_balance_display")));

                                    // Decide font size
                                    if (loanBallanceTV.getText().length() > 8 ||
                                            walletBallanceTV.getText().length() > 8) {
                                        moneyBalanceTextSize = 18;
                                    }
                                    walletBallanceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);
                                    loanBallanceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);
                                    timeLeftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);


                                    loanInfoJson = loan;
                                    loanInfoContainer.setVisibility(View.VISIBLE);
                                    amountDueContainer.setVisibility(View.VISIBLE);
                                }
                                swipeRefreshLayout.setRefreshing(false);
                                progressDialog.dismiss();
                            } else {
                                swipeRefreshLayout.setRefreshing(false);
                                progressDialog.dismiss();
                                String error = response.getString("error");
                                Toast.makeText(HomeActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            progressDialog.dismiss();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                swipeRefreshLayout.setRefreshing(false);
                progressDialog.dismiss();
                Log.v("TEST", "Error: " + error.getMessage());
                Toast.makeText(HomeActivity.this, getString(R.string.could_not_refresh), Toast.LENGTH_SHORT).show();
                getHomeScreenDataCached();
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

    private void getHomeScreenDataCached() {
        JSONObject response = ApplicationController.getCachedJSON("home");
        Log.v("TEST", "cached home screen: " + response.toString());
        try {
            // Assuming they have only 1 loan for now ~
            // TODO:: Fix with asking the user what loan they want to see if > 1
            JSONArray loans = response.getJSONArray("loan_info");

            usernameTV.setText(response.getString("username"));

            if (loans.length() > 0) {

                JSONObject loan = loans.getJSONObject(0);
                JSONObject nextRepaymentInfo = response.getJSONObject("next_repayment_info");

                timeLeftTV.setText(String.valueOf(calculateTimeDifference(nextRepaymentInfo.getString("date"))));
                balanceDueTV.setText(nextRepaymentInfo.getString("amount_display"));

                // TODO:: Need to install better CurrencyInstance Backend to support other currencies
                walletBallanceTV.setText((response.getString("wallet_balance")));
                loanBallanceTV.setText((loan.getString("current_balance_display")));

                // Decide font size
                if (loanBallanceTV.getText().length() > 8 ||
                        walletBallanceTV.getText().length() > 8) {
                    moneyBalanceTextSize = 18;
                }
                walletBallanceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);
                loanBallanceTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);
                timeLeftTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, moneyBalanceTextSize);


                loanInfoJson = loan;
                loanInfoContainer.setVisibility(View.VISIBLE);
                amountDueContainer.setVisibility(View.VISIBLE);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        swipeRefreshLayout.setRefreshing(false);
        progressDialog.dismiss();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_sign_out) {
            signOut();
        }

        return super.onOptionsItemSelected(item);
    }

    private void signOut() {
        ApplicationController.resetTimestamps();
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

    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.send_request:
                intent = new Intent(this, RepayLoanActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options.toBundle());
                return;
            case R.id.bitcoin_map:
                intent = new Intent(this, MapsActivity.class);
                ActivityOptions options5 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options5.toBundle());
                return;
            case R.id.wallet:
                intent = new Intent(this, WalletActivity.class);
                ActivityOptions options4 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options4.toBundle());
                return;
            case R.id.buy:
                intent = new Intent(this, BuyOptions.class);
                ActivityOptions options6 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options6.toBundle());
                return;

            case R.id.wallet_balance_view:
                intent = new Intent(this, WalletActivity.class);
                ActivityOptions options7 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options7.toBundle());
                return;
            case R.id.loan_balance_button:
                intent = new Intent(this, LoanInfoActivity.class);
                ActivityOptions options8 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent.putExtra("loanInfo", loanInfoJson.toString()), options8.toBundle());
                return;
            case R.id.amount_due_container:
                intent = new Intent(this, RepayLoanActivity.class);
                ActivityOptions options9 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options9.toBundle());
                return;
            case R.id.days_to_payment_container:
                return;
            default:
                return;
        }

    }

    // Handle all clicks for the left nav Drawer
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        //Grab all view/position/id info
        //Toast.makeText(this, "View: " + view + " \nPosition: " + position + " \nid: " + id, Toast.LENGTH_LONG).show();


        if (position == 0) {
            intent = new Intent(this, CurrencyInfoActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        } else if (position == 1) {
            intent = new Intent(this, SettingsActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());
            startActivity(intent, options1.toBundle());
        }

    }

    private int calculateTimeDifference(String date) {
        LocalDate dateTime = new LocalDate(date);
        LocalDate today = LocalDate.now();

        int days = Days.daysBetween(today, dateTime).getDays();

        return days;
    }

}
