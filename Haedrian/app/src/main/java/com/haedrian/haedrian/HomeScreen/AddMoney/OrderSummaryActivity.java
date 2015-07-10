package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.CustomDialogs.BuyInstructionsDialog;
import com.haedrian.haedrian.HomeScreen.Wallet.WalletActivity;
import com.haedrian.haedrian.Models.BuyOrderModel;
import com.haedrian.haedrian.R;
import com.haedrian.haedrian.UserInteraction.PinActivity;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.Locale;

public class OrderSummaryActivity extends ActionBarActivity {

    private TextView amountTV, haedrianFeeTV, paymentMethodFeeTV, totalDueTV, buyOrderId;

    private String instructions, total, paymentMethodFee, haedrianFee;

    private BuyOrderModel buyOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amountTV = (TextView) findViewById(R.id.amount);
        haedrianFeeTV = (TextView) findViewById(R.id.haedrian_fee);
        paymentMethodFeeTV = (TextView) findViewById(R.id.payment_method_fee);
        totalDueTV = (TextView) findViewById(R.id.total_due);
        buyOrderId = (TextView) findViewById(R.id.buy_order_id);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            buyOrder = extras.getParcelable("buy_order");

            haedrianFeeTV.setText(buyOrder.getHaedrianFee());
            paymentMethodFeeTV.setText(buyOrder.getPaymentMethodFee());
            totalDueTV.setText(buyOrder.getCurrencyAmount());
            amountTV.setText(buyOrder.getAmount());

            instructions = buyOrder.getInstructions();
            String id = getString(R.string.buy_order) + extras.getString("id");
            buyOrderId.setText(id);
        }
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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_order_summary, menu);
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
            case R.id.payment_instructions_button:
                showPaymentInstructionsDialog();
                break;
            case R.id.mark_as_paid_button:
                break;
            case R.id.wallet_activity_link:
                Intent intent = new Intent(this, WalletActivity.class);
                intent.putExtra("from_order", true);
                startActivity(intent);
            default:
                break;
        }
    }

    private void showPaymentInstructionsDialog() {
        BuyInstructionsDialog buyInstructionsDialog = new BuyInstructionsDialog(this, instructions);
        buyInstructionsDialog.show();
    }
}
