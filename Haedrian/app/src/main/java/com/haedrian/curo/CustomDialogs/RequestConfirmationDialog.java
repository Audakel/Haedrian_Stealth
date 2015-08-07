package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.curo.R;

/**
 * Created by Logan on 4/21/2015.
 */
public class RequestConfirmationDialog extends Dialog {

    private Button cancel, request;
    private TextView amountTV, recipientTV;

    private String amount, recipient;

    public RequestConfirmationDialog(Context context, String amount, String recipient) {
        super(context);
        this.amount = amount;
        this.recipient = recipient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_request_confirmation);

        cancel = (Button) findViewById(R.id.dialog_request_cancel);
        request = (Button) findViewById(R.id.dialog_request_button);
        amountTV = (TextView) findViewById(R.id.dialog_request_amount);
        recipientTV = (TextView) findViewById(R.id.dialog_request_recipient);

        amountTV.setText(amount);
        recipientTV.setText(recipient);

    }

    public Button getSendButton() {
        return this.request;
    }

    public Button getCancelButton() {
        return this.cancel;
    }
}
