package com.haedrian.haedrian.Modles;

/**
 * Created by audakel on 3/21/15.
 */
public class CurrencyModel {
    String currencyName;
    int buy;
    int sell;
    String symbol;

    public CurrencyModel(String currencyName, int buy, int sell, String symbol) {
        this.currencyName = currencyName;
        this.buy = buy;
        this.sell = sell;
        this.symbol = symbol;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String name) {
        this.currencyName = name;
    }

    public int getBuy() {
        return buy;
    }

    public void setBuy(int buy) {
        this.buy = buy;
    }

    public int getSell() {
        return sell;
    }

    public void setSell(int sell) {
        this.sell = sell;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
}
