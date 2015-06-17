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

import com.haedrian.haedrian.Models.TransactionModel;
import com.haedrian.haedrian.R;
import com.parse.ParseObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by Logan on 4/25/2015.
 */
public class TransactionListAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private ArrayList<TransactionModel> transactions;

    public TransactionListAdapter(Context context, int resource, ArrayList<TransactionModel> transactions) {
        super(context, resource, transactions);
        this.context = context;
        this.resource = resource;
        this.transactions = transactions;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionDataHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new TransactionDataHolder();
            holder.transactionType = (TextView) row.findViewById(R.id.transaction_type_textview);
            holder.amount = (TextView) row.findViewById(R.id.amount_textview);
            holder.toFrom = (TextView) row.findViewById(R.id.to_string);
            holder.details = (TextView) row.findViewById(R.id.transaction_details_textview);
            holder.imageType = (ImageView) row.findViewById(R.id.send_request_icon);

            row.setTag(holder);
        }
        else {
            holder = (TransactionDataHolder)row.getTag();
        }

        String transactionType = transactions.get(position).getEntryType();

        if (transactionType.equals("outgoing")) {
            holder.transactionType.setText(context.getResources().getString(R.string.outgoing_funds));
            holder.imageType.setImageResource(R.drawable.send);
            holder.toFrom.setText(context.getResources().getString(R.string.to));
            holder.details.setText(transactions.get(position).getTarget());
        }
        else if (transactionType.equals("incoming")) {
            holder.transactionType.setText(context.getResources().getString(R.string.incoming_funds));
            holder.imageType.setImageResource(R.drawable.receive);
            holder.toFrom.setText(context.getResources().getString(R.string.from));
            String sender = transactions.get(position).getSender();
            if (sender.equals("null")) {
                holder.details.setText(context.getResources().getString(R.string.outside_address));
            }
            else {
                holder.details.setText(sender);
            }
        }


        if ( ! transactions.get(position).getCurrency().equals("BTC")) {
            Double currencyAmount = Double.parseDouble(transactions.get(position).getAmount());
            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());
            holder.amount.setText(currencyFormatter.format(currencyAmount));
        }
        else {
            holder.amount.setText(transactions.get(position).getAmount());
        }


        return row;
    }
    static class TransactionDataHolder {
        TextView transactionType, amount, details, toFrom;
        ImageView imageType;
    }
}
