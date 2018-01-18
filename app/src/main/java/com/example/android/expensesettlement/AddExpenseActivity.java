package com.example.android.expensesettlement;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.expensesettlement.data.TripContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.SECOND;

/**
 * Created by ming1 on 3/16/2017.
 */

public class AddExpenseActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private Uri mCurrentUri;

    private long mTripID;

    private final int PERSON_LOADER = 0;

    private EditText mNameEditText;
    private EditText mCostEditText;
    private EditText mDateEditText;

    private DatePickerDialog mDatePickerDialog;
    private SimpleDateFormat mDateFormatter;
    private Date mDateChosen;

    private Spinner mTypeSpinner;
    private int mType = -1;

    private Spinner mCreditorSpinner;
    private CreditorSpinnerCursorAdapter mCreditorSpinnerAdapter;
    private long mCreditor = -1;

    private GridView mDebtorsGridView;
    private DebtorGridListCursorAdapter mDebtorAdapter;
    private Set<Long> mDebtorSet = new HashSet<>();

    /** Boolean flag that keeps track of whether the pet has been edited (true) or not (false) */
    private boolean mExpenseHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mExpenseHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        Intent intent = getIntent();
        mCurrentUri = intent.getData();
        mTripID = ContentUris.parseId(mCurrentUri);

        setTitle("Add an Expense");

        getLoaderManager().initLoader(PERSON_LOADER, null, this);

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.add_expense_name);
        mCostEditText = (EditText) findViewById(R.id.add_cost);
        mDateEditText = (EditText) findViewById(R.id.add_date);

        mDateFormatter = new SimpleDateFormat("MM-dd-yyyy");

        mTypeSpinner = (Spinner) findViewById(R.id.spinner_type);
        mCreditorSpinner = (Spinner) findViewById(R.id.spinner_creditor);

        mDebtorsGridView = (GridView) findViewById(R.id.grid_view_debtors);

        mDebtorAdapter = new DebtorGridListCursorAdapter(this, null);
        mDebtorsGridView.setAdapter(mDebtorAdapter);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        mNameEditText.setOnTouchListener(mTouchListener);
        mCostEditText.setOnTouchListener(mTouchListener);
        mDateEditText.setOnTouchListener(mTouchListener);
        mTypeSpinner.setOnTouchListener(mTouchListener);
        mCreditorSpinner.setOnTouchListener(mTouchListener);
        mDebtorsGridView.setOnTouchListener(mTouchListener);

        Calendar newCalendar = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, month, dayOfMonth);

                newDate.set(HOUR_OF_DAY, 0);
                newDate.set(MINUTE, 0);
                newDate.set(SECOND, 0);
                newDate.set(MILLISECOND, 0);

                mDateChosen = newDate.getTime();
                mDateEditText.setText(mDateFormatter.format(mDateChosen));
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        mDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDatePickerDialog.show();
            }
        });

        setupCreditorSpinner();
        setupTypeSpinner();
    }

    /**
     * Setup the dropdown type spinner
     */
    private void setupTypeSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        //ArrayAdapter typeSpinnerAdapter = ArrayAdapter.createFromResource(this,
                //R.array.array_type_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        //typeSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        String[] types = { "Hotel", "Shopping", "Transportation",
                "Entertainment", "Food", "Other" };

        TypeSpinnerAdapter typeSpinnerAdapter = new TypeSpinnerAdapter(this, R.layout.spinner_item,
                types);

        // Apply the adapter to the spinner
        mTypeSpinner.setAdapter(typeSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.type_hotel))) {
                        mType = TripContract.ExpenseEntry.TYPE_HOTEL;
                    } else if (selection.equals(getString(R.string.type_shopping))) {
                        mType = TripContract.ExpenseEntry.TYPE_SHOPPING;
                    } else if (selection.equals(getString(R.string.type_transportation))) {
                        mType = TripContract.ExpenseEntry.TYPE_TRANSPORTATION;
                    } else if (selection.equals(getString(R.string.type_entertainment))) {
                        mType = TripContract.ExpenseEntry.TYPE_ENTERTAINMENT;
                    } else if (selection.equals(getString(R.string.type_food))) {
                        mType = TripContract.ExpenseEntry.TYPE_FOOD;
                    } else {
                        mType = TripContract.ExpenseEntry.TYPE_OTHER;
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mType = TripContract.ExpenseEntry.TYPE_OTHER;
            }
        });
    }

    /**
     * Setup the dropdown creditor spinner
     */
    private void setupCreditorSpinner() {
        mCreditorSpinnerAdapter = new CreditorSpinnerCursorAdapter(this, null);

        // Specify dropdown layout style - simple list view with 1 item per line
        //creditorSpinnerAdapter.setDropDownViewTheme();

        // Apply the adapter to the spinner
        mCreditorSpinner.setAdapter(mCreditorSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mCreditorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //String creditorName = (String) parent.getItemAtPosition(position);
                Cursor result = (Cursor) parent.getItemAtPosition(position);
                mCreditor = result.getLong(result.getColumnIndex(TripContract.PersonEntry._ID));
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mCreditor = -1;
            }
        });
    }

    /**
     * Get user input from editor and save expense into database.
     */
    private void saveExpense() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mNameEditText.getText().toString().trim();
        String dateString = mDateEditText.getText().toString().trim();
        String costString = mCostEditText.getText().toString().trim();

        mDebtorSet.addAll(mDebtorAdapter.getCheckedPersonIDSet());

        int numOfDeb = 0;
        if (mDebtorSet.size() > 0) {
            numOfDeb = mDebtorSet.size();
        }

        // Check if any field is blank
        if (numOfDeb == 0) {
            Toast.makeText(this, "Data not saved, debtors should be set",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Data not saved, name should be filled",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(dateString)) {
            Toast.makeText(this, "Data not saved, date should be filled",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(costString)) {
            Toast.makeText(this, "Data not saved, cost should be filled",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mType == -1) {
            Toast.makeText(this, "Data not saved, type should be set",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        if (mCreditor == -1) {
            Toast.makeText(this, "Data not saved, creditor should be set",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int cost = (int)(Float.valueOf(costString) * 100);
        long dateMiliSec = mDateChosen.getTime();

        // Create a ContentValues object for Expense table
        ContentValues values = new ContentValues();
        values.put(TripContract.ExpenseEntry.COLUMN_EXPENSE_NAME, nameString);
        values.put(TripContract.ExpenseEntry.COLUMN_EXPENSE_VALUE, cost);
        values.put(TripContract.ExpenseEntry.COLUMN_EXPENSE_DATE, dateMiliSec);
        values.put(TripContract.ExpenseEntry.COLUMN_EXPENSE_TYPE, mType);
        values.put(TripContract.ExpenseEntry.COLUMN_NUM_OF_DEBTORS, numOfDeb);

        Uri newExpenseUri = getContentResolver().insert(TripContract.ExpenseEntry.CONTENT_URI, values);

        long expenseID = ContentUris.parseId(newExpenseUri);

        // Insert the trip-expense pair into the TripExpense Table
        values.clear();
        values.put(TripContract.TripExpenseEntry.COLUMN_TRIP_ID, mTripID);
        values.put(TripContract.TripExpenseEntry.COLUMN_EXPENSE_ID, expenseID);
        Uri insertUri = getContentResolver().insert(TripContract.TripExpenseEntry.CONTENT_URI, values);

        Uri uri = TripContract.ExpensePersonEntry.CONTENT_URI;

        // Insert into expense-person table
        for (long i: mDebtorSet) {
            values.clear();
            values.put(TripContract.ExpensePersonEntry.COLUMN_EXPENSE_ID, (int)expenseID);
            values.put(TripContract.ExpensePersonEntry.COLUMN_CREDITOR_ID, (int)mCreditor);
            values.put(TripContract.ExpensePersonEntry.COLUMN_DEBTOR_ID, (int)i);
            insertUri = getContentResolver().insert(uri, values);
        }


        // Show a toast message depending on whether or not the update was successful.
        if (insertUri == null) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, "Error with inserting", Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, "Inserted", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add_expense, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_save_expense:
                saveExpense();
                finish();
                return true;
            case R.id.action_cancel_edit_expense:
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mExpenseHasChanged) {
                    finish();
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                finish();
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mExpenseHasChanged) {
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
        Uri queryUri = Uri.withAppendedPath(mCurrentUri, TripContract.PATH_PERSONS);

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

        mCreditorSpinnerAdapter.swapCursor(cursor);
        mDebtorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data from the input fields.
        mNameEditText.setText("");
        mCostEditText.setText("");
        mDateEditText.setText("");
        mCreditorSpinnerAdapter.swapCursor(null);
        mDebtorAdapter.swapCursor(null);
        //mTypeSpinner.setSelection(5);
        //mCreditorSpinner.setSelection(0);
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
}
