package com.haedrian.haedrian.UserInteraction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Adapters.CurrencyAdapter;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.Models.CurrencyModel;
import com.haedrian.haedrian.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.haedrian.haedrian.R.array;
import static com.haedrian.haedrian.R.layout;


public class CurrencyInfoActivity extends ActionBarActivity {
    private static final String TAG = CurrencyInfoActivity.class.getSimpleName();
    CurrencyAdapter adapter;
    private List<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
    private ListView listView;
    private ProgressDialog pDialog;
    private Resources resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        listView = (ListView) findViewById(R.id.currencyListView);
        adapter = new CurrencyAdapter(this, currencies);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage(resources.getString(R.string.dialog_loading));
        pDialog.show();

        resources = getResources();

        getCurrencyInfo();
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
    protected void onPause() {
        super.onPause();
        ApplicationController.setToken("");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_invest_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_refresh) {
            getCurrencyInfo();
        }
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrencyInfo() {
        // Creating volley request obj
        String url = "https://blockchain.info/ticker";
        final String[] mCurrencyShortNames = getResources().getStringArray(array.currencyShortName);

        JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        currencies.clear();
                        for (String currency : mCurrencyShortNames) {
                            try {
                                JSONObject currentCurrency = response.getJSONObject(currency);
                                currencies.add(new CurrencyModel(
                                        currency,
                                        currentCurrency.getInt("buy"),
                                        currentCurrency.getInt("sell"),
                                        currentCurrency.getString("symbol")
                                ));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(currencyRequest);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
