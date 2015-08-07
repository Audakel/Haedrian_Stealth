package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.TextView;

import com.haedrian.curo.R;

/**
 * Created by Logan on 7/9/2015.
 */
public class DateDialog extends Dialog {

    private String date;

    public DateDialog(Context context, String date) {
        super(context);
        this.date = date;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_date);

        TextView dateTV = (TextView) findViewById(R.id.date);

        dateTV.setText(date);


    }

}
