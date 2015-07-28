package com.haedrian.haedrian.Models;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by audakel on 6/27/15.
 */
public class LoanInfoModel {
    private int loanId;
    private int repayEvery;
    private int numberOfRepayments;
    private double totalInterest;
    private String totalInterestDisplay;
    private String currency;
    private String totalOverdue;
    private String repayTimeUnit;
    private String loanDescriptor;
    private String interestRate;
    private String interestFrequency;
    private double startingBalance;
    private double currentBalance;
    private String startingBalanceDisplay;
    private String currentBalanceDisplay;

    public LoanInfoModel(JSONObject loanInfo) throws JSONException {
        this.loanId = loanInfo.getInt("loan_id");
        this.repayEvery = loanInfo.getInt("repay_every");
        this.numberOfRepayments = loanInfo.getInt("number_of_repayments");
        this.totalInterest = loanInfo.getDouble("total_interest");
        this.currency = loanInfo.getString("currency");
        this.totalOverdue = loanInfo.getString("total_overdue");
        this.repayTimeUnit = loanInfo.getString("repay_time_unit");
        this.loanDescriptor = loanInfo.getString("loan_descriptor");
        this.currentBalance = loanInfo.getDouble("current_balance");
        this.interestRate = loanInfo.getString("interest_rate");
        this.interestFrequency = loanInfo.getString("interest_frequency");
        this.startingBalance = loanInfo.getDouble("starting_balance");
        this.startingBalanceDisplay = loanInfo.getString("starting_balance_display");
        this.currentBalanceDisplay = loanInfo.getString("current_balance_display");
        this.totalInterestDisplay = loanInfo.getString("total_interest_display");
    }

    public int getLoanId() {
        return loanId;
    }

    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }

    public int getRepayEvery() {
        return repayEvery;
    }

    public void setRepayEvery(int repayEvery) {
        this.repayEvery = repayEvery;
    }

    public int getNumberOfRepayments() {
        return numberOfRepayments;
    }

    public void setNumberOfRepayments(int numberOfRepayments) {
        this.numberOfRepayments = numberOfRepayments;
    }

    public double getTotalInterest() {
        return totalInterest;
    }

    public void setTotalInterest(double totalInterest) {
        this.totalInterest = totalInterest;
    }

    public String getTotalInterestDisplay() {
        return totalInterestDisplay;
    }

    public void setTotalInterestDisplay(String totalInterestDisplay) {
        this.totalInterestDisplay = totalInterestDisplay;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(String totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public String getRepayTimeUnit() {
        String mRepayTimeUnit = repayTimeUnit.toLowerCase();

        Boolean pluralTime = "s".equals(mRepayTimeUnit.substring(mRepayTimeUnit.length() - 1));

        if (repayEvery == 1){
            if (pluralTime){
                return mRepayTimeUnit.substring(0, mRepayTimeUnit.length()-1);
            }
            else{
                return mRepayTimeUnit;
            }
        }
        else{
            if (pluralTime){
                return mRepayTimeUnit;
            }
            else{
                return mRepayTimeUnit + "s";
            }

        }
    }

    public void setRepayTimeUnit(String repayTimeUnit) {
        this.repayTimeUnit = repayTimeUnit;
    }

    public String getLoanDescriptor() {
        return loanDescriptor;
    }

    public void setLoanDescriptor(String loanDescriptor) {
        this.loanDescriptor = loanDescriptor;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getInterestFrequency() {
        return interestFrequency;
    }

    public void setInterestFrequency(String interestFrequency) {
        this.interestFrequency = interestFrequency;
    }

    public double getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(double startingBalance) {
        this.startingBalance = startingBalance;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public String getStartingBalanceDisplay() {
        return startingBalanceDisplay;
    }

    public void setStartingBalanceDisplay(String startingBalanceDisplay) {
        this.startingBalanceDisplay = startingBalanceDisplay;
    }

    public String getCurrentBalanceDisplay() {
        return currentBalanceDisplay;
    }

    public void setCurrentBalanceDisplay(String currentBalanceDisplay) {
        this.currentBalanceDisplay = currentBalanceDisplay;
    }




}
