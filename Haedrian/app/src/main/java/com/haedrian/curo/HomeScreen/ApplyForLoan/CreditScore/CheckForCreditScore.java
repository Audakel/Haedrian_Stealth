package com.haedrian.curo.HomeScreen.ApplyForLoan.CreditScore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.haedrian.curo.Database.DBHelper;
import com.haedrian.curo.Models.UserModel;
import com.haedrian.curo.R;

public class CheckForCreditScore extends ActionBarActivity {
    DBHelper db;
    UserModel user;
    SharedPreferences sp;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_check_for_credit_score);
        db = new DBHelper(this);
        sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        user = db.getUsersTable().query("_id", "=", String.valueOf(userId));

        //TODO:: This only works by checking local database. Need to figure out how to also check parse or our server for score
    }

    @Override
    protected void onStart() {
        super.onStart();

        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " opened.");

        Intent intent;
        if (user.getCreditScore() > 0){
//            intent = new Intent(this, GetCreditScoreActivity.class);
            intent = new Intent(this, HasCreditScoreActivity.class);
            startActivity(intent);
            finish();
        }
        else {

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
        FlurryAgent.onEndSession(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
