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
    private String totalEstimatedLoanCost;
    private String totalEstimatedLoanCostDisplay;
    private String currency;
    private String totalOverdue;
    private String repayTimeUnit;
    private String loanDescriptor;
    private String interestRate;
    private String interestFrequency;
    private String startingBalance;
    private String currentBalance;
    private String startingBalanceDisplay;
    private String currentBalanceDisplay;

    public LoanInfoModel(JSONObject loanInfo) throws JSONException {
        this.loanId = loanInfo.getInt("loan_id");
        this.repayEvery = loanInfo.getInt("repay_every");
        this.numberOfRepayments = loanInfo.getInt("number_of_repayments");
        this.totalEstimatedLoanCost = loanInfo.getString("total_estimated_loan_cost");
        this.currency = loanInfo.getString("currency");
        this.totalOverdue = loanInfo.getString("total_overdue");
        this.repayTimeUnit = loanInfo.getString("repay_time_unit");
        this.loanDescriptor = loanInfo.getString("loan_descriptor");
        this.currentBalance = loanInfo.getString("current_balance");
        this.interestRate = loanInfo.getString("interest_rate");
        this.interestFrequency = loanInfo.getString("interest_frequency");
        this.startingBalance = loanInfo.getString("starting_balance");
        this.startingBalanceDisplay = loanInfo.getString("starting_balance_display");
        this.currentBalanceDisplay = loanInfo.getString("current_balance_display");
        this.totalEstimatedLoanCostDisplay = loanInfo.getString("total_estimated_loan_cost_display");
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

    public String getTotalEstimatedLoanCost() {
        return totalEstimatedLoanCost;
    }

    public void setTotalEstimatedLoanCost(String totalEstimatedLoanCost) {
        this.totalEstimatedLoanCost = totalEstimatedLoanCost;
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

    public String getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(String currentBalance) {
        this.currentBalance = currentBalance;
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

    public String getStartingBalance() {
        return startingBalance;
    }

    public void setStartingBalance(String startingBalance) {
        this.startingBalance = startingBalance;
    }

    public String getTotalEstimatedLoanCostDisplay() {
        return totalEstimatedLoanCostDisplay;
    }

    public void setTotalEstimatedLoanCostDisplay(String totalEstimatedLoanCostDisplay) {
        this.totalEstimatedLoanCostDisplay = totalEstimatedLoanCostDisplay;
    }

}
