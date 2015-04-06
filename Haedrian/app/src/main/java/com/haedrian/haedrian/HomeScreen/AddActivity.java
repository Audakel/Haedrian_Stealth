package com.haedrian.haedrian.HomeScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.haedrian.haedrian.GetContacts;
import com.haedrian.haedrian.R;


public class AddActivity extends ActionBarActivity {
    String upcCode = "";
    private LinearLayout startScanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        startScanButton = (LinearLayout) findViewById(R.id.scan_button);

    }

    public void onClick(View view) {
        if (view.getId() == R.id.scan_button) {
            startScanning();
        }
        else if (view.getId() == R.id.contacts_button) {
            // Get contacts
            Intent intent = new Intent(this, GetContacts.class);
            startActivity(intent);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add, menu);

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
        } else if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (result != null) {
            String contents = result.getContents();

            if (contents != null) {
                Toast.makeText(this, "UPC code is : " + result.toString(), Toast.LENGTH_LONG).show();
                upcCode = contents.toString();
            } else {
                Toast.makeText(this, "Fail!!", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void startScanning() {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.addExtra("SCAN_WIDTH", 640);
        integrator.addExtra("SCAN_HEIGHT", 480);
        integrator.addExtra("SCAN_MODE", "QR_CODE_MODE,PRODUCT_MODE");
        //customize the prompt message before scanning
        integrator.addExtra("PROMPT_MESSAGE", "Scanner Start!");
        integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this);
        FlurryAgent.logEvent(this.getClass().getSimpleName() + " opened");
    }
    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }
}
