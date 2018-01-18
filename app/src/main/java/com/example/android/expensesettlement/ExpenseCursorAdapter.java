package com.example.android.expensesettlement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.expensesettlement.data.TripContract;
import com.example.android.expensesettlement.data.TripContract.TripEntry;

/**
 * {Create list items the expense ID.
 */

public class ExpenseCursorAdapter extends CursorAdapter{
    public ExpenseCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.name);

        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        summaryTextView.setVisibility(View.GONE);

        // Summary: price (divide by 100), date, type, creditor

        int nameColumnIndex = cursor.getColumnIndex(TripContract.ExpenseEntry.COLUMN_EXPENSE_NAME);
        String expenseName = cursor.getString(nameColumnIndex);

        nameTextView.setText(expenseName);
    }
}
