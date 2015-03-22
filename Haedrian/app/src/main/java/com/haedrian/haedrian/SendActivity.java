package com.haedrian.haedrian;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.haedrian.haedrian.HomeScreen.HomeActivity;

public class SendActivity extends ActionBarActivity{

    String send_amount;
    String selected_person = "";
    Button sendButton;
    Button cancelButton;
    ListView selectionList;
    TextView displayNumber;
    TextView dollarSignView;

    TextView SendPopWindowText2;
    TextView SendPopWindowPerson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        sendButton = (Button) findViewById(R.id.send_button);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        selectionList = (ListView) findViewById(R.id.selection_list);
        displayNumber = (TextView) findViewById(R.id.send_amount);
        dollarSignView = (TextView) findViewById(R.id.dollarSignView2);

        String[] sampleNames = new String[] {
                "Person 1",
                "Person 2",
                "Person 3",
                "Person 4",
                "Person 5",
                "Person 6",
                "Person 7",
                "Person 8",
                "Person 9"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1,sampleNames);
        selectionList.setAdapter(adapter);
        selectionList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                selected_person =(selectionList.getItemAtPosition(position).toString());
                Toast.makeText(getApplicationContext(),
                        "Selected " + selected_person, Toast.LENGTH_SHORT)
                        .show();
            }
        });
        /*selectionList.setOnClickListener(new ListView.OnClickListener(){
            public void onClick(View arg0) {
                selected_person = selectionList.getSelectedItem().toString();//setOnItemClickListener?
            }
            }
        );*/
       /* Bundle extras = getIntent().getExtras();
        String send_amount = "0";
        if (extras != null) {
            send_amount = extras.getString("send_amount");
        }
        else
        {
            send_amount = "failed";
        }*/
        send_amount = getIntent().getStringExtra("send_amount");
        displayNumber.setText(send_amount);

        /*  Button Listeners  */
        sendButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                LayoutInflater layoutInflater
                        = (LayoutInflater)getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.send_popup_window, null);
                final PopupWindow popupWindow = new PopupWindow(
                        popupView,
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.WRAP_CONTENT);
                SendPopWindowText2 = (TextView)popupView.findViewById(R.id.SendPopWindowText2);
                SendPopWindowText2.setText(SendActivity.this.getSend_amount());
                SendPopWindowPerson = (TextView)popupView.findViewById(R.id.SendPopWindowPerson);
                SendPopWindowPerson.setText(SendActivity.this.getSelected_person());
                Button btnDismiss = (Button)popupView.findViewById(R.id.PopWindowCancelButton);
                Button btnSend = (Button)popupView.findViewById(R.id.PopWindowSendButton);
                btnDismiss.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        popupWindow.dismiss();
                    }});
                btnSend.setOnClickListener(new Button.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        Toast.makeText(getApplicationContext(),
                                "Sending "+ displayNumber.getText() + " to " + selected_person +
                                        " Please Wait...", Toast.LENGTH_LONG)
                                .show();
                        Toast.makeText(getApplicationContext(),
                                "Transaction Complete", Toast.LENGTH_SHORT)
                                .show();
                        popupWindow.dismiss();
                    }});
                // popupWindow.showAsDropDown(sendButton, 50, -30);
                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

            }});
        cancelButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                Intent intent;
                intent = new Intent(SendActivity.this, HomeActivity.class);
                startActivity(intent);
            }});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_send_request, menu);
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

    public Button getSendButton() {
        return sendButton;
    }

    public void setSendButton(Button sendButton) {
        this.sendButton = sendButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public void setCancelButton(Button cancelButton) {
        this.cancelButton = cancelButton;
    }

    public ListView getSelectionList() {
        return selectionList;
    }

    public void setSelectionList(ListView selectionList) {
        this.selectionList = selectionList;
    }

    public TextView getDisplayNumber() {
        return displayNumber;
    }

    public void setDisplayNumber(TextView displayNumber) {
        this.displayNumber = displayNumber;
    }

    public TextView getDollarSignView() {
        return dollarSignView;
    }

    public void setDollarSignView(TextView dollarSignView) {
        this.dollarSignView = dollarSignView;
    }

    public TextView getSendPopWindowText2() {
        return SendPopWindowText2;
    }

    public void setSendPopWindowText2(TextView sendPopWindowText2) {
        SendPopWindowText2 = sendPopWindowText2;
    }

    public TextView getSendPopWindowPerson() {
        return SendPopWindowPerson;
    }

    public void setSendPopWindowPerson(TextView sendPopWindowPerson) {
        SendPopWindowPerson = sendPopWindowPerson;
    }

    public String getSend_amount() {
        return send_amount;
    }

    public void setSend_amount(String send_amount) {
        this.send_amount = send_amount;
    }

    public String getSelected_person() {
        return selected_person;
    }

    public void setSelected_person(String selected_person) {
        this.selected_person = selected_person;
    }
}