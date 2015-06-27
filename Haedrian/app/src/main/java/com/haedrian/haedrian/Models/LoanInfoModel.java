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
    private double totalEstimatedLoanCost;
    private String currency;
    private double totalOverdue;
    private String repayTimeUnit;
    private String loanDescriptor;
    private double currentBalance;
    private double interestRate;
    private String interestFrequency;
    private double startingBalance;

    public LoanInfoModel(JSONObject loanInfo) throws JSONException {
        this.loanId = loanInfo.getInt("loan_id");
        this.repayEvery = loanInfo.getInt("repay_every");
        this.numberOfRepayments = loanInfo.getInt("number_of_repayments");
        this.totalEstimatedLoanCost = loanInfo.getDouble("total_estimated_loan_cost");
        this.currency = loanInfo.getString("currency");
        this.totalOverdue = loanInfo.getInt("total_overdue");
        this.repayTimeUnit = loanInfo.getString("repay_time_unit");
        this.loanDescriptor = loanInfo.getString("loan_descriptor");
        this.currentBalance = loanInfo.getDouble("current_balance");
        this.interestRate = loanInfo.getDouble("interest_rate");
        this.interestFrequency = loanInfo.getString("interest_frequency");
        this.startingBalance = loanInfo.getDouble("starting_balance");
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

    public double getTotalEstimatedLoanCost() {
        return totalEstimatedLoanCost;
    }

    public void setTotalEstimatedLoanCost(double totalEstimatedLoanCost) {
        this.totalEstimatedLoanCost = totalEstimatedLoanCost;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public double getTotalOverdue() {
        return totalOverdue;
    }

    public void setTotalOverdue(double totalOverdue) {
        this.totalOverdue = totalOverdue;
    }

    public String getRepayTimeUnit() {
        return repayTimeUnit;
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

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getInterest_rate() {
        return interestRate;
    }

    public void setInterest_rate(double interest_rate) {
        this.interestRate = interest_rate;
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


}
