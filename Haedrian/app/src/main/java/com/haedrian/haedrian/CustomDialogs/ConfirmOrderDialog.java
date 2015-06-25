package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Logan on 5/18/2015.
 */
public class ConfirmOrderDialog extends Dialog {

    private String currencyAmount, bitcoinAmount, totalAmount;
    private Context context;

    private TextView orderDetails;
    private Button confirmButton;


    public ConfirmOrderDialog(Context context, String currencyAmount, String bitcoinAmount, String totalAmount) {
        super(context);

        this.context = context;
        this.currencyAmount = currencyAmount;
        this.bitcoinAmount = bitcoinAmount;
        this.totalAmount = totalAmount;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_order);

        orderDetails = (TextView) findViewById(R.id.order_details);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        Currency currency = Currency.getInstance(Locale.getDefault());

        String confirmMessage = context.getString(R.string.about_to_deposit) + " "
                + currency.getSymbol() + currencyAmount + " ("
                + bitcoinAmount + "BTC) "
                + context.getString(R.string.to_your_wallet_for)
                + " " + totalAmount + ".";

        orderDetails.setText(confirmMessage);

    }

    public Button getConfirmButton() { return this.confirmButton; }
}
