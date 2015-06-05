package com.haedrian.haedrian.Models;

import android.os.Parcel;
import android.os.Parcelable;

import com.haedrian.haedrian.CustomDialogs.BuyInstructionsDialog;

/**
 * Created by Logan on 6/5/2015.
 */
public class BuyOrderModel implements Parcelable {

    private String status, paymentOutlet, instructions, btcAmount, currencyAmount,paymentMethodFee;

    public BuyOrderModel() { }

    private BuyOrderModel(Parcel source) {
        status = source.readString();
        paymentOutlet = source.readString();
        instructions = source.readString();
        btcAmount = source.readString();
        currencyAmount = source.readString();
        paymentMethodFee = source.readString();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentOutlet() {
        return paymentOutlet;
    }

    public void setPaymentOutlet(String paymentOutlet) {
        this.paymentOutlet = paymentOutlet;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
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

    public String getPaymentMethodFee() {
        return paymentMethodFee;
    }

    public void setPaymentMethodFee(String paymentMethodFee) {
        this.paymentMethodFee = paymentMethodFee;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(status);
        dest.writeString(paymentOutlet);
        dest.writeString(instructions);
        dest.writeString(btcAmount);
        dest.writeString(currencyAmount);
        dest.writeString(paymentMethodFee);
    }

    public static final Parcelable.Creator<BuyOrderModel> CREATOR = new Parcelable.Creator<BuyOrderModel>() {

        @Override
        public BuyOrderModel createFromParcel(Parcel source) {
            return new BuyOrderModel(source);
        }

        @Override
        public BuyOrderModel[] newArray(int size) {
            return new BuyOrderModel[size];
        }
    };
}
