package com.haedrian.haedrian.UserInteraction;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.AddMoney.OrderSummaryActivity;
import com.haedrian.haedrian.Models.BuyOrderModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends Activity {

    private ProgressDialog progressDialog;
    private TextView nameTV, usernameTV, emailTV;
    private LinearLayout nameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();

        getActionBar().setDisplayHomeAsUpEnabled(true);

        nameTV = (TextView) findViewById(R.id.name);
        usernameTV = (TextView) findViewById(R.id.username);
        emailTV = (TextView) findViewById(R.id.email);

        nameLayout = (LinearLayout) findViewById(R.id.name_container);

        getUserInformation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " opened.");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.sign_out_container:
                signOut();
                break;
            case R.id.about_container:
                Intent intent = new Intent(this, About.class);
                startActivity(intent);
                break;
            case R.id.help_container:
                Intent intent1 = new Intent(this, Help.class);
                startActivity(intent1);
                break;
            case R.id.privacy_policy_container:
                String url = "https://haedrian.io/privacy-policy/";
                Intent intent2 = new Intent(Intent.ACTION_VIEW);
                intent2.setData(Uri.parse(url));
                startActivity(intent2);
                break;
            case R.id.terms_of_service_container:
                String url2 = "https://haedrian.io/tos/";
                Intent intent3 = new Intent(Intent.ACTION_VIEW);
                intent3.setData(Uri.parse(url2));
                startActivity(intent3);
                break;
            default:
                break;
        }
    }

    private void getUserInformation() {
        String url = ApplicationConstants.BASE + "id/";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                url, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            String name = response.getString("first_name") + " " + response.getString("last_name");
                            String username = response.getString("user");
                            String email = response.getString("email");

                            if (name != " " && name != "") {
                                nameTV.setText(name);
                            }
                            else {
                                nameLayout.setVisibility(View.GONE);
                            }
                            usernameTV.setText(username);
                            emailTV.setText(email);

                            progressDialog.dismiss();
                        } catch (JSONException e) {
                            progressDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Log.v("TEST", "Error: " + error.getMessage());
                progressDialog.dismiss();
            }
        }){
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

    private void signOut() {
        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString("token", "");
        // This is so that it will prompt the user to enter in the pin on app startup
        editor.putString("pin_state", "");

        ApplicationController.setToken("");

        editor.commit();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


}
