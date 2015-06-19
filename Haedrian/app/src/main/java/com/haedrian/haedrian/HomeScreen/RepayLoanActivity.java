package com.haedrian.haedrian.HomeScreen;

import android.app.ActionBar;
import android.app.Activity;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.haedrian.haedrian.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

public class RepayLoanActivity extends Activity {

    private Spinner microfinanceSpinner, groupPaymentsSpinner;
    private LinearLayout paymentTypeContainer, individualContainer;
    private RadioGroup individualGroup;
    private Button repayLoanButton;
    private TextView currencySymbol;
    private EditText amountEditText;

    private ArrayList<String> microfinanceInstitutions = new ArrayList<>();
    private ArrayList<String> groupPayments = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repay_loan);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        microfinanceInstitutions.add(getString(R.string.microfinance_institution));
        microfinanceInstitutions.add(getString(R.string.mentors_international));

        groupPayments.add(getString(R.string.pending_group_repayments));
        groupPayments.add("Dummy repayment");

        microfinanceSpinner = (Spinner) findViewById(R.id.microfinance_spinner);
        groupPaymentsSpinner = (Spinner) findViewById(R.id.group_payments_spinner);
        paymentTypeContainer = (LinearLayout) findViewById(R.id.payment_type_container);
        individualContainer = (LinearLayout) findViewById(R.id.individual_payment_container);
        individualGroup = (RadioGroup) findViewById(R.id.radio_group);
        repayLoanButton = (Button) findViewById(R.id.repay_loan_button);
        currencySymbol = (TextView) findViewById(R.id.currency_sign);
        amountEditText = (EditText) findViewById(R.id.amount_currency);

        amountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    repayLoanButton.setVisibility(View.VISIBLE);
                    repayLoanButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                }
                else {
                    repayLoanButton.setVisibility(View.GONE);
                }
            }
        });

        Currency currency = Currency.getInstance(Locale.getDefault());
        currencySymbol.setText(currency.getSymbol());


        ArrayAdapter<String> mfiAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, microfinanceInstitutions);
        microfinanceSpinner.setAdapter(mfiAdapter);
        microfinanceSpinner.setSelection(0);
        microfinanceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    paymentTypeContainer.setVisibility(View.VISIBLE);
                    paymentTypeContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                } else {
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        individualGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.individual) {
                    groupPaymentsSpinner.setVisibility(View.GONE);
                    individualContainer.setVisibility(View.VISIBLE);
                    individualContainer.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));

                } else if (checkedId == R.id.group) {
                    individualContainer.setVisibility(View.GONE);
                    groupPaymentsSpinner.setVisibility(View.VISIBLE);
                    groupPaymentsSpinner.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                }
            }
        });

        ArrayAdapter<String> groupPaymentsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, groupPayments);
        groupPaymentsSpinner.setAdapter(groupPaymentsAdapter);
        groupPaymentsSpinner.setSelection(0);
        groupPaymentsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    repayLoanButton.setVisibility(View.VISIBLE);
                    repayLoanButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.abc_slide_in_bottom));
                } else {
                    repayLoanButton.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_repay_loan, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
