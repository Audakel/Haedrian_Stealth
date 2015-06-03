package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;

/**
 * Created by Logan on 5/18/2015.
 */
public class ConfirmOrderDialog extends Dialog {

    private String currencyAmount, bitcoinAmount;
    private Context context;

    private TextView orderDetails;
    private Button confirmButton;


    public ConfirmOrderDialog(Context context, String currencyAmount, String bitcoinAmount) {
        super(context);

        this.context = context;
        this.currencyAmount = currencyAmount;
        this.bitcoinAmount = bitcoinAmount;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_order);

        orderDetails = (TextView) findViewById(R.id.order_details);
        confirmButton = (Button) findViewById(R.id.confirm_button);

        Resources resources = context.getResources();

        orderDetails.setText(bitcoinAmount + " "
                + resources.getString(R.string.btc_for) + " "
                + currencyAmount);

    }

    public Button getConfirmButton() { return this.confirmButton; }
}
