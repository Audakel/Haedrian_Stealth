package com.haedrian.haedrian.HomeScreen;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.lenddo.sdk.core.LenddoEventListener;
import com.lenddo.sdk.models.FormDataCollector;
import com.lenddo.sdk.utils.UIHelper;
import com.lenddo.sdk.widget.LenddoButton;

import java.util.Calendar;

public class CreditCheckActivity extends ActionBarActivity implements LenddoEventListener {

    private UIHelper helper;
    EditText mFirstNameEditText, mLastNameEditText, mEmailEditText, mAddressEditText, mBirthDateEditText, mLoanEditText;
    Button dobButton;
    TextView dateOfBirth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_check);

        dateOfBirth = (TextView) findViewById(R.id.dobButton);
        initEditTexts();

        helper = new UIHelper(this, this);

        LenddoButton lenddoButton = (LenddoButton) findViewById(R.id.verifyButton);
        lenddoButton.setUiHelper(helper);
        lenddoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fillOutForm();
            }
        });

        dobButton = (Button) findViewById(R.id.dobButton);
        dobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callCalenderDisplay();
            }
        });



//
    }

    private void callCalenderDisplay() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        final DatePickerDialog datePicker = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                c.set(year, monthOfYear, dayOfMonth);
                dateOfBirth.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));
            }
        }, year, month, day);
        datePicker.show();
    }

    private void fillOutForm() {
        FormDataCollector formData = new FormDataCollector();
        formData.setUserId("123456789");
        formData.setLastName(mLastNameEditText.getText().toString());
        formData.setFirstName(mFirstNameEditText.getText().toString());
        formData.setEmail(mEmailEditText.getText().toString());
        formData.setDateOfBirth(mBirthDateEditText.getText().toString());

        //send custom fields
        formData.putField("Loan_Amount", mLoanEditText.getText().toString());

        formData.validate();
    }

    private void initEditTexts() {
        mFirstNameEditText = (EditText) findViewById(R.id.firstNameEditText);
        mLastNameEditText = (EditText) findViewById(R.id.lastNameEditText);
        mEmailEditText = (EditText) findViewById(R.id.emailEditText);
        mAddressEditText = (EditText) findViewById(R.id.addressEditText);
        mBirthDateEditText = (EditText) findViewById(R.id.birthDateEditText);
        mLoanEditText = (EditText) findViewById(R.id.loanEditText);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credit_check, menu);
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

    @Override
    public boolean onButtonClicked(FormDataCollector collector) {
        return false;
    }

    @Override
    public void onAuthorizeComplete(FormDataCollector collector) {

    }

    @Override
    public void onAuthorizeCanceled(FormDataCollector collector) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }
}
