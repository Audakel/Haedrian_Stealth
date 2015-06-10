package com.haedrian.haedrian.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Logan on 6/8/2015.
 */
public class BuyOrderHistoryModel implements Parcelable {

    private String id, status, outletTitle, createdAt, exchangeRate, instructions, expirationTime, paidTime, btcAmount, currencyAmount;

    public BuyOrderHistoryModel() {}

    private BuyOrderHistoryModel(Parcel source) {
        id = source.readString();
        status = source.readString();
        outletTitle = source.readString();
        createdAt = source.readString();
        exchangeRate = source.readString();
        instructions = source.readString();
        expirationTime = source.readString();
        paidTime = source.readString();
        btcAmount = source.readString();
        currencyAmount = source.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOutletTitle() {
        return outletTitle;
    }

    public void setOutletTitle(String outletTitle) {
        this.outletTitle = outletTitle;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(String exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getPaidTime() {
        return paidTime;
    }

    public void setPaidTime(String paidTime) {
        this.paidTime = paidTime;
    }

    public String getBtcAmount() {
        return btcAmount;
    }

    public void setBtcAmount(String btcAmount) {
        this.btcAmount = btcAmount;
    }

    public String getCurrencyAmount() {
        return currencyAmount;
    }

    public void setCurrencyAmount(String currencyAmount) {
        this.currencyAmount = currencyAmount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(outletTitle);
        dest.writeString(createdAt);
        dest.writeString(exchangeRate);
        dest.writeString(instructions);
        dest.writeString(expirationTime);
        dest.writeString(paidTime);
        dest.writeString(btcAmount);
        dest.writeString(currencyAmount);
    }

    public static final Parcelable.Creator<BuyOrderHistoryModel> CREATOR = new Parcelable.Creator<BuyOrderHistoryModel>() {

        @Override
        public BuyOrderHistoryModel createFromParcel(Parcel source) {
            return new BuyOrderHistoryModel(source);
        }

        @Override
        public BuyOrderHistoryModel[] newArray(int size) {
            return new BuyOrderHistoryModel[size];
        }
    };

    @Override
    public String toString() {
        return "BuyOrderHistoryModel{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", outletTitle='" + outletTitle + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", exchangeRate='" + exchangeRate + '\'' +
                ", instructions='" + instructions + '\'' +
                ", expirationTime='" + expirationTime + '\'' +
                ", paidTime='" + paidTime + '\'' +
                ", btcAmount='" + btcAmount + '\'' +
                ", currencyAmount='" + currencyAmount + '\'' +
                '}';
    }
}
