package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.curo.R;

/**
 * Created by Logan on 4/22/2015.
 */
public class RequestDialog extends Dialog {


    private Button notNowButton, yesButton;
    private TextView requestorTV, amountTV;

    public RequestDialog(Context context) {
        super(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request);

        notNowButton = (Button) findViewById(R.id.dialog_not_now);
        yesButton = (Button) findViewById(R.id.dialog_yes);
        requestorTV = (TextView) findViewById(R.id.dialog_requestor);
        amountTV = (TextView) findViewById(R.id.dialog_request_amount);


    }

    public Button getYesButton() { return this.yesButton; }

    public Button getNotNowButton() { return this.notNowButton; }
}
