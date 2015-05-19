package com.haedrian.haedrian.HomeScreen.AddMoney;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.haedrian.haedrian.R;

public class OrderSummaryActivity extends ActionBarActivity {

    private Button paymentInstructionsButton, markAsPaidButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_summary);

        paymentInstructionsButton = (Button) findViewById(R.id.payment_instructions_button);
        markAsPaidButton = (Button) findViewById(R.id.mark_as_paid_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
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
        if (id == R.id.action_settings) {
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
            case R.id.cancel_button:
                break;
            default:
                break;
        }
    }

    private void showPaymentInstructionsDialog() {

    }
}
