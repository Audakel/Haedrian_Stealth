package com.haedrian.haedrian.HomeScreen.Contacts;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.haedrian.haedrian.Database.DBHelper;
import com.haedrian.haedrian.R;


public class ContactsActivity extends ActionBarActivity implements
        ContactsListFragment.OnContactsInteractionListener {

    String upcCode = "";
    private LinearLayout startScanButton;
    private EditText contactsET;
    private LinearLayout resultsLinearLayout;
    private TextView resultsNumber;
    private ListView dataList;


    private DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        // Set up ActionBar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        contactsET = (EditText) findViewById(R.id.contact_edit_text);
        resultsLinearLayout = (LinearLayout) findViewById(R.id.results_linear_layout);
        resultsNumber = (TextView) findViewById(R.id.results_number_textview);
        dataList = (ListView) findViewById(R.id.contact_list);


        // To do autocomplete with the listview
        contactsET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

                // Requery/Filter the adapter and then set it to listview here

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

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
//                Toast.makeText(this, "UPC code is : " + result.toString(), Toast.LENGTH_LONG).show();
                upcCode = contents.toString();
            } else {
//                Toast.makeText(this, "Fail!!", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onContactSelected(Cursor cursor, int position) {

    }

    @Override
    public void onSelectionCleared() {

    }
}
