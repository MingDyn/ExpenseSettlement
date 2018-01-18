package com.example.android.expensesettlement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.expensesettlement.data.TripContract;

/**
 * Adapter for the type spinner
 */

public class CreditorSpinnerCursorAdapter extends CursorAdapter {
    public CreditorSpinnerCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a spinner item view
        //return LayoutInflater.from(context).inflate(android.R.layout.simple_spinner_item, parent, false);

        return LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //TextView nameTextView = (TextView) view.findViewById(android.R.id.text1)
        // ;
        TextView nameTextView = (TextView) view.findViewById(R.id.name);

        int nameColumnIndex = cursor.getColumnIndex(TripContract.PersonEntry.COLUMN_PERSON_NAME);
        String personName = cursor.getString(nameColumnIndex);

        nameTextView.setText(personName);
    }
}
