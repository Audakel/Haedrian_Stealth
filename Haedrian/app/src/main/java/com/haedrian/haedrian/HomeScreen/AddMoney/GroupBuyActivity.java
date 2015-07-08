package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.haedrian.haedrian.Adapters.GroupMemberListAdapter;
import com.haedrian.haedrian.Application.ApplicationConstants;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.GroupVerifyDialog;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.Network.JsonUTF8Request;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;
import com.haedrian.haedrian.util.TimeoutRetryPolicy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public class GroupBuyActivity extends ActionBarActivity {

    private GroupMemberListAdapter adapter;
    private ArrayList<UserModel> groupMembers;
    private ListView groupMemberListView;
    private Button submitButton;
    private TextView officeNameTV;
    private LinearLayout emptyContainer, officeContainer;
    private RelativeLayout groupContainer;

    private String groupId, officeName;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_buy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.dialog_loading));
        progressDialog.show();

        groupMemberListView = (ListView) findViewById(R.id.group_members_container);
        submitButton = (Button) findViewById(R.id.submit_button);
        officeNameTV = (TextView) findViewById(R.id.office_name);
        officeContainer = (LinearLayout) findViewById(R.id.office_container);

        emptyContainer = (LinearLayout) findViewById(R.id.empty_state_container);
        groupContainer = (RelativeLayout) findViewById(R.id.group_container);

        groupMembers = new ArrayList<>();

        adapter = new GroupMemberListAdapter(this, R.layout.row_group_member, groupMembers);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String total = getTotal();
                if (total.equals("0")) {
                    Toast.makeText(GroupBuyActivity.this, getString(R.string.please_enter_an_amount), Toast.LENGTH_SHORT).show();
                }
                else {
                    GroupVerifyDialog dialog = new GroupVerifyDialog(GroupBuyActivity.this, total);
                    dialog.show();
                    dialog.getConfirmButton().setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            progressDialog.show();
                            groupVerify();
                        }
                    });
                }

            }
        });

        initializeGroup();
    }

    @Override
    protected void onStop() {
        super.onStop();
        progressDialog.dismiss();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_group_buy, menu);
        return true;
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

    private void initializeGroup() {
        final String URL = ApplicationConstants.BASE + "group/";

        JsonUTF8Request jsonObjectRequest = new JsonUTF8Request(Request.Method.GET,
                URL, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", ": " + response.toString());
                        try {
                            if (response.getBoolean("success")) {
                                JSONArray array = response.getJSONArray("group_members");
                                if (array.length() > 0) {
                                    groupId = response.getString("group_id");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject object = array.getJSONObject(i);
                                        UserModel user = new UserModel();
                                        user.setId(object.getString("mifos_id"));
                                        user.setFirstName(object.getString("first_name"));
                                        user.setLastName(object.getString("last_name"));
                                        user.setPhoneNumber(object.getString("phone"));

                                        officeName = response.getString("office");

                                        groupMembers.add(user);
                                    }
                                    setView();
                                }
                                else {
                                    emptyContainer.setVisibility(View.VISIBLE);
                                    groupContainer.setVisibility(View.GONE);
                                }
                            }
                            else {
                                progressDialog.dismiss();
                                JSONObject error = response.getJSONObject("error");
                                Toast.makeText(GroupBuyActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            emptyContainer.setVisibility(View.VISIBLE);
                            groupContainer.setVisibility(View.GONE);
                            e.printStackTrace();
                            progressDialog.dismiss();
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

    private void setView() {

        if (officeName != null) {
            officeContainer.setVisibility(View.VISIBLE);
            officeNameTV.setText(officeName);
        }

        adapter = new GroupMemberListAdapter(this, R.layout.row_group_member, groupMembers);
        groupMemberListView.setAdapter(adapter);
        progressDialog.dismiss();
    }

    public void groupVerify() {
        final String URL = ApplicationConstants.BASE + "group-verify/";

        JSONObject body = new JSONObject();
        try {
            body.put("group_id", groupId);
            JSONArray groupMembersJSON = new JSONArray();
            for (int i = 0; i < groupMembers.size(); i++) {
                JSONObject member = new JSONObject();
                member.put("amount", groupMembers.get(i).getAmount());
                member.put("id", groupMembers.get(i).getId());
                member.put("phone", groupMembers.get(i).getPhoneNumber());
                member.put("first_name", groupMembers.get(i).getFirstName());

                groupMembersJSON.put(i, member);
            }
            body.put("group_members", groupMembersJSON);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST,
                URL, body,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.v("TEST", ": " + response.toString());
                        try {
                            if (response.getBoolean("success")) {

                                String repaymentId = response.getString("group_repayment_id");

                                progressDialog.dismiss();
                                Intent intent = new Intent(GroupBuyActivity.this, BuyActivity.class);
                                intent.putExtra("total", getTotal());
                                intent.putExtra("group_repayment_id", repaymentId);
                                startActivity(intent);
                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(GroupBuyActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (JSONException e) {
                            progressDialog.dismiss();
                            Toast.makeText(GroupBuyActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                Toast.makeText(GroupBuyActivity.this, getString(R.string.try_again_later_error), Toast.LENGTH_SHORT).show();
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

    public String getTotal() {
        BigDecimal total = new BigDecimal(0);
        for (int i = 0; i < groupMembers.size(); i++) {
            total = total.add(new BigDecimal(groupMembers.get(i).getAmount()));
        }
        return total.toString();
    }

}
