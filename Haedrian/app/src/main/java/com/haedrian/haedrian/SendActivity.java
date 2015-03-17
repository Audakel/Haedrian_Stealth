package com.haedrian.haedrian;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class SendActivity extends ActionBarActivity {
    TextView sendAmountView;
    String sendAmountValue;
    SmsManager smsManager;
    Button sendTextButton;
    EditText phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            sendAmountValue = extras.getString("sendAmountView");
        }
        else{
            sendAmountValue = "error";
        }


        sendAmountView = (TextView) findViewById(R.id.sendAmountText);
        sendAmountView.setText(sendAmountValue);


        // ----- Phone stuff ---------
        smsManager = SmsManager.getDefault();
        phoneNumber = (EditText) findViewById(R.id.phoneNumbeEditText);
        sendTextButton = (Button) findViewById(R.id.sendTextButton);
        sendTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sendTextMessage(phoneNumber.getText().toString(), sendAmountValue);
                sendTextMessage(phoneNumber.getText().toString(), sendAmountValue);
                sendTextButton.setText("Away it was sent!!");
                sendTextButton.setBackgroundColor(getResources().getColor(R.color.myGrey));
            }
        });


    }

    private void sendTextMessage(String phoneNumber, String message) {
        smsManager.sendTextMessage(phoneNumber, null, message, null, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);
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
}
