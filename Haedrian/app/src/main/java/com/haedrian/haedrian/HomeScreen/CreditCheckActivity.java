package com.haedrian.haedrian.HomeScreen;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.haedrian.haedrian.R;
import com.lenddo.sdk.core.LenddoEventListener;
import com.lenddo.sdk.models.FormDataCollector;
import com.lenddo.sdk.utils.UIHelper;
import com.lenddo.sdk.widget.LenddoButton;

public class CreditCheckActivity extends ActionBarActivity implements LenddoEventListener {

    private UIHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_check);

        //helper = new UIHelper(CreditCheckActivity.this);

        LenddoButton button = (LenddoButton) findViewById(R.id.verifyButton);
        button.setUiHelper(helper);




//
    }

    public boolean onButtonClicked(FormDataCollector formData) {
        //auto-collect
        formData.collect(CreditCheckActivity.this, R.id.formContainer);

        //place partner defined user identifier
        formData.setUserId("123456789");
        formData.setLastName(lastName.getText().toString());
        formData.setFirstName(firstName.getText().toString());
        formData.setEmail(email.getText().toString());
        formData.setDateOfBirth(dateOfBirth.getText().toString());

        //send custom fields
        formData.putField("Loan_Amount", loanAmount.getText().toString());

        formData.validate();
        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_credit_check, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onButtonClicked(FormDataCollector collector) {
        return false;
    }

    @Override
    public void onAuthorizeComplete(FormDataCollector collector) {

    }

    @Override
    public void onAuthorizeCanceled(FormDataCollector collector) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        helper.onActivityResult(requestCode, resultCode, data);
    }
}
