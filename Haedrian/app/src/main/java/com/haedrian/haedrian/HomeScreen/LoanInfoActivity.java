package com.haedrian.haedrian.HomeScreen;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
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
        totalEstimatedLoanCost.setText(loanInfoModel.getTotalEstimatedLoanCostDisplay()+"");
        totalOverdue.setText(loanInfoModel.getTotalOverdue()+"");
        loanDescriptor.setText(loanInfoModel.getLoanDescriptor());
        interestRate.setText(loanInfoModel.getInterestRate()+"");
        interestFrequency.setText(loanInfoModel.getInterestFrequency()+"");
        startingBalance.setText(loanInfoModel.getStartingBalanceDisplay()+"");
        currentBalance.setText(loanInfoModel.getCurrentBalanceDisplay()+"");

    /*
        PieChart pieChart = (PieChart) findViewById(R.id.pieChart);

        ArrayList<Entry> yValues = new ArrayList<Entry>();
        yValues.add(new Entry((float) ((float) loanInfoModel.getStartingBalance() - loanInfoModel.getCurrentBalance()), 1));
        yValues.add(new Entry((float) loanInfoModel.getCurrentBalance(), 2));

        PieDataSet dataSet = new PieDataSet(yValues, "Loan Repayment");
        dataSet.setColors(new int[] {getResources().getColor(R.color.primary),
                getResources().getColor(R.color.accent)});

        ArrayList<String> xValues = new ArrayList<String>();
        xValues.add("Starting Balance");
        xValues.add("Current Balance");

        PieData data = new PieData(xValues, dataSet);
        pieChart.setData(data);
        pieChart.invalidate();

    */




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
