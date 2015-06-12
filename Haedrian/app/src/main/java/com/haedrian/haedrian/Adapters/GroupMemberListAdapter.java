package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.haedrian.haedrian.Models.UserModel;
import com.haedrian.haedrian.R;

import org.w3c.dom.Text;

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        GroupMemberRowHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new GroupMemberRowHolder();
            holder.memberName = (TextView) row.findViewById(R.id.member_name);
            holder.memberAmount = (EditText) row.findViewById(R.id.member_amount);

            row.setTag(holder);
        }
        else {
            holder = (GroupMemberRowHolder)row.getTag();
        }

        String name = groupMembers.get(position).getFirstName() + " " + groupMembers.get(position).getLastName();
        SpannableString underlinedName = new SpannableString(name);
        underlinedName.setSpan(new UnderlineSpan(), 0, underlinedName.length(), 0);
        holder.memberName.setText(underlinedName);

        final int positionFinal = position;
        holder.memberAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if ( ! hasFocus) {
                    Log.v("TEST", "HERE");
                    final EditText memberAmount = (EditText) v;
                    if ( ! memberAmount.getText().toString().equals("")) {
                        groupMembers.get(positionFinal).setAmount(Long.parseLong(memberAmount.getText().toString()));
                    }
                }
            }
        });

        return row;
    }

    static class GroupMemberRowHolder {
        TextView memberName;
        EditText memberAmount;
    }

}
