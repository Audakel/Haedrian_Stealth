package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.haedrian.haedrian.CustomDialogs.GroupMemberDialog;
import com.haedrian.haedrian.HomeScreen.AddMoney.GroupBuyActivity;
import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;
import com.lenddo.sdk.models.User;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;
import java.util.Objects;

/**
 * Created by Logan on 6/11/2015.
 */
public class GroupMemberListAdapter extends ArrayAdapter<UserModel> {

    private ArrayList<UserModel> groupMembers;
    private Context context;
    private int resource;

    public GroupMemberListAdapter(Context context, int resource, ArrayList<UserModel> groupMembers) {
        super(context, resource, groupMembers);
        this.groupMembers = groupMembers;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public int getCount() {
        return groupMembers.size();
    }

    @Override
    public UserModel getItem(int pos) {
        return groupMembers.get(pos);
    }

    public ArrayList<UserModel> getUsers() {
        return groupMembers;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GroupMemberRowHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new GroupMemberRowHolder();
            holder.memberName = (TextView) row.findViewById(R.id.member_name);
            holder.memberAmount = (EditText) row.findViewById(R.id.member_amount);
            holder.currencySign = (TextView) row.findViewById(R.id.currency_sign);

            row.setTag(holder);
        }
        else {
            holder = (GroupMemberRowHolder)row.getTag();
        }

        String name = groupMembers.get(position).getFirstName() + " " + groupMembers.get(position).getLastName();
        SpannableString underlinedName = new SpannableString(name);
        underlinedName.setSpan(new UnderlineSpan(), 0, underlinedName.length(), 0);
        holder.memberName.setText(underlinedName);

        Currency currency = Currency.getInstance(Locale.getDefault());
        holder.currencySign.setText(currency.getSymbol());

        final int positionFinal = position;

        holder.memberName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupMemberDialog dialog = new GroupMemberDialog(context, groupMembers.get(position));
                dialog.show();
            }
        });

        holder.memberAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    groupMembers.get(position).setAmount(Long.parseLong(s.toString()));
                }
            }
        });

        return row;
    }

    static class GroupMemberRowHolder {
        TextView memberName, currencySign;
        EditText memberAmount;
    }

}
