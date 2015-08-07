package com.haedrian.curo.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haedrian.curo.Models.CurrencyModel;
import com.haedrian.curo.R;

import java.util.List;

/**
 * Created by audakel on 3/22/15.
 */


public class CurrencyAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CurrencyModel> currencies;
    private Resources resources;


    public CurrencyAdapter(Activity activity, List<CurrencyModel> currencies) {
        this.activity = activity;
        this.currencies = currencies;

        resources = activity.getResources();
    }

    @Override
    public int getCount() {
        return currencies.size();
    }

    @Override
    public Object getItem(int location) {
        return currencies.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.view_currency_list, null);

        TextView name = (TextView) convertView.findViewById(R.id.currencyNameText);
        TextView buy = (TextView) convertView.findViewById(R.id.currencyBuyText);
        TextView sell = (TextView) convertView.findViewById(R.id.currencySellText);
        TextView symbol = (TextView) convertView.findViewById(R.id.currencySymbolText);

        // getting  data for the row
        CurrencyModel currencyModel = currencies.get(position);


        // title
        name.setText(currencyModel.getCurrencyName());

        // rating
        buy.setText(resources.getString(R.string.buy)+ " " + String.valueOf(currencyModel.getBuyRate()));

        // sell
        sell.setText(resources.getString(R.string.sell) + " " + currencyModel.getSellRate());

        // release year
        symbol.setText(currencyModel.getSymbol());

        return convertView;
    }

}
