package com.example.android.expensesettlement;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ming1 on 3/23/2017.
 */

public class TypeSpinnerAdapter extends ArrayAdapter{
    private String[] types;

    private Context context;

    public TypeSpinnerAdapter( Context context, int resource, String[] types) {
        super(context, resource, 0, types);
        this.context = context;
        this.types = types;
    }

    @Override
    public View getDropDownView(int position, View cnvtView, ViewGroup prnt) {
        return getCustomView(position, cnvtView, prnt);
    }

    @Override
    public View getView(int pos, View cnvtView, ViewGroup prnt) {
        return getCustomView(pos, cnvtView, prnt);
    }

    private View getCustomView(int pos, View cnvtView, ViewGroup prnt) {
        View mySpinner = LayoutInflater.from(context).inflate(R.layout.spinner_item, prnt, false);

        TextView text = (TextView) mySpinner.findViewById(R.id.name);
        text.setText(types[pos]);

        return mySpinner;
    }
}
