package com.example.android.expensesettlement;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.expensesettlement.data.TripContract;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Created by ming1 on 3/11/2017.
 */

public class CalculateActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Map with the person ID as its key and person name as its value
    private Map<Integer, String> mTripPerson;

    // Current trip Uri
    private Uri mCurrentTrip;

    // Current trip id
    private int mTripId;

    // A treeset to compare its object items
    private TreeSet<Pair<Integer, Integer>> mBalanceSet;

    // A list of results
    private List<String> mListResults;

    // Loader
    private final int PERSON_LOADER = 0;
    private final int BALANCE_LOADER = 1;

    // Adpater for list view
    private ArrayAdapter<String> mArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculation);

        // Get the trip ID
        Intent intent = getIntent();
        mCurrentTrip = intent.getData();
        mTripId = (int)ContentUris.parseId(mCurrentTrip);

        // Change the title
        setTitle("Settlement Results");

        // ListView setup
        ListView settlementListView = (ListView) findViewById(R.id.list_settlement);

        // Initialization
        mListResults = new ArrayList<String>();
        mTripPerson = new HashMap<Integer, String>();

        // Setup the adapter
        mArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mListResults);
        settlementListView.setAdapter(mArrayAdapter);

        // Setup the balance treeset
        mBalanceSet = new TreeSet<Pair<Integer, Integer>>(new Comparator<Pair<Integer, Integer>>(){
            public int compare(Pair a, Pair b) {
                if (a.second.equals(b.second))
                    return (int)a.first - (int)b.first;
                else
                    return (int)a.second - (int)b.second;
            }
        });

        // Initialize the loader
        getLoaderManager().initLoader(PERSON_LOADER, null, this);
        //getLoaderManager().initLoader(BALANCE_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri queryUri = null;

        switch (i) {
            case PERSON_LOADER:
                queryUri = Uri.withAppendedPath(mCurrentTrip, TripContract.PATH_PERSONS);
                break;
            case BALANCE_LOADER:
                queryUri = Uri.withAppendedPath(mCurrentTrip, TripContract.PATH_BALANCE);
                break;
        }

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                queryUri,         // Query the content URI for the current pet
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        int id = loader.getId();
        switch (id) {
            case PERSON_LOADER:
                if (cursor.moveToFirst()) {
                    do {
                        String personName = cursor.getString(cursor.getColumnIndex(TripContract.PersonEntry.COLUMN_PERSON_NAME));
                        int personId = cursor.getInt(cursor.getColumnIndex("_id"));

                        //Toast.makeText(this, "save person id " + personId + ", person name " + personName, Toast.LENGTH_SHORT).show();

                        mTripPerson.put(personId, personName);
                    } while (cursor.moveToNext());
                }

                // After the mTripPerson Map is set, initialize the balance loader
                getLoaderManager().initLoader(BALANCE_LOADER, null, this);
                break;
            case BALANCE_LOADER:
                if (cursor.moveToFirst()) {
                    do {
                        int personId = cursor.getInt(cursor.getColumnIndex(TripContract.BalanceEntry.PERSON_ID));
                        int balance = cursor.getInt(cursor.getColumnIndex(TripContract.BalanceEntry.BALANCE_VALUE));
                        Pair<Integer, Integer> pair = new Pair<>(personId, balance);
                        mBalanceSet.add(pair);
                    } while (cursor.moveToNext());
                }
                settleExpenses();
                mArrayAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mArrayAdapter.clear();
    }

    private void settleExpenses() {
        while(!mBalanceSet.isEmpty()) {
            Pair<Integer, Integer> min = mBalanceSet.pollFirst();
            Pair<Integer, Integer> max = mBalanceSet.pollLast();
            if ( max == null) {
                // Only one row: having value 0
                return;
            }

            int trans = min.second + max.second;

            //Toast.makeText(this, "trip_person size " + mTripPerson.size(), Toast.LENGTH_SHORT).show();

            // print one transition
            if (trans > 0) {
                Pair<Integer, Integer> newPair = new Pair<>(max.first, trans);
                mBalanceSet.add(newPair);
                float transition = -((float)min.second)/100;
                String result = mTripPerson.get(min.first) + " should pay "
                        + mTripPerson.get(max.first) + " " + transition;
                mListResults.add(result);
            } else if (trans < 0){
                Pair<Integer, Integer> newPair = new Pair<>(min.first, trans);
                mBalanceSet.add(newPair);
                float transition = ((float)max.second)/100;
                String result = mTripPerson.get(min.first) + " should pay "
                        + mTripPerson.get(max.first) + " " + transition;
                mListResults.add(result);
            } else {
                float transition = ((float)max.second)/100;
                String result = mTripPerson.get(min.first) + " should pay "
                        + mTripPerson.get(max.first) + " " + transition;
                mListResults.add(result);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
