package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.parse.ParseObject;

import java.math.BigDecimal;

/**
 * Created by Logan on 4/22/2015.
 */
public class RequestDialog extends Dialog {

    private ParseObject request;

    private Button notNowButton, yesButton;
    private TextView requestorTV, amountTV;

    public RequestDialog(Context context, ParseObject request) {
        super(context);

        this.request = request;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.request_dialog);

        notNowButton = (Button) findViewById(R.id.dialog_not_now);
        yesButton = (Button) findViewById(R.id.dialog_yes);
        requestorTV = (TextView) findViewById(R.id.dialog_requestor);
        amountTV = (TextView) findViewById(R.id.dialog_request_amount);

        String requestorId = request.getString("requestorId");
        String amount = String.valueOf(request.getNumber("amountCurrency"));

        amountTV.setText("$" + amount);

    }

    public Button getYesButton() { return this.yesButton; }

    public Button getNotNowButton() { return this.notNowButton; }
}
