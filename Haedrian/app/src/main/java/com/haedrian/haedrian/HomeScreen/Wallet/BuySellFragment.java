package com.haedrian.haedrian.HomeScreen.Wallet;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

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


public class BuySellFragment extends android.support.v4.app.Fragment {

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
        final String URL = ApplicationConstants.BASE + "buy-history/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "buy-history: " + response.toString());
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
                                        buyOrder.setCreatedAt(object.getString("created_at"));
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

}