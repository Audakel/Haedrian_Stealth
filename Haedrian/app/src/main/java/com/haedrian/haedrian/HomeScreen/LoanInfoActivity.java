package com.haedrian.haedrian.HomeScreen;

import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.haedrian.haedrian.Models.LoanInfoModel;
import com.haedrian.haedrian.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        repayEvery.setText(loanInfoModel.getRepayEvery()+ " "+ loanInfoModel.getRepayTimeUnit());
        numberOfRepayments.setText(loanInfoModel.getNumberOfRepayments()+"");
        totalEstimatedLoanCost.setText(loanInfoModel.getTotalInterestDisplay()+"");
        totalOverdue.setText(loanInfoModel.getTotalOverdue()+"");
        loanDescriptor.setText(loanInfoModel.getLoanDescriptor());
        interestRate.setText(loanInfoModel.getInterestRate()+"%");
        interestFrequency.setText(loanInfoModel.getInterestFrequency() + "");
        startingBalance.setText(loanInfoModel.getStartingBalanceDisplay() + "");
        currentBalance.setText(loanInfoModel.getCurrentBalanceDisplay() + "");


        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);

        ArrayList<Entry> yValues = new ArrayList<Entry>();
//        What has been paid
        Double totalLoan = loanInfoModel.getStartingBalance() + loanInfoModel.getTotalInterest();
        Double amountPaidOff = totalLoan - loanInfoModel.getCurrentBalance();
        Double remainingBalance = totalLoan - amountPaidOff;

        yValues.add(new Entry(Float.parseFloat(String.valueOf(amountPaidOff)), 1));
        yValues.add(new Entry(Float.parseFloat(String.valueOf(remainingBalance)), 2));

        PieDataSet dataSet = new PieDataSet(yValues, ""); // Optional title for 2nd parameter
        dataSet.setColors(new int[]{getResources().getColor(R.color.primary),
                getResources().getColor(R.color.accent)});

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Paid off Balance");
        xValues.add("Remaining Balance");

        PieData data = new PieData(xValues, dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

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
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_loan_info, menu);
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
