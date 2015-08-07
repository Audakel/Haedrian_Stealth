package com.haedrian.curo.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Logan on 6/5/2015.
 */
public class BuyOrderModel implements Parcelable {

    private String status, paymentOutlet, instructions, btcAmount, currencyAmount, paymentMethodFee, haedrianFee, amount;

    public BuyOrderModel() { }

    private BuyOrderModel(Parcel source) {
        status = source.readString();
        paymentOutlet = source.readString();
        instructions = source.readString();
        btcAmount = source.readString();
        currencyAmount = source.readString();
        paymentMethodFee = source.readString();
        haedrianFee = source.readString();
        amount = source.readString();
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

    public String getHaedrianFee() {
        return haedrianFee;
    }

    public void setHaedrianFee(String haedrianFee) {
        this.haedrianFee = haedrianFee;
    }

    public String getAmount() { return this.amount; }

    public void setAmount(String amount) {
        this.amount = amount;
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
        dest.writeString(haedrianFee);
        dest.writeString(amount);
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

    @Override
    public String toString() {
        return "BuyOrderModel{" +
                "status='" + status + '\'' +
                ", paymentOutlet='" + paymentOutlet + '\'' +
                ", instructions='" + instructions + '\'' +
                ", btcAmount='" + btcAmount + '\'' +
                ", currencyAmount='" + currencyAmount + '\'' +
                ", paymentMethodFee='" + paymentMethodFee + '\'' +
                '}';
    }

}
