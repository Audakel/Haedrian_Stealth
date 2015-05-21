package com.haedrian.haedrian.HomeScreen.Wallet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.Models.WalletModel;
import com.haedrian.haedrian.R;
import com.parse.Parse;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.android.volley.Request.Method;


public class CreateWalletActivity extends ActionBarActivity {
    private final String TAG = "CANCEL_TAG";
    private Button addBankButton;
    private EditText addEmailText;
    private EditText addPasswordText, reenterPasswordText;
    private UserModel user;
    private DBHelper db;
    private RequestQueue queue;
    private WalletModel wallet;
    private Resources resources;

    private LinearLayout errorLayout;
    private TextView errorMessage;

    public static final int SALT_BYTE_SIZE = 32;
    public static final int HASH_BYTE_SIZE = 32;
    public static final int PBKDF2_ITERATIONS = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        addEmailText = (EditText) findViewById(R.id.addBankEmailEditText);
        addPasswordText = (EditText) findViewById(R.id.password_edit_text);
        reenterPasswordText = (EditText) findViewById(R.id.reenter_password_edit_text);

        errorLayout = (LinearLayout) findViewById(R.id.error_layout);

        errorMessage = (TextView) findViewById(R.id.error_message);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);

        db = new DBHelper(this);

        user = db.getUsersTable().query("id", "=", String.valueOf(userId));

        addEmailText.setText(user.getEmail());

        addBankButton = (Button) findViewById(R.id.addBankButton);

        resources = getResources();

        addBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolleyRequest();
            }
        });

        addPasswordText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (errorLayout.getVisibility() == View.VISIBLE) {
                        errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up));
                        // Execute some code after 2 seconds have passed
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                errorLayout.setVisibility(View.GONE);
                            }
                        }, 500);

                    }
                }
            }
        });

        queue = Volley.newRequestQueue(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_personal_bank, menu);
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

    public void testVolleyRequest() {

        wallet = new WalletModel();
        wallet.setUserId(user.getId());

        if ( ! addPasswordText.getText().toString().equals(reenterPasswordText.getText().toString()) ) {
            errorLayout.setVisibility(View.VISIBLE);
            errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
            return;
        }
        else if (addPasswordText.getText().length() < 10) {
            errorLayout.setVisibility(View.VISIBLE);
            errorMessage.setText(resources.getString(R.string.short_password));
            errorLayout.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down));
            return;
        }

        String password = addPasswordText.getText().toString();
        String secondPass = hash(password);


        final String URL = "https://blockchain.info/api/v2/create_wallet"
                + "?password=" + password
                + "?priv=" + secondPass
                + "&email=" + addEmailText.getText().toString()
                + "&api_code=5a25bea3-7f2f-4a40-acb6-3ed0497d570e";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(resources.getString(R.string.creating_wallet));
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.hide();
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Test", "Error: " + error.toString());
                progressDialog.hide();
            }
        });

        jsonObjectRequest.setTag(TAG);
        queue.add(jsonObjectRequest);


    }

    private String hash(String password) {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_BYTE_SIZE];
        random.nextBytes(salt);

        byte[] hashedPassword = new byte[0];
        try {
            hashedPassword = encrypt(password, salt);
        } catch (NoSuchAlgorithmException e) {

        }
        return hashedPassword.toString();
    }

    public byte[] encrypt(String password, byte[] salt) throws NoSuchAlgorithmException {
        MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
        byte[] passBytes = password.getBytes();
        byte[] result = new byte[passBytes.length + salt.length];
        System.arraycopy(passBytes, 0, result, 0, passBytes.length);
        System.arraycopy(salt, 0, result, passBytes.length, salt.length);
        byte[] passHash = sha256.digest(result);
        return passHash;
    }


    private boolean checkCredentials() {
        return addPasswordText.length() >= 10 && addEmailText.length() > 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
//        FlurryAgent.onEndSession(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
//        FlurryAgent.onStartSession(this);
//        FlurryAgent.logEvent(this.getClass().getSimpleName() + " opened");
    }

}