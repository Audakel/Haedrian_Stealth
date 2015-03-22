package com.haedrian.haedrian;

import android.app.ProgressDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Adapters.CurrencyAdapter;
import com.haedrian.haedrian.Modles.CurrencyModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.haedrian.haedrian.R.*;


public class CurrencyInfoActivity extends ActionBarActivity {
    private static final String TAG = CurrencyInfoActivity.class.getSimpleName();
    Button getInvestInfo;
    private List<CurrencyModel> currencies = new ArrayList<CurrencyModel>();
    private ListView listView;
    CurrencyAdapter adapter;
    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_currency_info);

        getInvestInfo = (Button) findViewById(id.getInvestInfoButton);
        getInvestInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrencyInfo();
            }
        });

        listView = (ListView) findViewById(R.id.currencyListView);
        adapter = new CurrencyAdapter(this, currencies);
        listView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);
        // Showing progress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();

        getCurrencyInfo();
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCurrencyInfo(){
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
                        for(String currency : mCurrencyShortNames){
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
