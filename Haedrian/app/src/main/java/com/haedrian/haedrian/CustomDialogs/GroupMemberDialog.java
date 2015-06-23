package com.haedrian.haedrian.CustomDialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.util.Log;
import android.view.Window;
import android.widget.ListView;
import android.widget.TextView;

import com.haedrian.haedrian.Adapters.PaymentMethodListAdapter;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;

import org.w3c.dom.Text;

import java.util.Locale;

/**
 * Created by Logan on 6/15/2015.
 */
public class GroupMemberDialog extends Dialog {

    private UserModel user;
    private TextView name, phoneNumber, id;

    public GroupMemberDialog(Context context, UserModel user) {
        super(context);
        this.user = user;
    }

    @Override
    protected void onCreate(Bundle savedBundle) {
        super.onCreate(savedBundle);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_group_member);

        name = (TextView) findViewById(R.id.dialog_member_name);
        phoneNumber = (TextView) findViewById(R.id.dialog_phone_number);
        id = (TextView) findViewById(R.id.dialog_id);

        name.setText(user.getFirstName() + " " + user.getLastName());
        
        String formattedNumber = PhoneNumberUtils.formatNumber(user.getPhoneNumber());

        phoneNumber.setText(formattedNumber);
        id.setText(user.getId());

    }
}
