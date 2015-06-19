package com.haedrian.haedrian.HomeScreen.Wallet;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.flurry.android.FlurryAgent;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.Models.WalletModel;
import com.haedrian.haedrian.R;


public class ImportWalletActivity extends Activity {

    private EditText walletAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_import_wallet);

        walletAddress = (EditText) findViewById(R.id.import_wallet_address);
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
//        getMenuInflater().inflate(R.menu.menu_import_wallet, menu);
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
        switch (view.getId()) {
            case R.id.import_wallet:
                saveWallet();
                break;
            default:
                break;
        }
    }

    public void saveWallet() {
        DBHelper db = new DBHelper(this);
        WalletModel wallet = new WalletModel();

        wallet.setAddress(walletAddress.getText().toString());

        SharedPreferences sp = getSharedPreferences("haedrian_prefs", Activity.MODE_PRIVATE);
        int userId = sp.getInt("user_id", -1);

        wallet.setUserId(userId);

        db.getWalletsTable().insert(wallet);
        finish();

    }
}
