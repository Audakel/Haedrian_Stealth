package com.haedrian.curo.HomeScreen.Wallet;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.google.zxing.WriterException;
import com.haedrian.curo.Application.ApplicationConstants;
import com.haedrian.curo.Application.ApplicationController;
import com.haedrian.curo.CustomDialogs.BitcoinAddressDialog;
import com.haedrian.curo.Database.DBHelper;
import com.haedrian.curo.Network.JsonUTF8Request;
import com.haedrian.curo.QrCode.QRCodeEncoder;
import com.haedrian.curo.R;
import com.haedrian.curo.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class BalanceFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private final String TAG = "CANCEL_TAG";
    private Button getWalletAddressButton;
    private TextView convertedAmount, bitcoinAmount;
    private DBHelper db;
    private Context context;
    private ProgressDialog progressDialog;

    public BalanceFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_balance, container, false);

        context = rootView.getContext();

        progressDialog = new ProgressDialog(context);

        // Check to see if the user has a wallet associated and if not, display create a wallet button
        db = new DBHelper(rootView.getContext());

        progressDialog.setMessage(getResources().getString(R.string.initializing_wallet));
        progressDialog.show();

//        walletAddress = (TextView) rootView.findViewById(R.id.wallet_address);
        convertedAmount = (TextView) rootView.findViewById(R.id.converted_currency_amount);
        bitcoinAmount = (TextView) rootView.findViewById(R.id.bitcoin_amount);
        getWalletAddressButton = (Button) rootView.findViewById(R.id.get_wallet_address);

        initializeWallet();

        return rootView;
    }

    public void initializeDisplay(String bitcoinAddress) {

        // Generate QRCode and then display it
        QRCodeEncoder encoder = new QRCodeEncoder();
        Bitmap qrCodeBitmap = null;
        try {
            qrCodeBitmap = encoder.encodeAsBitmap(bitcoinAddress, 300);
        } catch (WriterException e) {
            Log.e("ZXing", e.toString());
        }


        progressDialog.dismiss();

        // Set up dialog stuff with wallet address and bitmap
        final String walletAddress = bitcoinAddress;
        final Bitmap finalQrCodeBitmap = qrCodeBitmap;
        getWalletAddressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitcoinAddressDialog dialog = new BitcoinAddressDialog(context, walletAddress, finalQrCodeBitmap);
                dialog.show();
            }
        });

    }

    private void initializeWallet() {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);

            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                // Check if a request was made less than a minute ago
                long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                    initializeWalletCached();
                } else {
                    initializeWalletNetwork();
                }
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                    // Check if a request was made less than a minute ago
                    long oneMinuteAgo = System.currentTimeMillis() - ApplicationConstants.ONE_MINUTE;
                    if (ApplicationController.getBalanceTimestamp() != 0L && ApplicationController.getBalanceTimestamp() > oneMinuteAgo) {
                        initializeWalletCached();
                    } else {
                        initializeWalletNetwork();
                    }
                } else {
                    initializeWalletCached();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeWalletNetwork() {
        final String URL = ApplicationConstants.BASE + "wallet-info/";

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "Wallet-info: " + response.toString());
                        ApplicationController.cacheJSON(response, "wallet-info");
                        ApplicationController.setBalanceTimestamp(System.currentTimeMillis());
                        try {
                            // Server is sending properly converted and formated currency amount
                            bitcoinAmount.setText(String.valueOf(response.getJSONObject("bitcoin").getString("btc_balance")));
                            convertedAmount.setText(response.getJSONObject("bitcoin").getString("balance"));

                            initializeDisplay(response.getJSONObject("bitcoin").getString("blockchain_address"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
                progressDialog.dismiss();

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

    private void initializeWalletCached() {
        JSONObject response = ApplicationController.getCachedJSON("wallet-info");
        Log.v("TEST", "cached wallet-info: " + response.toString());
        try {
            // Server is sending properly converted and formated currency amount
            bitcoinAmount.setText(String.valueOf(response.getJSONObject("bitcoin").getString("btc_balance")));
            convertedAmount.setText(response.getJSONObject("bitcoin").getString("balance"));

            initializeDisplay(response.getJSONObject("bitcoin").getString("blockchain_address"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
