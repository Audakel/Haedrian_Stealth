package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.HomeScreen.Wallet.WalletActivity;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;

public class BuyOptions extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_options);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (ApplicationController.getToken().equals("")) {
            Intent intent = new Intent(this, PinActivity.class);
            startActivity(intent);
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_buy_options, menu);
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

    public void onClick(View view) {
        int id = view.getId();

        Intent intent;
        switch (id) {
            case R.id.individual:
                intent = new Intent(this, BuyActivity.class);
                startActivity(intent);
                break;
            case R.id.group:
                intent = new Intent(this, GroupBuyActivity.class);
                startActivity(intent);
                break;
            case R.id.my_wallet:
                intent = new Intent(this, WalletActivity.class);
                intent.putExtra("from_order", true);
                startActivity(intent);
            default:
                break;
        }
    }
}
