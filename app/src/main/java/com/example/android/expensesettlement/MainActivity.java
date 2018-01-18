package com.example.android.expensesettlement;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.expensesettlement.data.TripContract.TripEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    // Identifier for the trip data loader
    private static final int TRIP_LOADER = 0;

    // ListView for the all trips
    ListView tripListView;

    // Adapter for the ListView
    TripCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, TripEditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the trip data
        tripListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        tripListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of trip data in the Cursor.
        // There is no trip data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new TripCursorAdapter(this, null);
        tripListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        tripListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link CatalogActivity}
                Intent intent = new Intent(MainActivity.this, CatalogActivity.class);

                // Form the content URI that represents the specific trip that was clicked on,
                // by appending the "id" (passed as input to this method) onto the
                // {@link TripEntry#CONTENT_URI}.
                Uri currentTripUri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, id);

                // Set the URI on the data field of the intent
                intent.setData(currentTripUri);

                // Launch the {@link CatalogActivity} to display the data for the current trip.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(TRIP_LOADER, null, this);
    }

    // Helper method to insert hardcoded trip data into the database. For debugging purposes only.
    private void insertTrip() {
        // Create a ContentValues object
        ContentValues values = new ContentValues();
        values.put(TripEntry.COLUMN_TRIP_NAME, "Big Lake");

        Uri newUri = getContentResolver().insert(TripEntry.CONTENT_URI, values);
    }

    // Helper method to delete all trips in the database.
    private void deleteAllTrips() {
        int rowsDeleted = getContentResolver().delete(TripEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from pet database");

        Toast.makeText(this, getString(R.string.delete_successful),
                Toast.LENGTH_SHORT).show();
    }

    // Check if there is no trip. Toast if there is no trip.
    private void checkEmptyDb() {
        if ( tripListView.getCount() == 0 ) {
            Toast.makeText(MainActivity.this, "No trip to delete",
                    Toast.LENGTH_SHORT).show();
        } else {
            showDeleteConfirmationDialog();
        }
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_all_trips);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteAllTrips();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // Menu is just for debugging
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_main.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertTrip();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                checkEmptyDb();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                TripEntry._ID,
                TripEntry.COLUMN_TRIP_NAME };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                TripEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link TripCursorAdapter} with this new cursor containing updated trip data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
