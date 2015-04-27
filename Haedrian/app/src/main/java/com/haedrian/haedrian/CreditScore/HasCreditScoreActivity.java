package com.haedrian.haedrian.CreditScore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.ApplicationController;
import com.haedrian.haedrian.CreateProjectActivity;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.HomeScreen.HomeActivity;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;
import com.parse.codec.binary.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class HasCreditScoreActivity extends ActionBarActivity {
    Button getCreditScore, updateLoanInfoButton, createProjectButton;
    TextView creditScoreTextView;
    String TAG = "ExistingCreditScoreActivity";
    DBHelper db;
    UserModel user;
    SharedPreferences sp;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_existing_credit_score);
        creditScoreTextView = (TextView) findViewById(R.id.creditScoreTextView);

        createProjectButton = (Button) findViewById(R.id.createProjectButton);
        createProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreateProjectActivity.class);
                startActivity(intent);
            }
        });

        updateLoanInfoButton = (Button) findViewById(R.id.updateLoanInfoButton);
        updateLoanInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GetCreditScoreActivity.class);
                startActivity(intent);
            }
        });

        getCreditScore = (Button) findViewById(R.id.checkCreditScoreButton);
        getCreditScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //checkCreditScore();
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
            }
        });

        db = new DBHelper(this);
        sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        user = db.getUsersTable().query("id", "=", String.valueOf(userId));
        creditScoreTextView.setText(user.getCreditScore()+"");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_existing_credit_score, menu);
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

    public void checkCreditScore() {
        /*AWS STYLE
            "Authorization: AWS " + AWSAccessKeyId + ":"  + base64(hmac-sha1(VERB + "\n"
                + CONTENT-MD5 + "\n"
                + CONTENT-TYPE + "\n"
                + DATE + "\n"
                + CanonicalizedAmzHeaders + "\n"
                + CanonicalizedResource))*/

        /*Lenddo style
        *   HTTP_METHOD + “\n” + BODY_MD5 + “\n” + DATE + “\n” + PATH
        *
        *   Date style = 'Mon Jan 01 HH:MM:SS GMT 2013'
        * */
        String newLine = "\n";
        String httpMethod = "GET";
        String bodyMd5 = null;
        String userId = "123456789";
        String path = "/Members/" + userId;
        String apiId = "55372dff7ec70ec34d9b3ef6";
        String prefixId = "LENDDO" + apiId + ":";

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        String formattedDate = dateFormat.format(calendar.getTime());

        String requestString = httpMethod + newLine
                + bodyMd5 + newLine
                + dateFormat.toString() + newLine
                + path;

        byte[] bytesEncoded = Base64.encodeBase64(encrypt(requestString).getBytes());
        String finalAuthorizationString = prefixId + bytesEncoded;

    }

    private void sendVolleyRequest(String url){
        // Creating volley request obj
        JsonObjectRequest creditScoreRequest = new JsonObjectRequest(url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString());
                        // Parsing json
                        try {
                            response.getJSONObject("credit_score");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        // Adding request to request queue
        ApplicationController.getInstance().addToRequestQueue(creditScoreRequest);
    }

    private static String encrypt(String password) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(password.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return sha1;
    }
}
