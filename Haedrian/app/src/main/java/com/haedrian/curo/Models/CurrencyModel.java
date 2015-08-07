package com.haedrian.curo.Models;

/**
 * Created by audakel on 3/21/15.
 */
public class CurrencyModel {
    private String currencyName;
    private int buyRate;
    private int sellRate;
    private String symbol;

    public CurrencyModel(String currencyName, int buyRate, int sellRate, String symbol) {
        this.currencyName = currencyName;
        this.buyRate = buyRate;
        this.sellRate = sellRate;
        this.symbol = symbol;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String name) {
        this.currencyName = name;
    }

    public int getBuyRate() {
        return buyRate;
    }

    public void setBuyRate(int buyRate) {
        this.buyRate = buyRate;
    }

    public int getSellRate() {
        return sellRate;
    }

    public void setSellRate(int sellRate) {
        this.sellRate = sellRate;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
