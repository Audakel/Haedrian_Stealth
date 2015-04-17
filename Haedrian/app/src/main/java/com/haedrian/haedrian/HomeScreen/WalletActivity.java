package com.haedrian.haedrian.HomeScreen;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.zxing.WriterException;
import com.haedrian.haedrian.CreateWalletActivity;
import com.haedrian.haedrian.CustomDialogs.BitcoinAddressDialog;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.ImportWalletActivity;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.Models.WalletModel;
import com.haedrian.haedrian.QrCode.QRCodeEncoder;
import com.haedrian.haedrian.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;


public class WalletActivity extends ActionBarActivity implements ActionBar.TabListener {

    public static int userId;
    private static String parseId;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        parseId = sp.getString("parse_id", "");

        // Set up the action bar.
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_wallet, menu);
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
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    public static class BalanceFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private ImageView qrCode;
        private ImageButton copyButton;
        private TextView walletAddress, convertedAmount, bitcoinAmount;
        private RequestQueue queue;
        private DBHelper db;

        private Context context;
        private final String TAG = "CANCEL_TAG";

        private ProgressDialog progressDialog;

        public BalanceFragment() {
        }

        public static BalanceFragment newInstance(int sectionNumber) {
            BalanceFragment fragment = new BalanceFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public static BigDecimal round(float d, int decimalPlace) {
            BigDecimal bd = new BigDecimal(Float.toString(d));
            bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
            return bd;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_balance, container, false);

            context = rootView.getContext();
            queue = Volley.newRequestQueue(context);

            progressDialog = new ProgressDialog(context);

            // Check to see if the user has a wallet associated and if not, display create a wallet button
            db = new DBHelper(rootView.getContext());

            progressDialog.setMessage("Initializing wallet...");
            progressDialog.show();

            walletAddress = (TextView) rootView.findViewById(R.id.wallet_address);
            convertedAmount = (TextView) rootView.findViewById(R.id.converted_currency_amount);
            bitcoinAmount = (TextView) rootView.findViewById(R.id.bitcoin_amount);

            qrCode = (ImageView) rootView.findViewById(R.id.bitcoin_qr_code);
            copyButton = (ImageButton) rootView.findViewById(R.id.copy_button);


            // Check for wallet
            ParseQuery<ParseObject> walletQuery = ParseQuery.getQuery("Wallet");
            walletQuery.whereEqualTo("userId", parseId);
            walletQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> parseObjects, ParseException e) {
                    if (e == null) {
                        // If it exists
                        if (parseObjects.size() > 0) {
                            WalletModel wallet = new WalletModel();
                            wallet.setUserId(userId);
                            wallet.setAddress(parseObjects.get(0).getString("walletAddress"));
                            wallet.setBalance(String.valueOf(parseObjects.get(0).getNumber("balance")));
                            initializeWallet(wallet);
                        }
                        else {
                            createWallet();
                        }

                    }
                    else {
                        Toast.makeText(rootView.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

            return rootView;
        }

        public void initializeWallet(WalletModel wallet) {

            // Generate QRCode and then display it
            QRCodeEncoder encoder = new QRCodeEncoder();
            Bitmap qrCodeBitmap = null;
            try {
                qrCodeBitmap = encoder.encodeAsBitmap(wallet.getAddress(), 300);
            } catch (WriterException e) {
                Log.e("ZXing", e.toString());
            }

            if (qrCodeBitmap != null) {
                qrCode.setImageBitmap(qrCodeBitmap);
            }

            // Display wallet address
            walletAddress.setText(wallet.getAddress());

            // Get wallet balance and display it
            getWalletBalance(wallet.getAddress(), context);


            // Set up dialog stuff with wallet address and bitmap
            final String walletAddress = wallet.getAddress();
            final Bitmap finalQrCodeBitmap = qrCodeBitmap;
            qrCode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BitcoinAddressDialog dialog = new BitcoinAddressDialog(context, walletAddress, finalQrCodeBitmap);
                    dialog.show();
                }
            });


            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int sdk = android.os.Build.VERSION.SDK_INT;
                    if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                        android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        clipboard.setText(getActivity().getResources().getString(R.string.dummy_address));
                    } else {
                        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        android.content.ClipData clip = android.content.ClipData.newPlainText("bitcoinAddress", walletAddress);
                        clipboard.setPrimaryClip(clip);
                    }

                    Toast.makeText(context, walletAddress + " was copied to the clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void createWallet() {
            SharedPreferences sp = context.getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
            String password = sp.getString("secret", "");

            DBHelper db = new DBHelper(context);

            UserModel user = db.getUsersTable().query("id", "=", String.valueOf(userId));


            final String URL = "https://blockchain.info/api/v2/create_wallet"
                    + "?password=" + password
                    + "&email=" + user.getEmail()
                    + "&api_code=5a25bea3-7f2f-4a40-acb6-3ed0497d570e";


            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                    URL, null,
                    new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                saveWallet(response.getString("address"));

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
            });

            jsonObjectRequest.setTag(TAG);
            queue.add(jsonObjectRequest);

        }

        private void saveWallet(String address) {
            WalletModel wallet = new WalletModel();

            if (address != "" && address != null) {
                wallet.setUserId(userId);
                wallet.setAddress(address);
                wallet.setBalance("0");
                db.getWalletsTable().insert(wallet);

                ParseObject parseWallet = new ParseObject("Wallet");
                parseWallet.put("walletAddress", address);
                parseWallet.put("userId", parseId);
                parseWallet.put("balance", 0);
                parseWallet.saveInBackground();

                initializeWallet(wallet);
            }
        }

        public void getWalletBalance(String address, Context context) {
            final String URL = "https://blockchain.info/q/addressbalance/"
                    + address;


            queue = Volley.newRequestQueue(context);

            StringRequest stringRequest = new StringRequest(Request.Method.GET,
                    URL,
                    new Response.Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            setBalance(response);
                        }
                    }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d("Test", "Error: " + error.toString());
                    progressDialog.dismiss();
                }
            });

            // Add the request to the RequestQueue.
            queue.add(stringRequest);

        }

        public void setBalance(String response) {

            float balance = Float.parseFloat(response);

            balance = (balance / (float) 100000000);

            getConvertedRate(String.valueOf(balance));

            bitcoinAmount.setText(String.valueOf(balance));
        }

        public void getConvertedRate(String bitcoinAmount) {
            final String url = "https://blockchain.info/ticker";

            final String amount = bitcoinAmount;
            JsonObjectRequest currencyRequest = new JsonObjectRequest(url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
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

            queue.add(currencyRequest);
        }

        public void setConvertedRate(int rate, String bitcoinAmount) {
            float conversion = (float) rate * Float.parseFloat(bitcoinAmount);

            BigDecimal value;
            value = round(conversion, 2);

            convertedAmount.setText(String.valueOf(value));
            progressDialog.dismiss();
        }

    }

    public static class TransactionFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

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
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            switch (position) {
                case 0:
                    return BalanceFragment.newInstance(position + 1);
                case 1:
                    return TransactionFragment.newInstance(position + 1);
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }
    }

}
