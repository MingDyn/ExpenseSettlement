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
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.expensesettlement.data.TripContract;

import java.util.List;

/**
 * To change an existing person name or add a new person
 */

public class PersonEditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentUri;

    private int mTripID;

    private EditText mNameEditText;

    private boolean mNameHasChanged = false;

    private boolean mIsNewPerson = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mNameHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_editor);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        List<String> segments = mCurrentUri.getPathSegments();
        mTripID = Integer.valueOf(segments.get(1));

        if (segments.size() < 3) {
            // New person to add: uri as trips/#
            setTitle("Add a Person");
            mIsNewPerson = true;
        } else {
            // Change an existing one
            setTitle("Change person name");
            getLoaderManager().initLoader(0, null, this);
        }

        mNameEditText = (EditText) findViewById(R.id.edit_person_name);

        mNameEditText.setOnTouchListener(mTouchListener);
    }

    private void savePerson() {
        String nameString = mNameEditText.getText().toString().trim();

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "No name provided",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(TripContract.PersonEntry.COLUMN_PERSON_NAME, nameString);

        if (mIsNewPerson) {
            Uri newUri = getContentResolver().insert(TripContract.PersonEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, "Error with inserting person",
                        Toast.LENGTH_SHORT).show();
            } else {
                Uri tripPersonUri = Uri.withAppendedPath(mCurrentUri, TripContract.PATH_PERSONS);
                long personId = ContentUris.parseId(newUri);
                tripPersonUri = ContentUris.withAppendedId(tripPersonUri,personId);

                values.clear();
                values.put(TripContract.TripPersonEntry.COLUMN_TRIP_ID, mTripID);
                values.put(TripContract.TripPersonEntry.COLUMN_PERSON_ID, (int)personId);

                getContentResolver().insert(tripPersonUri, values);

                Toast.makeText(this, "Person inserted",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Only need to update the person table, no need to update trip_person table
            Uri personUri = ContentUris.withAppendedId(TripContract.PersonEntry.CONTENT_URI,
                    ContentUris.parseId(mCurrentUri));
            int rowsAffected = getContentResolver().update(personUri, values, null, null);

            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, "Error with updating person",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, "Person updated",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_person_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save_person:
                // Save pet to database
                savePerson();
                // Exit activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_cancel_edit_person:
                // Pop up confirmation dialog for deletion
                if (!mNameHasChanged) {
                    //NavUtils.navigateUpFromSameTask(PersonEditorActivity.this); TODO: will lead to Uri.buildUpon on a null object reference!
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener1 =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, close the current activity.
                                finish();
                            }
                        };

                // Show dialog that there are unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener1);
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mNameHasChanged) {
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener2 =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mNameHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                TripContract.PersonEntry.COLUMN_PERSON_NAME
        };

        long personId = ContentUris.parseId(mCurrentUri);
        Uri personUri = ContentUris.withAppendedId(TripContract.PersonEntry.CONTENT_URI, personId);

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                personUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
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

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(TripContract.PersonEntry.COLUMN_PERSON_NAME);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);

            // Update the views on the screen with the values from the database
            mNameEditText.setText(name);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
    }
}
