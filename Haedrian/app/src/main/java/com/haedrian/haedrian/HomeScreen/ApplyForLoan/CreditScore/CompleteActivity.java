package com.haedrian.haedrian.HomeScreen.ApplyForLoan.CreditScore;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.R;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class CompleteActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete);
        TextView transactionInfo = (TextView)findViewById(R.id.transactionInformation);
        Intent intent = getIntent();
        String transactionId = intent.getStringExtra("transId");
        transactionInfo.setText(Html.fromHtml("Your transaction ID is: <b>" + transactionId + "</b>"));

        DBHelper dbHelper = new DBHelper(this);
        String score = dbHelper.getUsersTable().getCreditScore().toString();


        TextView creditScoreText = (TextView) findViewById(R.id.creditScoreText);
        creditScoreText.setText(score);

        Button returnButton = (Button) findViewById(R.id.returnHomeButton);
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HasCreditScoreActivity.class);
                startActivity(intent);
            }
        });
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.complete, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
