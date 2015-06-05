package com.haedrian.haedrian.HomeScreen.Wallet;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.R;


public class TransactionDetailsActivity extends ActionBarActivity {

    private TransactionModel transaction;
    private TextView btcAmount, currencyAmount, fromPerson, toPerson, note, date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        if (intent != null) {
            transaction = intent.getParcelableExtra("transaction");
        }

        date = (TextView) findViewById(R.id.date);
        btcAmount = (TextView) findViewById(R.id.btc_amount);
        currencyAmount = (TextView) findViewById(R.id.currency_amount);

        fromPerson = (TextView) findViewById(R.id.from_person);
        toPerson = (TextView) findViewById(R.id.to_person);
        note = (TextView) findViewById(R.id.note);

        String[] dateParts = transaction.getDate().split("T");
        date.setText(formatDate(dateParts[0]));
        btcAmount.setText(transaction.getAmount());
        if (transaction.getEntryType().equals("incoming")) {
            toPerson.setText(getString(R.string.me));
            if (transaction.getSender().equals("null")){
                fromPerson.setText(getString(R.string.outside_address));
            }
            else {
                fromPerson.setText(transaction.getSender());
            }
        }
        else {
            if (transaction.getTarget().equals("null")){
                toPerson.setText(getString(R.string.outside_address));
            }
            else {
                toPerson.setText(transaction.getTarget());
            }
            fromPerson.setText(getString(R.string.me));
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
        FlurryAgent.onEndSession(this);
        FlurryAgent.logEvent(this.getClass().getName() + " closed.");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_transaction_details, menu);
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
        else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public String formatDate(String date) {
        String[] parts = date.split("-");
        String year = parts[0];
        String month = "";

        switch (parts[1]) {
            case "01":
                month = getString(R.string.january);
                break;
            case "02":
                month = getString(R.string.february);
                break;
            case "03":
                month = getString(R.string.march);
                break;
            case "04":
                month = getString(R.string.april);
                break;
            case "05":
                month = getString(R.string.may);
                break;
            case "06":
                month = getString(R.string.june);
                break;
            case "07":
                month = getString(R.string.july);
                break;
            case "08":
                month = getString(R.string.august);
                break;
            case "09":
                month = getString(R.string.september);
                break;
            case "10":
                month = getString(R.string.october);
                break;
            case "11":
                month = getString(R.string.november);
                break;
            case "12":
                month = getString(R.string.december);
                break;
        }

        int day = Integer.parseInt(parts[2]);

        return month + " " + day + ", " + year;

    }
}
