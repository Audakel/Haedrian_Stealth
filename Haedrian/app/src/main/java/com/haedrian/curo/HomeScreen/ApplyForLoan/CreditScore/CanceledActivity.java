package com.haedrian.curo.HomeScreen.ApplyForLoan.CreditScore;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.flurry.android.FlurryAgent;
import com.haedrian.curo.R;


public class CanceledActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canceled);
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
        //getMenuInflater().inflate(R.menu.canceled, menu);
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
