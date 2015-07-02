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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.haedrian.haedrian.Adapters.TransactionListAdapter;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.Network.JsonUTF8Request;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    private LinearLayout noTransactions;

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
        noTransactions = (LinearLayout) rootView.findViewById(R.id.empty_state_container);

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
                long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                    initializeTransactionsCached();
                } else {
                    initializeTransactionsNetwork();
                }
            } else {
                netInfo = cm.getNetworkInfo(1);

                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                    if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                        initializeTransactionsCached();
                    } else {
                        initializeTransactionsNetwork();
                    }
                } else {
                    initializeTransactionsCached();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeTransactionsNetwork() {
        final String URL = ApplicationConstants.BASE + "history/";

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "history: " + response.toString());
                        ApplicationController.cacheJSON(response, "history");
                        ApplicationController.setTransactionTimestamp(System.currentTimeMillis());
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
                                        String[] dateParts = object.getString("date").split("T");
                                        transaction.setDate(formatDate(dateParts[0]));
                                        transaction.setEntryType(object.getString("entry_type"));
                                        transaction.setSender(object.getString("original_sender"));
                                        transaction.setTarget(object.getString("original_target"));
                                        transaction.setCurrency(object.getString("currency"));

                                        transactions.add(transaction);
                                    }
                                    setView();
                                } else {
                                    noTransactions.setVisibility(View.VISIBLE);
                                }
                            } else {
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
                        transaction.setId(object.getString("id"));
                        transaction.setStatus(object.getString("status"));
                        transaction.setFeeAmount(object.getString("fee_amount"));
                        transaction.setAmount(object.getString("amount"));
                        String[] dateParts = object.getString("date").split("T");
                        transaction.setDate(formatDate(dateParts[0]));
                        transaction.setEntryType(object.getString("entry_type"));
                        transaction.setSender(object.getString("original_sender"));
                        transaction.setTarget(object.getString("original_target"));
                        transaction.setCurrency(object.getString("currency"));

                        transactions.add(transaction);
                    }
                    setView();
                } else {
                    noTransactions.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setView() {
        transactionList.setVisibility(View.VISIBLE);

        adapter = new TransactionListAdapter(context);
        int size = transactions.size();
        String date = "";
        for (int i = 0; i < size; i++) {
            String newDate = transactions.get(i).getDate();
            if (!date.equals(newDate)) {
                TransactionModel dateModel = new TransactionModel();
                dateModel.setDate(newDate);
                date = transactions.get(i).getDate();
                adapter.addSectionHeader(dateModel);
            }
            adapter.addTransaction(transactions.get(i));
        }

        transactionList.setAdapter(adapter);
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (adapter.getItemViewType(position) != TransactionListAdapter.TYPE_SEPARATOR) {
                    Intent intent = new Intent(context, TransactionDetailsActivity.class);
                    intent.putExtra("transaction", transactions.get(adapter.getPosition(position)));
                    startActivity(intent);
                }
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
