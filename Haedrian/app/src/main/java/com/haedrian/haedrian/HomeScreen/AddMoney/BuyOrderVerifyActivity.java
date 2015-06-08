package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.haedrian.haedrian.R;

public class BuyOrderVerifyActivity extends ActionBarActivity {

    private TextView buyOrderTV, statusTV, amountTV, orderTimeTV, expirationDateTV, locationTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_order_verify);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        buyOrderTV = (TextView) findViewById(R.id.buy_order_title);
        statusTV = (TextView) findViewById(R.id.status);
        amountTV = (TextView) findViewById(R.id.amount);
        orderTimeTV = (TextView) findViewById(R.id.order_time);
        expirationDateTV = (TextView) findViewById(R.id.expiration_date);
        locationTV = (TextView) findViewById(R.id.location);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_buy_order_verify, menu);
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

        switch (id) {
            case R.id.mark_as_paid_button:
                verifyPayment();
                break;
            default:
                break;
        }
    }

    private void verifyPayment() {

    }
}
