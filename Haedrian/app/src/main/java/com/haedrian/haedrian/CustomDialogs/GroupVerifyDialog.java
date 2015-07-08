package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.Application.ApplicationController;
import com.haedrian.haedrian.R;

import java.util.Currency;
import java.util.Locale;

/**
 * Created by Logan on 5/18/2015.
 */
public class GroupVerifyDialog extends Dialog {

    private String total;

    private Context context;

    private Button confirmButton;
    private TextView totalTV;

    public GroupVerifyDialog(Context context, String total) {
        super(context);

        this.context = context;
        this.total = total;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_group_verify);

        confirmButton = (Button) findViewById(R.id.confirm_button);
        totalTV = (TextView) findViewById(R.id.order_amount);

        String currency = ApplicationController.getSetCurrencySign();
        totalTV.setText(currency + total);

    }

    public Button getConfirmButton() { return this.confirmButton; }
}
