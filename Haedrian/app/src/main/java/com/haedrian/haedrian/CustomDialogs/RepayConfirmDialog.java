package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;


/**
 * Created by Logan on 6/22/2015.
 */
public class RepayConfirmDialog extends Dialog {

    private TextView messageTV;
    private Button confirm, cancel;

    private String message;

    public RepayConfirmDialog(Context context, String message) {
        super(context);

        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_repay_confirmation);

        confirm = (Button) findViewById(R.id.dialog_confirm);
        cancel = (Button) findViewById(R.id.dialog_cancel);
        messageTV = (TextView) findViewById(R.id.dialog_confirmation_message);

        messageTV.setText(message);
    }

    public Button getConfirmButton() {
        return this.confirm;
    }

    public Button getCancelButton() {
        return this.cancel;
    }
}
