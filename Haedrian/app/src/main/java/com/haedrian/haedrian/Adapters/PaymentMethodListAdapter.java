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

import java.util.ArrayList;

/**
 * Created by Logan on 5/18/2015.
 */
public class PaymentMethodListAdapter extends ArrayAdapter<String> {

    private ArrayList<String> paymentMethods;
    private Context context;
    private int resource;

    public PaymentMethodListAdapter(Context context, int resource, ArrayList<String> paymentMethods) {
        super(context, resource, paymentMethods);

        this.paymentMethods = paymentMethods;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        PaymentMethodRowHolder holder = null;


        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(resource, parent, false);
            holder = new PaymentMethodRowHolder();
            holder.paymentMethod = (TextView) row.findViewById(R.id.method_type);
            holder.fee = (TextView) row.findViewById(R.id.fee);

            row.setTag(holder);
        }
        else {
            holder = (PaymentMethodRowHolder)row.getTag();
        }

        Log.v("TEST", paymentMethods.get(position));

        holder.paymentMethod.setText(paymentMethods.get(position));
        holder.fee.setText("No fee");

        return row;
    }

    static class PaymentMethodRowHolder {
        TextView paymentMethod, fee;
    }
}
