package com.haedrian.haedrian.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.haedrian.haedrian.R;


/**
 * Created by Logan on 3/12/2015.
 */
public class CreatedProjectsListAdapter extends BaseAdapter {


    private static LayoutInflater inflater = null;

    public CreatedProjectsListAdapter(Context context) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null)
            view = inflater.inflate(R.layout.created_projects_row, parent, false);

        return view;
    }
}
