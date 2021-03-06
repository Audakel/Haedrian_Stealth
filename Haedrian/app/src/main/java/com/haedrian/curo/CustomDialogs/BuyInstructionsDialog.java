package com.haedrian.curo.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;

import com.haedrian.curo.R;

/**
 * Created by Logan on 5/28/2015.
 */
public class BuyInstructionsDialog extends Dialog {

    private Button closeButton;
    private WebView instructionsWV;

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

        instructionsWV = (WebView) findViewById(R.id.instructions);

        String mime = "text/html";
        String encoding = "utf-8";

        instructionsWV.getSettings().setJavaScriptEnabled(true);
        instructionsWV.loadDataWithBaseURL(null, instructions, mime, encoding, null );

        closeButton = (Button) findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

}
