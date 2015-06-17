package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;

import org.w3c.dom.Text;

/**
 * Created by Logan on 5/28/2015.
 */
public class BuyInstructionsDialog extends Dialog {

    private Button closeButton;
    private TextView instructionsTV;

    private String instructions;

    public BuyInstructionsDialog(Context context, String instructions) {
        super(context);

        this.instructions = instructions;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_buy_instructions);

        instructionsTV = (TextView) findViewById(R.id.instructions);

        Spanned span = Html.fromHtml(instructions);
        instructionsTV.setText(span);

        closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
