package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.parse.ParseObject;

import java.util.ArrayList;

/**
 * Created by Logan on 4/25/2015.
 */
public class TransactionListAdapter extends ArrayAdapter<ParseObject> {

    private Context context;
    private int resource;
    private ArrayList<ParseObject> transactions;

    public TransactionListAdapter(Context context, int resource, ArrayList<ParseObject> transactions) {
        super(context, resource, transactions);
        this.context = context;
        this.resource = resource;
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionDataHolder holder = null;
        Log.v("TEST", "Got in here");

        if(row == null)
        {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new TransactionDataHolder();
            holder.transactionType = (TextView) row.findViewById(R.id.transaction_type_textview);
            holder.amount = (TextView) row.findViewById(R.id.amount_textview);
            holder.details = (TextView) row.findViewById(R.id.transaction_details_textview);

            row.setTag(holder);
        }
        else
        {
            holder = (TransactionDataHolder)row.getTag();
        }

        holder.transactionType.setText("Sent Bitcoins");
        holder.amount.setText("$" + transactions.get(position).getNumber("amountCurrency"));
//        holder.details.setText();

        return row;
    }
    static class TransactionDataHolder {
        TextView transactionType, amount, details;
    }
}
