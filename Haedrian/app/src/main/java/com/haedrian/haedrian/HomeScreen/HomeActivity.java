package com.haedrian.haedrian.HomeScreen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.haedrian.haedrian.ApplicationController;

import com.haedrian.haedrian.CreditScore.CheckForCreditScore;
import com.haedrian.haedrian.CreditScore.HasCreditScoreActivity;
import com.haedrian.haedrian.CurrencyInfoActivity;
import com.haedrian.haedrian.CustomDialogs.RequestDialog;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.ProjectsActivity;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.SendActivity;
import com.haedrian.haedrian.SendRequestActivity;
import com.haedrian.haedrian.SettingsActivity;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    // Nav Drawer stuff
    private String[] mHomeButtons = {"Home", "Wallet", "Buy", "Send", "Projects", "Invest", "FX Rates", "Settings"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private UserModel user;
    private String parseId;
    private final static String PENDING_STATUS = "m5jO6Rz54h";
    private final static String REFUSED_STATUS = "10eX5swCo0";
    private final static String FULFILLED_STATUS = "GIidPHawur";

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupDrawer();

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_nav_item, mHomeButtons));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);


        // After login, set up shared preferences to store the current users ID globally
        final SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        parseId = sp.getString("parse_id", "");
        final DBHelper db = new DBHelper(this);

        // No user is currently set
        if (userId == -1) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.getInBackground(parseId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        user = new UserModel();
                        user.setParseId(object.getObjectId());
                        user.setFirstName(object.getString("firstName"));
                        user.setLastName(object.getString("lastName"));
                        user.setUsername(object.getString("username"));
                        user.setEmail(object.getString("email"));
                        user.setPhoneNumber(object.getString("phoneNumber"));

                        UserModel newUser = db.getUsersTable().insert(user);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("user_id", newUser.getId());
                        editor.commit();
                    } else {
                        // something went wrong
                    }
                }
            });
        } else {

            user = db.getUsersTable().query("id", "=", "1");
        }

        // Check if funds have been requested of user
        checkForRequest(parseId);
    }

    private void checkForRequest(String parseId) {

        ParseQuery<ParseObject> requestQuery = ParseQuery.getQuery("Request");
        requestQuery.whereEqualTo("requesteeId", parseId);
        requestQuery.whereEqualTo("fulfillmentStatusId", PENDING_STATUS);
        requestQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    // If user has any requests
                    if (parseObjects.size() > 0) {
                        displayRequestDialog(parseObjects);
                    }
                    else {
                        Log.v("TEST", "test here");
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void displayRequestDialog(List<ParseObject> requests) {

        final ParseObject requestObject = requests.get(0);
        final RequestDialog dialog = new RequestDialog(this, requests.get(0));
        dialog.show();
        dialog.setCancelable(false);
        dialog.getYesButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sendPayment(requestObject);
            }
        });
        dialog.getNotNowButton().setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
                refusePayment(requestObject);
            }
        });

    }

    private void getUserId(ParseObject requestObject) {
        ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
        userQuery.whereEqualTo("objectId", requestObject.get("requestorId"));
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    // If user has any requests
                    if (parseObjects.size() > 0) {
                        sendPayment(parseObjects.get(0));
                    }
                    else {
                        Log.v("TEST", "test here");
                    }
                }
                else {
                    Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendPayment(ParseObject requestObject) {

//        RequestQueue queue = Volley.newRequestQueue(this);
//
//        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
//        String secret = sp.getString("secret", "");
//
//        final String recipient = getWalletAddress();
//        final String from = walletAddress;
//        final String password = secret;
//        final String note = noteET.getText().toString();
//
//        Log.v("TEST", "Receiver: " + recipientId);
//        Log.v("TEST", "Sender: "  + walletAddress);
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
//                base, null,
//                new Response.Listener<JSONObject>() {
//
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        try {
//                            if (response.has("error")) {
//                                progressDialog.hide();
//                                Toast.makeText(SendActivity.this, response.getString("error"), Toast.LENGTH_SHORT).show();
//                                // Save as failed transaction
//                                saveTransaction(false, response.getString("error"));
//                            }
//                            else {
//                                // Save as successful transaction
//                                saveTransaction(true, response.getString("message"));
//                                returnToPreviousActivitySuccess(response.getString("message"));
//                            }
//                        } catch (JSONException e) {
//                            Toast.makeText(SendActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
//                            // Save as failed transaction
//                            saveTransaction(false, e.getMessage());
//                        }
//                    }
//                }, new Response.ErrorListener() {
//
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                progressDialog.hide();
//                Toast.makeText(SendActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("password", password);
//                params.put("to", recipient);
//                params.put("from", from);
//                params.put("note", note);
//
//                return params;
//            }
//        };
//
//        // Adds request to the request queue
//        ApplicationController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void refusePayment(ParseObject requestObject) {
        requestObject.put("fulfillmentStatusId", REFUSED_STATUS);
        requestObject.saveInBackground();
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Haedrian");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Haedrian");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.send_request:
                intent = new Intent(this, SendRequestActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options.toBundle());
                return;
            case R.id.add:
                intent = new Intent(this, AddActivity.class);
                ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options1.toBundle());
                return;
            case R.id.projects:
                /*For testing  --
                intent = new Intent(this, ExistingCreditScoreActivity.class);
                intent = new Intent(this, CreditCheckActivity.class);
                */
                intent = new Intent(this, CheckForCreditScore.class);
                ActivityOptions options2 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options2.toBundle());
                return;
            case R.id.invest:
                intent = new Intent(this, InvestActivity.class);
                ActivityOptions options3 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options3.toBundle());
                return;
            case R.id.wallet:
                intent = new Intent(this, WalletActivity.class);
                ActivityOptions options4 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                intent.putExtra("parse_id", parseId);
                startActivity(intent, options4.toBundle());
                return;
            case R.id.buy:
                intent = new Intent(this, BuyActivity.class);
                ActivityOptions options5 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options5.toBundle());
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
            intent = new Intent(this, HomeActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 1) {
            intent = new Intent(this, WalletActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 2) {
            intent = new Intent(this, BuyActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 3) {
            intent = new Intent(this, SendRequestActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 4) {
            intent = new Intent(this, ProjectsActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 5) {
            intent = new Intent(this, InvestActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 6) {
            intent = new Intent(this, CurrencyInfoActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
        else if (position == 7) {
            intent = new Intent(this, SettingsActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());
            startActivity(intent, options1.toBundle());
        }

    }
}
