package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
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
import java.util.TreeSet;

/**
 * Created by Logan on 4/25/2015.
 */
public class TransactionListAdapter extends BaseAdapter {

    public static final int TYPE_ITEM = 0;
    public static final int TYPE_SEPARATOR = 1;

    private Context context;
    private ArrayList<TransactionModel> transactions = new ArrayList<>();

    private LayoutInflater inflater;
    private TreeSet<Integer> sectionHeader = new TreeSet<>();

    public TransactionListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
    }

    public void addTransaction(final TransactionModel transaction) {
        transactions.add(transaction);
        notifyDataSetChanged();
    }

    public void addSectionHeader(final TransactionModel transaction) {
        transactions.add(transaction);
        sectionHeader.add(transactions.size() - 1);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return sectionHeader.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionDataHolder holder = null;
        int rowType = getItemViewType(position);


        if (row == null) {
            holder = new TransactionDataHolder();
            switch (rowType) {
                case TYPE_ITEM:
                    row = inflater.inflate(R.layout.row_transaction, null);
                    holder.transactionType = (TextView) row.findViewById(R.id.transaction_type_textview);
                    holder.amount = (TextView) row.findViewById(R.id.amount_textview);
                    holder.toFrom = (TextView) row.findViewById(R.id.to_string);
                    holder.details = (TextView) row.findViewById(R.id.transaction_details_textview);
                    holder.imageType = (ImageView) row.findViewById(R.id.send_request_icon);
                    break;
                case TYPE_SEPARATOR:
                    row = inflater.inflate(R.layout.row_section_header, null);
                    holder.date = (TextView) row.findViewById(R.id.header_title);
                    break;
            }
            row.setTag(holder);
        }
        else {
            holder = (TransactionDataHolder)row.getTag();
        }

        if (rowType == TYPE_ITEM) {
            String transactionType = transactions.get(position).getEntryType();

            if (transactionType.equals("outgoing")) {
                holder.transactionType.setText(context.getResources().getString(R.string.outgoing_funds));
                holder.imageType.setImageResource(R.drawable.money);
                holder.toFrom.setText(context.getResources().getString(R.string.to));
                holder.details.setText(transactions.get(position).getTarget());
                // Outgoing text color
                holder.transactionType.setTextColor(context.getResources().getColor(R.color.primary));
                holder.amount.setTextColor(context.getResources().getColor(R.color.primary));



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

                // Incoming text color
                holder.transactionType.setTextColor(context.getResources().getColor(R.color.grey));
                holder.amount.setTextColor(context.getResources().getColor(R.color.grey));

            }
            holder.amount.setText(transactions.get(position).getAmount());
        }
        else if (rowType == TYPE_SEPARATOR) {
            holder.date.setText(transactions.get(position).getDate());
        }

        return row;
    }

    @Override
    public int getCount() {
        return transactions.size();
    }

    @Override
    public TransactionModel getItem(int position) {
        return transactions.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    public int getPosition(int position) {
        return position - sectionHeader.headSet(position).size();
    }

    static class TransactionDataHolder {
        TextView transactionType, amount, details, toFrom, date;
        ImageView imageType;
    }
}
