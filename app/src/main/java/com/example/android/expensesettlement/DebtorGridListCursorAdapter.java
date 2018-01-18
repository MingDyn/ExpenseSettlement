package com.example.android.expensesettlement;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.expensesettlement.data.TripContract;

import java.util.HashSet;
import java.util.Set;

/**
 * {@link TripCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of trip data as its data source. This adapter knows
 * how to create list items for each row of trip data in the {@link Cursor}.
 */

public class DebtorGridListCursorAdapter extends CursorAdapter {

    private Set<Long> checkedPersonIDSet = new HashSet<>();
    private boolean editable = true;

    public DebtorGridListCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a grid list item
        return LayoutInflater.from(context).inflate(R.layout.gridlist_debtors_checkbox, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.debtor_name);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.debtor_check_box);

        int nameColumnIndex = cursor.getColumnIndex(TripContract.PersonEntry.COLUMN_PERSON_NAME);
        String personName = cursor.getString(nameColumnIndex);
        nameTextView.setText(personName);

        int idColumnIndex = cursor.getColumnIndex(TripContract.PersonEntry._ID);
        final long personId = cursor.getLong(idColumnIndex);

        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((CheckBox) v).isChecked()) {
                    checkedPersonIDSet.add(personId);
                } else {
                    checkedPersonIDSet.remove(personId);
                }
            }
        });

        if (checkedPersonIDSet.contains(personId)) {
            checkBox.setChecked(true);
        } else {
            checkBox.setChecked(false);
        }

        if (editable) {
            checkBox.setEnabled(true);
        } else {
            checkBox.setEnabled(false);
        }
    }

    public Set<Long> getCheckedPersonIDSet() {
        return checkedPersonIDSet;
    }

    public void setCheckedPersonIDSet(Set<Long> debtorSet) {
        checkedPersonIDSet.clear();
        checkedPersonIDSet.addAll(debtorSet);
    }

    public void setEditable(boolean b) {
        editable = b;
    }
}
