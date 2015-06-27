package com.haedrian.haedrian.HomeScreen;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.haedrian.haedrian.Models.LoanInfoModel;
import com.haedrian.haedrian.R;

import org.json.JSONException;
import org.json.JSONObject;

public class LoanInfoActivity extends ActionBarActivity {
    // TODO:: import ButterKnife for view binding https://github.com/JakeWharton/butterknife

    private TextView loanId, loanDescriptor;
    private TextView repayEvery, repayTimeUnit;
    private TextView totalEstimatedLoanCost, totalOverdue;
    private TextView interestRate, interestFrequency;
    private TextView numberOfRepayments, currency;
    private TextView currentBalance, startingBalance;
    private LoanInfoModel loanInfoModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loan_info);

        Bundle bundle = getIntent().getExtras();
        String loanInfo = bundle.getString("loanInfo");

        try {
            loanInfoModel = new LoanInfoModel(new JSONObject(loanInfo));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        loanId = (TextView) findViewById(R.id.summary_loanId);
        repayEvery = (TextView) findViewById(R.id.summary_repay_every);
        numberOfRepayments = (TextView) findViewById(R.id.summary_total_repayments);
        totalEstimatedLoanCost = (TextView) findViewById(R.id.summary_loan_cost);
        totalOverdue = (TextView) findViewById(R.id.summary_total_overdue);
        loanDescriptor = (TextView) findViewById(R.id.summary_loan_description);
        currentBalance = (TextView) findViewById(R.id.summary_current_balance);
        interestRate = (TextView) findViewById(R.id.summary_interest_rate);
        interestFrequency = (TextView) findViewById(R.id.summary_interest_frequency);
        startingBalance = (TextView) findViewById(R.id.summary_starting_balance);


        loanId.setText(loanInfoModel.getLoanId()+"");
        repayEvery.setText(loanInfoModel.getRepayEvery()+"");
        numberOfRepayments.setText(loanInfoModel.getNumberOfRepayments()+"");
        totalEstimatedLoanCost.setText(loanInfoModel.getTotalEstimatedLoanCost()+"");
        totalOverdue.setText(loanInfoModel.getTotalOverdue()+"");
        loanDescriptor.setText(loanInfoModel.getLoanDescriptor());
        currentBalance.setText(loanInfoModel.getCurrentBalance()+"");
        interestRate.setText(loanInfoModel.getInterest_rate()+"");
        interestFrequency.setText(loanInfoModel.getInterestFrequency()+"");
        startingBalance.setText(loanInfoModel.getStartingBalance()+"");

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_loan_info, menu);
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
