package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.curo.R;

/**
 * Created by Logan on 3/30/2015.
 */
public class SendConfirmationDialog extends Dialog {

    private Button cancel, send;
    private TextView amountTV, recipientTV;

    private String amount, recipient;

    public SendConfirmationDialog(Context context, String amount, String recipient) {
        super(context);
        this.amount = amount;
        this.recipient = recipient;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_send_confirmation);

        cancel = (Button) findViewById(R.id.dialog_cancel);
        send = (Button) findViewById(R.id.dialog_send_button);
        amountTV = (TextView) findViewById(R.id.dialog_amount);
        recipientTV = (TextView) findViewById(R.id.dialog_recipient);

        amountTV.setText(amount);
        recipientTV.setText(recipient);

    }

    public Button getSendButton() {
        return this.send;
    }

    public Button getCancelButton() {
        return this.cancel;
    }
}
