package com.haedrian.haedrian;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;


public class HomeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.send_request:
                // TODO: Call send request activity from here
                Log.v("Haedrian", "Send Request");
                return;
            case R.id.add:
                // TODO: Call add activity from here
                Log.v("Haedrian", "Add");
                return;
            case R.id.projects:
                // TODO: Call projects activity from here
                Log.v("Haedrian", "Projects");
                return;
            case R.id.invest:
                // TODO: Call invest activity from here
                Log.v("Haedrian", "Invest");
                return;
            default:
                return;
        }

    }
}
