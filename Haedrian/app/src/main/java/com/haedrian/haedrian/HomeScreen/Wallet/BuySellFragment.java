package com.haedrian.haedrian.HomeScreen.Wallet;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Adapters.BuyOrderListAdapter;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.AddMoney.BuyOrderVerifyActivity;
import com.haedrian.haedrian.Models.BuyOrderHistoryModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


public class BuySellFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView buyOrderList;
    private ArrayList<BuyOrderHistoryModel> buyOrders;
    private BuyOrderListAdapter adapter;
    private Context context;
    private TextView noBuyOrders;

    public BuySellFragment() {}

    public static BuySellFragment newInstance(int sectionNumber) {
        BuySellFragment fragment = new BuySellFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_buy_sell, container, false);

        buyOrderList = (ListView) rootView.findViewById(R.id.buyorder_list);
        noBuyOrders = (TextView) rootView.findViewById(R.id.no_buyorder_textview);

        buyOrders = new ArrayList<>();

        this.context = rootView.getContext();

        initializeBuyHistory();

        return rootView;
    }

    private void initializeBuyHistory() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                    initializeBuyHistoryCached();
                }
                else {
                    initializeBuyHistoryNetwork();
                }
            }
            else {
                netInfo = cm.getNetworkInfo(1);

                if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED){
                    long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                    if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                        initializeBuyHistoryCached();
                    }
                    else {
                        initializeBuyHistoryNetwork();
                    }
                }
                else {
                    initializeBuyHistoryCached();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeBuyHistoryNetwork() {
        final String URL = ApplicationConstants.BASE + "buy-history/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "buy-history: " + response.toString());
                        ApplicationController.cacheJSON(response, "buy-history");
                        ApplicationController.setTransactionTimestamp(System.currentTimeMillis());
                        try {
                            if (response.getBoolean("success")) {
                                int transactionCount = response.getInt("transaction_count");
                                if (transactionCount > 0) {
                                    JSONArray transactionArray = response.getJSONArray("transactions");
                                    for (int i = 0; i < transactionArray.length(); i++) {
                                        JSONObject object = transactionArray.getJSONObject(i);
                                        BuyOrderHistoryModel buyOrder = new BuyOrderHistoryModel();
                                        buyOrder.setId(object.getString("id"));
                                        buyOrder.setStatus(object.getString("status"));
                                        buyOrder.setOutletTitle(object.getString("outlet_title"));
                                        String createdAt = object.getString("created_at");
                                        buyOrder.setCreatedAt(createdAt);
                                        buyOrder.setExchangeRate(object.getString("exchange_rate"));
                                        buyOrder.setInstructions(object.getString("instructions"));
                                        buyOrder.setExpirationTime(object.getString("expiration_time"));
                                        buyOrder.setBtcAmount(object.getString("btc_amount"));
                                        buyOrder.setCurrencyAmount(object.getString("currency_amount"));

                                        buyOrders.add(buyOrder);
                                    }
                                    setView();
                                }
                            }
                            else {
                                JSONObject error = response.getJSONObject("error");
                                Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show();
                            }
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

    private void initializeBuyHistoryCached() {
        JSONObject response = ApplicationController.getCachedJSON("buy-history");
        try {
            if (response.getBoolean("success")) {
                int transactionCount = response.getInt("transaction_count");
                if (transactionCount > 0) {
                    JSONArray transactionArray = response.getJSONArray("transactions");
                    for (int i = 0; i < transactionArray.length(); i++) {
                        JSONObject object = transactionArray.getJSONObject(i);
                        BuyOrderHistoryModel buyOrder = new BuyOrderHistoryModel();
                        buyOrder.setId(object.getString("id"));
                        buyOrder.setStatus(object.getString("status"));
                        buyOrder.setOutletTitle(object.getString("outlet_title"));
                        String createdAt = object.getString("created_at");
                        buyOrder.setCreatedAt(createdAt);
                        buyOrder.setExchangeRate(object.getString("exchange_rate"));
                        buyOrder.setInstructions(object.getString("instructions"));
                        buyOrder.setExpirationTime(object.getString("expiration_time"));
                        buyOrder.setBtcAmount(object.getString("btc_amount"));
                        buyOrder.setCurrencyAmount(object.getString("currency_amount"));

                        buyOrders.add(buyOrder);
                    }
                    setView();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setView() {
        noBuyOrders.setVisibility(View.GONE);

        adapter = new BuyOrderListAdapter(context, R.layout.row_buy_order, buyOrders);
        buyOrderList.setAdapter(adapter);
        buyOrderList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, BuyOrderVerifyActivity.class);
                intent.putExtra("buy_order", buyOrders.get(position));
                startActivity(intent);
            }
        });
    }

    public String formatDate(String date) {
        String[] parts = date.split("-");
        String year = parts[0];
        String month = "";

        switch (parts[1]) {
            case "01":
                month = getString(R.string.january);
                break;
            case "02":
                month = getString(R.string.february);
                break;
            case "03":
                month = getString(R.string.march);
                break;
            case "04":
                month = getString(R.string.april);
                break;
            case "05":
                month = getString(R.string.may);
                break;
            case "06":
                month = getString(R.string.june);
                break;
            case "07":
                month = getString(R.string.july);
                break;
            case "08":
                month = getString(R.string.august);
                break;
            case "09":
                month = getString(R.string.september);
                break;
            case "10":
                month = getString(R.string.october);
                break;
            case "11":
                month = getString(R.string.november);
                break;
            case "12":
                month = getString(R.string.december);
                break;
        }

        int day = Integer.parseInt(parts[2]);

        return month + " " + day + ", " + year;

    }

}