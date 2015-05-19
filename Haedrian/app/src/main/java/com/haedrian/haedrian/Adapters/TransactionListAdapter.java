package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haedrian.haedrian.R;
import com.parse.ParseObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Locale;

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

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new TransactionDataHolder();
            holder.transactionType = (TextView) row.findViewById(R.id.transaction_type_textview);
            holder.amount = (TextView) row.findViewById(R.id.amount_textview);
            holder.details = (TextView) row.findViewById(R.id.transaction_details_textview);
            holder.imageType = (ImageView) row.findViewById(R.id.send_request_icon);

            row.setTag(holder);
        }
        else {
            holder = (TransactionDataHolder)row.getTag();
        }

        int transactionType = (Math.random()<0.5) ? 0 : 1;

        if (transactionType == 0) {
            holder.transactionType.setText("Sent Bitcoin");
            holder.imageType.setImageResource(R.drawable.send);
        }
        else {
            holder.transactionType.setText("Received Bitcoin");
            holder.imageType.setImageResource(R.drawable.receive);
        }

        Double currencyAmount = Double.parseDouble(String.valueOf(transactions.get(position).getNumber("amountCurrency")));
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        holder.amount.setText(currencyFormatter.format(currencyAmount));
//        holder.details.setText();

        return row;
    }
    static class TransactionDataHolder {
        TextView transactionType, amount, details;
        ImageView imageType;
    }
}
