package com.haedrian.haedrian.HomeScreen.Wallet;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.WriterException;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.BitcoinAddressDialog;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.QrCode.QRCodeEncoder;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;

public class BalanceFragment extends Fragment {

    private static final String ARG_SECTION_NUMBER = "section_number";
    private Button getWalletAddressButton;
    private TextView convertedAmount, bitcoinAmount;
    private DBHelper db;

    private Context context;
    private final String TAG = "CANCEL_TAG";

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

    public void initializeDisplay(String bitcoinAdress) {

        // Generate QRCode and then display it
        QRCodeEncoder encoder = new QRCodeEncoder();
        Bitmap qrCodeBitmap = null;
        try {
            qrCodeBitmap = encoder.encodeAsBitmap(bitcoinAdress, 300);
        } catch (WriterException e) {
            Log.e("ZXing", e.toString());
        }



        progressDialog.dismiss();

        // Set up dialog stuff with wallet address and bitmap
        final String walletAddress = bitcoinAdress;
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
                initializeWalletNetwork();
            }
            else {
                netInfo = cm.getNetworkInfo(1);

                if(netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED){
                    initializeWalletNetwork();
                }
                else {
                    initializeWalletCached();
                }
            }
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeWalletNetwork() {
        final String URL = ApplicationConstants.BASE + "wallet-info/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", "Wallet-info: " + response.toString());
                        ApplicationController.cacheJSON(response, "wallet-info");
                        try {
                            setBalanceNetwork(response.getJSONObject("bitcoin").getString("balance"));
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



    public void setBalanceNetwork(String response) {

        float balance = Float.parseFloat(response);

//        balance = (balance / (float) 100000000);

        getConvertedRateNetwork(String.valueOf(balance));

        bitcoinAmount.setText(String.valueOf(balance));
    }

    public void getConvertedRateNetwork(String bitcoinAmount) {

        final String amount = bitcoinAmount;
        if (Locale.getDefault().equals(Locale.US)) {
            final String url = "https://blockchain.info/ticker";

            JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            ApplicationController.cacheJSON(response, "ticker");
                            try {
                                JSONObject currentCurrency = response.getJSONObject("USD");
                                int last = currentCurrency.getInt("last");
                                setConvertedRate(last, amount);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Error", "Error: " + error.getMessage());
                    progressDialog.dismiss();
                }
            });

            currencyRequest.setRetryPolicy(new TimeoutRetryPolicy());
            ApplicationController.getInstance().addToRequestQueue(currencyRequest);

        }
        else if (Locale.getDefault().getLanguage().equals("fil")) {
            final String URL = ApplicationConstants.BASE + "exchange-rate/?currency=PHP";

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                    URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            Log.v("TEST", "exchange-rate: " + response.toString());
                            ApplicationController.cacheJSON(response, "ticker");
                            try {
                                JSONObject currentCurrency = response.getJSONObject("market");
                                int last = currentCurrency.getInt("ask");
                                setConvertedRate(last, amount);

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
    }

    private void initializeWalletCached() {
        JSONObject response = ApplicationController.getCachedJSON("wallet-info");
        Log.v("TEST", "cached wallet-info: " + response.toString());
        try {
            setBalanceCached(response.getJSONObject("bitcoin").getString("balance"));
            initializeDisplay(response.getJSONObject("bitcoin").getString("blockchain_address"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    public void setBalanceCached(String response) {
        float balance = Float.parseFloat(response);
        getConvertedRateCached(String.valueOf(balance));
        bitcoinAmount.setText(String.valueOf(balance));
    }

    public void getConvertedRateCached(String bitcoinAmount) {
        JSONObject response = ApplicationController.getCachedJSON("ticker");
        Log.v("TEST", "cached ticker: " +  response.toString());
        JSONObject currentCurrency = null;
        int last = 0;
        try {
            if (Locale.getDefault().equals(Locale.US)) {
                currentCurrency = response.getJSONObject("USD");
                last = currentCurrency.getInt("last");
            }
            else if (Locale.getDefault().getLanguage().equals("fil")) {
                currentCurrency = response.getJSONObject("market");
                last = currentCurrency.getInt("ask");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setConvertedRate(last, bitcoinAmount);
    }

    public void setConvertedRate(int rate, String bitcoinAmount) {
        float conversion = (float) rate * Float.parseFloat(bitcoinAmount);

        Double newAmount = Double.parseDouble(String.valueOf(conversion));
        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.getDefault());
        // To strip out the currency symbol
        DecimalFormatSymbols symbols = ((DecimalFormat) format).getDecimalFormatSymbols();
        symbols.setCurrencySymbol("");
        ((DecimalFormat) format).setDecimalFormatSymbols(symbols);

        String formattedAmount = format.format(newAmount).replaceAll(" ", "");

        convertedAmount.setText(formattedAmount);
        progressDialog.dismiss();
    }

}
