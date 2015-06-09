package com.haedrian.haedrian.Adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.haedrian.haedrian.Models.BuyOrderHistoryModel;
import com.haedrian.haedrian.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Logan on 6/8/2015.
 */
public class BuyOrderListAdapter extends ArrayAdapter {

    private Context context;
    private int resource;
    private ArrayList<BuyOrderHistoryModel> buyOrders;


    public BuyOrderListAdapter(Context context, int resource, ArrayList<BuyOrderHistoryModel> buyOrders) {
        super(context, resource, buyOrders);

        this.context = context;
        this.resource = resource;
        this.buyOrders = buyOrders;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        BuyOrderDataHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new BuyOrderDataHolder();
            holder.buyOrderType = (TextView) row.findViewById(R.id.buyorder_type_textview);
            holder.buyOrderAmount = (TextView) row.findViewById(R.id.buyorder_amount_textview);
            holder.buyOrderStatus = (TextView) row.findViewById(R.id.buyorder_status_textview);

            row.setTag(holder);
        }
        else {
            holder = (BuyOrderDataHolder)row.getTag();
        }

        holder.buyOrderType.setText(context.getString(R.string.buyorder_buy));

        Double currencyAmount = Double.parseDouble(buyOrders.get(position).getCurrencyAmount());
        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.getDefault());

        holder.buyOrderAmount.setText(currencyFormatter.format(currencyAmount));

        String buyOrderStatusTemp = buyOrders.get(position).getStatus();
        String buyOrderStatus = "";

        String[] parts = buyOrderStatusTemp.split("_");
        for (int i = 0; i < parts.length; i++) {
            buyOrderStatus += parts[i].toUpperCase() + " ";
        }
        holder.buyOrderStatus.setText(buyOrderStatus);


        return row;
    }

    static class BuyOrderDataHolder {
        private TextView buyOrderType, buyOrderAmount, buyOrderStatus;
    }

}
