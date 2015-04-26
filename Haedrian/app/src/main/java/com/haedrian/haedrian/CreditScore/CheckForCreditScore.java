package com.haedrian.haedrian.CreditScore;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;

public class CheckForCreditScore extends ActionBarActivity {
    DBHelper db;
    UserModel user;
    SharedPreferences sp;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_for_credit_score);
        db = new DBHelper(this);
        sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        userId = sp.getInt("user_id", -1);
        user = db.getUsersTable().query("id", "=", String.valueOf(userId));

        //TODO:: This only works by checking local database. Need to figure out how to also check parse or our server for score
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent;
        if (user.getCreditScore() > 0){
//            intent = new Intent(this, GetCreditScoreActivity.class);
            intent = new Intent(this, HasCreditScoreActivity.class);
            startActivity(intent);
        }
        else {
            intent = new Intent(this, GetCreditScoreActivity.class);
            startActivity(intent);
        }
    }
}
