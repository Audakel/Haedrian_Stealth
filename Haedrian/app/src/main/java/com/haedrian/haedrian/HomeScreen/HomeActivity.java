package com.haedrian.haedrian.HomeScreen;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.haedrian.haedrian.CreditScore.CreditCheckActivity;
import com.haedrian.haedrian.CurrencyInfoActivity;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.SendRequestActivity;
import com.haedrian.haedrian.SettingsActivity;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;


public class HomeActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {
    // Nav Drawer stuff
    private String[] mHomeButtons = {"Home", "Wallet", "Buy", "Add", "Projects", "Invest", "FX Rates", "Settings"};
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private UserModel user;

    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        setupDrawer();

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_nav_item, mHomeButtons));
        // Set the list's click listener
        mDrawerList.setOnItemClickListener(this);


        // After login, set up shared preferences to store the current users ID globally
        final SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);
        final DBHelper db = new DBHelper(this);

        Bundle extras = getIntent().getExtras();

        String parseId = "";
        if (extras != null) {
            parseId = extras.getString("parse_id");
        }

        // No user is currently set
        if (userId == -1) {

            ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
            query.getInBackground(parseId, new GetCallback<ParseObject>() {
                public void done(ParseObject object, ParseException e) {
                    if (e == null) {
                        user = new UserModel();
                        user.setParseId(object.getObjectId());
                        user.setFirstName(object.getString("firstName"));
                        user.setLastName(object.getString("lastName"));
                        user.setUsername(object.getString("username"));
                        user.setEmail(object.getString("email"));
                        user.setPhoneNumber(object.getString("phoneNumber"));

                        UserModel newUser = db.getUsersTable().insert(user);

                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("user_id", newUser.getId());
                        editor.commit();
                    } else {
                        // something went wrong
                    }
                }
            });
        } else {

            user = db.getUsersTable().query("id", "=", "1");
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /**
             * Called when a drawer has settled in a completely open state.
             */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Haedrian");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /**
             * Called when a drawer has settled in a completely closed state.
             */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle("Haedrian");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.send_request:
                intent = new Intent(this, SendRequestActivity.class);
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options.toBundle());
                return;
            case R.id.add:
                intent = new Intent(this, AddActivity.class);
                ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options1.toBundle());
                return;
            case R.id.projects:
                intent = new Intent(this, CreditCheckActivity.class);
                ActivityOptions options2 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options2.toBundle());
                return;
            case R.id.invest:
                intent = new Intent(this, InvestActivity.class);
                ActivityOptions options3 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options3.toBundle());
                return;
            case R.id.wallet:
                intent = new Intent(this, WalletActivity.class);
                ActivityOptions options4 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options4.toBundle());
                return;
            case R.id.buy:
                intent = new Intent(this, BuyActivity.class);
                ActivityOptions options5 = ActivityOptions.makeScaleUpAnimation(view, 0,
                        0, view.getWidth(), view.getHeight());
                startActivity(intent, options5.toBundle());
                return;
            default:
                return;
        }

    }

    // Handle all clicks for the left nav Drawer
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent;
        //Grab all view/position/id info
        //Toast.makeText(this, "View: " + view + " \nPosition: " + position + " \nid: " + id, Toast.LENGTH_LONG).show();

        if (position == 7) {
            intent = new Intent(this, SettingsActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        } else if (position == 6) {
            intent = new Intent(this, CurrencyInfoActivity.class);
            ActivityOptions options1 = ActivityOptions.makeScaleUpAnimation(view, 0,
                    0, view.getWidth(), view.getHeight());

            startActivity(intent, options1.toBundle());
        }
    }
}
