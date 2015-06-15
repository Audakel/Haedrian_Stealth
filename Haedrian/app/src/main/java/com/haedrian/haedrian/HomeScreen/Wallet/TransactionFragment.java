package com.haedrian.haedrian.HomeScreen.Wallet;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.haedrian.haedrian.Adapters.TransactionListAdapter;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "section_number";
    private ListView transactionList;
    private ArrayList<TransactionModel> transactions;
    private TransactionListAdapter adapter;
    private Context context;
    private TextView noTransactions;

    public TransactionFragment() {
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TransactionFragment newInstance(int sectionNumber) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_transaction, container, false);

        transactionList = (ListView) rootView.findViewById(R.id.transaction_list);
        noTransactions = (TextView) rootView.findViewById(R.id.no_transaction_textview);

        transactions = new ArrayList<>();

        this.context = rootView.getContext();

        initializeTransactions();

        return rootView;
    }

    private void initializeTransactions() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                initializeTransactionsNetwork();
            }
            else {
                netInfo = cm.getNetworkInfo(1);

                if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED){
                    initializeTransactionsNetwork();
                }
                else {
                    initializeTransactionsCached();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTransactionsNetwork() {
        final String URL = ApplicationConstants.BASE + "history/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "history: " + response.toString());
                        ApplicationController.cacheJSON(response, "history");
                        try {
                            if (response.getBoolean("success")) {
                                int transactionCount = response.getInt("transaction_count");
                                if (transactionCount > 0) {
                                    JSONArray transactionArray = response.getJSONArray("transactions");
                                    for (int i = 0; i < transactionArray.length(); i++) {
                                        JSONObject object = transactionArray.getJSONObject(i);
                                        TransactionModel transaction = new TransactionModel();
                                        transaction.setId(object.getString("id"));
                                        transaction.setStatus(object.getString("status"));
                                        transaction.setFeeAmount(object.getString("fee_amount"));
                                        transaction.setAmount(object.getString("amount"));
                                        transaction.setDate(object.getString("date"));
                                        transaction.setEntryType(object.getString("entry_type"));
                                        transaction.setSender(object.getString("original_sender"));
                                        transaction.setTarget(object.getString("original_target"));
                                        transaction.setCurrency(object.getString("currency"));

                                        transactions.add(transaction);
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

    private void initializeTransactionsCached() {
        JSONObject response = ApplicationController.getCachedJSON("history");
        try {
            if (response.getBoolean("success")) {
                int transactionCount = response.getInt("transaction_count");
                if (transactionCount > 0) {
                    JSONArray transactionArray = response.getJSONArray("transactions");
                    for (int i = 0; i < transactionArray.length(); i++) {
                        JSONObject object = transactionArray.getJSONObject(i);
                        TransactionModel transaction = new TransactionModel();
                        transaction.setStatus(object.getString("status"));
                        transaction.setFeeAmount(object.getString("fee_amount"));
                        transaction.setAmount(object.getString("amount"));
                        transaction.setDate(object.getString("date"));
                        transaction.setEntryType(object.getString("entry_type"));
                        transaction.setSender(object.getString("original_sender"));
                        transaction.setTarget(object.getString("original_target"));
                        transaction.setCurrency(object.getString("currency"));

                        transactions.add(transaction);
                    }
                    setView();
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setView() {
        noTransactions.setVisibility(View.GONE);

        adapter = new TransactionListAdapter(context, R.layout.row_transaction, transactions);
        transactionList.setAdapter(adapter);
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, TransactionDetailsActivity.class);
                intent.putExtra("transaction", transactions.get(position));
                startActivity(intent);
            }
        });
    }

}
