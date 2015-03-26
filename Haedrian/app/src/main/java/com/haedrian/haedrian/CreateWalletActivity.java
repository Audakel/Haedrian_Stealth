package com.haedrian.haedrian;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.Models.WalletModel;

import org.json.JSONException;
import org.json.JSONObject;

import static com.android.volley.Request.Method;


public class CreateWalletActivity extends ActionBarActivity {
    private final String TAG = "CANCEL_TAG";
    private Button addBankButton;
    private EditText addEmailText;
    private EditText addPasswordText;
    private UserModel user;
    private DBHelper db;
    private RequestQueue queue;
    private WalletModel wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_wallet);

        addEmailText = (EditText) findViewById(R.id.addBankEmailEditText);
        addPasswordText = (EditText) findViewById(R.id.password_edit_text);

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);

        db = new DBHelper(this);

        user = db.getUsersTable().query("id", "=", String.valueOf(userId));

        addEmailText.setText(user.getEmail());

        addBankButton = (Button) findViewById(R.id.addBankButton);

        addBankButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testVolleyRequest();
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


        final String URL = "https://blockchain.info/api/v2/create_wallet"
                + "?password=" + addPasswordText.getText().toString()
                + "&email=" + addEmailText.getText().toString()
                + "&api_code=5a25bea3-7f2f-4a40-acb6-3ed0497d570e";


        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Creating Wallet...");
        progressDialog.show();

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Method.POST,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            progressDialog.hide();
                            saveWallet(response.getString("address"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressDialog.hide();
                        }
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

    private void saveWallet(String address) {

        if (address != "" && address != null) {
            wallet.setAddress(address);
            wallet.setBalance("0");
            db.getWalletsTable().insert(wallet);

            finish();
        }
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
    }
}
