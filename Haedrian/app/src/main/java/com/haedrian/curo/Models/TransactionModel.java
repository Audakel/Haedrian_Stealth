package com.haedrian.curo.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Logan on 6/3/2015.
 */
public class TransactionModel implements Parcelable{
    private String id, status, feeAmount, amount, date, entryType, sender, target, currency;

    public TransactionModel() {}

    private TransactionModel(Parcel source) {
        id = source.readString();
        status = source.readString();
        feeAmount = source.readString();
        amount = source.readString();
        date = source.readString();
        entryType = source.readString();
        sender = source.readString();
        target = source.readString();
        currency = source.readString();
    }

    public String getId() { return id; }

    public void setId(String id) { this.id = id; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFeeAmount() {
        return feeAmount;
    }

    public void setFeeAmount(String feeAmount) {
        this.feeAmount = feeAmount;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEntryType() {
        return entryType;
    }

    public void setEntryType(String entryType) {
        this.entryType = entryType;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public String getCurrency() { return currency; }

    public void setCurrency(String currency) { this.currency = currency; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(feeAmount);
        dest.writeString(amount);
        dest.writeString(date);
        dest.writeString(entryType);
        dest.writeString(sender);
        dest.writeString(target);
        dest.writeString(currency);
    }

    public static final Parcelable.Creator<TransactionModel> CREATOR = new Parcelable.Creator<TransactionModel>() {

        @Override
        public TransactionModel createFromParcel(Parcel source) {
            return new TransactionModel(source);
        }

        @Override
        public TransactionModel[] newArray(int size) {
            return new TransactionModel[size];
        }
    };
}
