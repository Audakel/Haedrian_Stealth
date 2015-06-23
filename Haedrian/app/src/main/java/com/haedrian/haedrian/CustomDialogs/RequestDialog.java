package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Currency;
import java.util.List;
import java.util.Locale;

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
        setContentView(R.layout.dialog_request);

        notNowButton = (Button) findViewById(R.id.dialog_not_now);
        yesButton = (Button) findViewById(R.id.dialog_yes);
        requestorTV = (TextView) findViewById(R.id.dialog_requestor);
        amountTV = (TextView) findViewById(R.id.dialog_request_amount);

        String requestorId = request.getString("requestorId");
        ParseQuery<ParseObject> userQuery = new ParseQuery<ParseObject>("_User");
        userQuery.whereEqualTo("objectId", requestorId);
        userQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if (e == null) {
                    if (parseObjects.size() > 0) {
                        String amount = String.valueOf(request.getNumber("amountCurrency"));
                        String usersName = parseObjects.get(0).getString("firstName") + " " + parseObjects.get(0).getString("lastName");
                        Currency currency = Currency.getInstance(Locale.getDefault());
                        amountTV.setText(currency.getSymbol() + amount);
                        requestorTV.setText(usersName);
                    }
                    else {

                    }
                }
                else {
                }
            }
        });

    }

    public Button getYesButton() { return this.yesButton; }

    public Button getNotNowButton() { return this.notNowButton; }
}
