package com.example.android.expensesettlement;

/**
 * Created by ming1 on 3/8/2017.
 */

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import com.example.android.expensesettlement.data.TripContract;



/**
 * Displays list of expenses depending on the uri
 */
public class ExpenseDisplayActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mUri;
    /** Adapter for the ListView */
    private ExpenseCursorAdapter mCursorAdapter;

    private long mTripID;

    // If is, able to edit the person name or delete the person which will also delete all related expenses
    private boolean mIsFromPerson = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_display);

        Intent intent = getIntent();
        mUri = intent.getData();
        List<String> segments = mUri.getPathSegments();
        mTripID = Long.parseLong(segments.get(1));

        if (segments.size() > 2 && TripContract.PATH_PERSONS.equals(segments.get(2))) {
            mIsFromPerson = true;
        }

        ListView expenseListView = (ListView) findViewById(R.id.list);

        View emptyView = findViewById(R.id.empty_text);
        expenseListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of pet data in the Cursor.
        // There is no pet data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new ExpenseCursorAdapter(this, null);
        expenseListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        expenseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(ExpenseDisplayActivity.this, ExpenseDetailsActivity.class);

                Uri currentExpenseUri = ContentUris.withAppendedId(TripContract.TripEntry.CONTENT_URI,
                        mTripID);
                currentExpenseUri = Uri.withAppendedPath(currentExpenseUri, TripContract.PATH_EXPENSES);

                currentExpenseUri = ContentUris.withAppendedId(currentExpenseUri, id);

                intent.setData(currentExpenseUri);

                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(0, null, this);

        invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_expense_display_person, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        MenuItem menuEdit = menu.findItem(R.id.action_edit_person);
        MenuItem menuDelete = menu.findItem(R.id.action_delete_person);

        if (!mIsFromPerson) {
            menuEdit.setVisible(false);
            menuDelete.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_edit_person:
                // Call the person edit activity
                Intent intent = new Intent(ExpenseDisplayActivity.this, PersonEditorActivity.class);

                intent.setData(mUri);

                startActivity(intent);
                return true;
            case R.id.action_delete_person:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePerson();
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

    private void deletePerson(){
        // Call the ContentResolver to delete the pet at the given content URI.
        // Pass in null for the selection and selection args because the mCurrentPetUri
        // content URI already identifies the pet that we want.
        int rowsDeleted = getContentResolver().delete(mUri, null, null);

        // Show a toast message depending on whether or not the delete was successful.
        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, "Delete failed, person should not be involved in any expense",
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, "Delete successful", Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mUri,   // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}
