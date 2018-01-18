package com.example.android.expensesettlement.data;

/**
 * Created by ming1 on 2/28/2017.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.expensesettlement.data.TripContract.TripEntry;
import com.example.android.expensesettlement.data.TripContract.PersonEntry;
import com.example.android.expensesettlement.data.TripContract.ExpenseEntry;
import com.example.android.expensesettlement.data.TripContract.TripPersonEntry;
import com.example.android.expensesettlement.data.TripContract.TripExpenseEntry;
import com.example.android.expensesettlement.data.TripContract.ExpensePersonEntry;
import com.example.android.expensesettlement.data.TripContract.SummaryEntry;
import com.example.android.expensesettlement.data.TripContract.CreditorEntry;
import com.example.android.expensesettlement.data.TripContract.DebtorEntry;
import com.example.android.expensesettlement.data.TripContract.BalanceEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link ContentProvider} for expensesettlement app.
 */
public class TripProvider extends ContentProvider {

    /** Tag for the log messages */
    public static final String LOG_TAG = TripProvider.class.getSimpleName();

    /** URI matcher code for the content URI for the trips table */
    private static final int TRIPS = 100;

    private static final int TRIP_PERSONS = 101;
    private static final int TRIP_EXPENSES = 102;
    private static final int TRIP_CREDITORS = 103;
    private static final int TRIP_DEBTORS = 104;
    private static final int TRIP_BALANCES = 105;

    private static final int PERSONS = 106;
    private static final int EXPENSES = 107;

    private static final int TRIP_EXPENSE = 108;
    private static final int EXPENSE_PERSON = 109;

    /** URI matcher code for the content URI for a single trip in the trips table */
    private static final int TRIP_ID = 110;

    private static final int TRIP_PERSON_ID = 111;
    private static final int TRIP_EXPENSE_ID = 112;
    private static final int TRIP_CREDITOR_ID = 113;
    private static final int TRIP_DEBTOR_ID = 114;
    private static final int TRIP_BALANCE_ID = 115;

    private static final int PERSON_ID = 116;
    private static final int EXPENSE_ID = 117;

    private static final int EXPENSE_PERSON_ID = 118;

    private static final int TRIP_DATE_EXPENSES = 120;
    private static final int TRIP_TYPE_EXPENSES = 121;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form "content://com.example.android.expensesettlement/trips" will
        // map to the integer code {@link #TRIPS}. This URI is used to provide access to MULTIPLE
        // rows of the trips table.
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS, TRIPS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_PERSONS, PERSONS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_EXPENSES, EXPENSES);

        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_PERSONS, TRIP_PERSONS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_EXPENSES, TRIP_EXPENSES);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_CREDITOR, TRIP_CREDITORS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_DEBTOR, TRIP_DEBTORS);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_BALANCE, TRIP_BALANCES);

        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIP_EXPENSE, TRIP_EXPENSE);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_EXPENSE_PERSON, EXPENSE_PERSON);

        // The content URI of the form "content://com.example.android.expensesettlement/trips/#"
        // will map to the integer code {@link #TRIP_ID}. This URI is used to provide access to
        // ONE single row of the pets table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.expensesettlement/trips/3" matches, but
        // "content://com.example.android.expensesettlement/trips" (without a number at the end)
        // doesn't match.
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#",
                TRIP_ID);

        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_PERSONS + "/#", TRIP_PERSON_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_EXPENSES + "/#", TRIP_EXPENSE_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_CREDITOR + "/#", TRIP_CREDITOR_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_DEBTOR + "/#", TRIP_DEBTOR_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_BALANCE + "/#", TRIP_BALANCE_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_PERSONS + "/#", PERSON_ID);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_EXPENSES + "/#", EXPENSE_ID);

        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_EXPENSE_PERSON
                + "/#", EXPENSE_PERSON_ID);

        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_EXPENSE_DATE + "/#", TRIP_DATE_EXPENSES);
        sUriMatcher.addURI(TripContract.CONTENT_AUTHORITY, TripContract.PATH_TRIPS + "/#/"
                + TripContract.PATH_EXPENSE_TYPE + "/#", TRIP_TYPE_EXPENSES);
    }

    /** Database helper object */
    private TripDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new TripDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // For uri.getPathSegments
        List<String> segments;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                // For the TRIPS code, query the trips table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the trips table.
                cursor = database.query(TripEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_ID:
                // For the TRIP_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.expensesettlement/trips/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the trips table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(TripEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_PERSONS:
                segments = uri.getPathSegments();
                String queryTripPersons = "SELECT " +  TripPersonEntry.TABLE_NAME + "."
                        + TripPersonEntry.COLUMN_PERSON_ID + " AS _id, " + PersonEntry.TABLE_NAME + "."
                        + PersonEntry.COLUMN_PERSON_NAME + " FROM " + TripPersonEntry.TABLE_NAME
                        + ", " + PersonEntry.TABLE_NAME + " WHERE " + TripPersonEntry.TABLE_NAME
                        + "." + TripPersonEntry.COLUMN_PERSON_ID + " = " + PersonEntry.TABLE_NAME
                        + "." + PersonEntry._ID + " AND " + TripPersonEntry.TABLE_NAME + "."
                        + TripPersonEntry.COLUMN_TRIP_ID + " = " + segments.get(1);

                cursor = database.rawQuery(queryTripPersons, null);
                break;
            case TRIP_EXPENSES:
                segments = uri.getPathSegments();
                String queryTripExpenses = "SELECT " +  TripExpenseEntry.TABLE_NAME + "."
                        + TripExpenseEntry.COLUMN_EXPENSE_ID + " AS _id, " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_NAME + "," + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_VALUE + "," + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_DATE + "," + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_TYPE + " FROM " + TripExpenseEntry.TABLE_NAME
                        + ", " + ExpenseEntry.TABLE_NAME + " WHERE " + TripExpenseEntry.TABLE_NAME
                        + "." + TripExpenseEntry.COLUMN_EXPENSE_ID + " = " + ExpenseEntry.TABLE_NAME
                        + "." + ExpenseEntry._ID + " AND " + TripExpenseEntry.TABLE_NAME + "."
                        + TripExpenseEntry.COLUMN_TRIP_ID + " = " + segments.get(1);

                cursor = database.rawQuery(queryTripExpenses, null);
                break;
            case TRIP_EXPENSE_ID:
                selection = ExpensePersonEntry.COLUMN_EXPENSE_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // Return creditor ID and all debtor IDs
                cursor = database.query(ExpensePersonEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_CREDITORS:
                selection = CreditorEntry.TRIP_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Return all creditor IDs
                cursor = database.query(CreditorEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_CREDITOR_ID:
                selection = CreditorEntry.TRIP_ID + "=? AND " + CreditorEntry.CREDITOR_ID + "=?";
                segments = uri.getPathSegments();
                selectionArgs = new String[] { segments.get(1), segments.get(3) };

                // Return all creditor values
                cursor = database.query(CreditorEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_DEBTORS:
                selection = DebtorEntry.TRIP_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Return all debtor IDs
                cursor = database.query(DebtorEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_DEBTOR_ID:
                selection = DebtorEntry.TRIP_ID + "=? AND " + DebtorEntry.DEBTOR_ID + "=?";
                segments = uri.getPathSegments();
                selectionArgs = new String[] { segments.get(1), segments.get(3) };

                // Return all debtor values
                cursor = database.query(DebtorEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_BALANCES:
                segments = uri.getPathSegments();
                selection = BalanceEntry.TRIP_ID + "=?";
                selectionArgs = new String[] { segments.get(1) };

                // Return all person IDs and values
                cursor = database.query(BalanceEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_BALANCE_ID:
                selection = BalanceEntry.TRIP_ID + "=? AND " + BalanceEntry.PERSON_ID + "=?";
                segments = uri.getPathSegments();
                selectionArgs = new String[] { segments.get(1), segments.get(3) };

                // Return all person values
                cursor = database.query(BalanceEntry.VIEW_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PERSON_ID:
                selection = PersonEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Return person name
                cursor = database.query(PersonEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case EXPENSE_ID:
                selection = ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri))};

                // Return expense info
                cursor = database.query(ExpenseEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case TRIP_PERSON_ID:
                segments = uri.getPathSegments();

                String queryTripPersonExpenses = "SELECT " +  SummaryEntry.EXPENSE_ID + " AS _id, "
                        + ExpenseEntry.COLUMN_EXPENSE_NAME + " FROM " + SummaryEntry.VIEW_NAME
                        + ", " + ExpenseEntry.TABLE_NAME + " WHERE " + SummaryEntry.VIEW_NAME
                        + "." + SummaryEntry.EXPENSE_ID + " = " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry._ID + " AND " + SummaryEntry.VIEW_NAME + "."
                        + SummaryEntry.CREDITOR_ID + " = " + segments.get(3) + " AND "
                        + SummaryEntry.VIEW_NAME + "." + SummaryEntry.TRIP_ID + " = "
                        + segments.get(1);

                cursor = database.rawQuery(queryTripPersonExpenses, null);
                break;
            case TRIP_DATE_EXPENSES:
                segments = uri.getPathSegments();

                String queryTripDateExpenses = "SELECT " +  SummaryEntry.EXPENSE_ID + " AS _id, "
                        + ExpenseEntry.COLUMN_EXPENSE_NAME + " FROM " + SummaryEntry.VIEW_NAME
                        + ", " + ExpenseEntry.TABLE_NAME + " WHERE " + SummaryEntry.VIEW_NAME
                        + "." + SummaryEntry.EXPENSE_ID + " = " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry._ID + " AND " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_DATE + " = " + segments.get(3) + " AND "
                        + SummaryEntry.VIEW_NAME + "." + SummaryEntry.TRIP_ID + " = "
                        + segments.get(1);

                cursor = database.rawQuery(queryTripDateExpenses, null);
                break;
            case TRIP_TYPE_EXPENSES:
                segments = uri.getPathSegments();

                String queryTripTypeExpenses = "SELECT " +  SummaryEntry.EXPENSE_ID + " AS _id, "
                        + ExpenseEntry.COLUMN_EXPENSE_NAME + " FROM " + SummaryEntry.VIEW_NAME
                        + ", " + ExpenseEntry.TABLE_NAME + " WHERE " + SummaryEntry.VIEW_NAME
                        + "." + SummaryEntry.EXPENSE_ID + " = " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry._ID + " AND " + ExpenseEntry.TABLE_NAME + "."
                        + ExpenseEntry.COLUMN_EXPENSE_TYPE + " = " + segments.get(3) + " AND "
                        + SummaryEntry.VIEW_NAME + "." + SummaryEntry.TRIP_ID + " = "
                        + segments.get(1);

                cursor = database.rawQuery(queryTripTypeExpenses, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        // Return the cursor
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                return insertTrip(uri, contentValues);
            case TRIP_PERSON_ID:
                return insertTripPerson(uri, contentValues);
            case TRIP_EXPENSE:
                return insertTripExpense(uri, contentValues);
            case EXPENSE_PERSON:
                return insertExpensePerson(uri, contentValues);
            case PERSONS:
                return insertPerson(uri, contentValues);
            case EXPENSES:
                return insertExpense(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a trip into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTrip(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(TripEntry.COLUMN_TRIP_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Trip requires a name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new trip with the given values
        long id = database.insert(TripEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the trip content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a trip-person pair into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTripPerson(Uri uri, ContentValues values) {
        // Make sure the input is valid here

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new trip-person pair with the given values
        long id = database.insert(TripPersonEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for person query URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a trip-expense pair into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertTripExpense(Uri uri, ContentValues values) {
        // Make sure the input is valid here

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new trip-expense pair with the given values
        long id = database.insert(TripExpenseEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the expense query URI
        long tripId = values.getAsLong(TripContract.TripExpenseEntry.COLUMN_TRIP_ID);
        notifyTripExpense(tripId);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a expense-person pair into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertExpensePerson(Uri uri, ContentValues values) {
        // Make sure the input is valid here

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new expense-person pair with the given values
        long id = database.insert(ExpensePersonEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the trip content URI
        //getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert a person into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertPerson(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(PersonEntry.COLUMN_PERSON_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Person requires a name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new person with the given values
        long id = database.insert(PersonEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the trip content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Insert an expense into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertExpense(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(ExpenseEntry.COLUMN_EXPENSE_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Person requires a name");
        }

        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new expense with the given values
        long id = database.insert(ExpenseEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the trip content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                return updateTrip(uri, contentValues, selection, selectionArgs);
            case TRIP_ID:
                // For the TRIP_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTrip(uri, contentValues, selection, selectionArgs);
            case TRIP_PERSON_ID:
                selection = TripPersonEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTripPerson(uri, contentValues, selection, selectionArgs);
            case TRIP_EXPENSE_ID:
                selection = TripExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateTripExpense(uri, contentValues, selection, selectionArgs);
            case EXPENSE_PERSON_ID:
                selection = ExpensePersonEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateExpensePerson(uri, contentValues, selection, selectionArgs);
            case PERSON_ID:
                selection = PersonEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePerson(uri, contentValues, selection, selectionArgs);
            case EXPENSE_ID:
                selection = ExpenseEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateExpense(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update trips in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more trips).
     * Return the number of rows that were successfully updated.
     */
    private int updateTrip(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PetEntry#COLUMN_TRIP_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(TripEntry.COLUMN_TRIP_NAME)) {
            String name = values.getAsString(TripEntry.COLUMN_TRIP_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Trip requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TripEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateTripPerson(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if the input is valid here

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TripPersonEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateTripExpense(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if the input is valid here

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(TripExpenseEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateExpensePerson(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // Check if the input is valid here

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ExpensePersonEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updatePerson(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link PersonEntry#COLUMN_PERSON_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(PersonEntry.COLUMN_PERSON_NAME)) {
            String name = values.getAsString(PersonEntry.COLUMN_PERSON_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Person requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(PersonEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

    private int updateExpense(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ExpenseEntry#COLUMN_EXPENSE_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ExpenseEntry.COLUMN_EXPENSE_NAME)) {
            String name = values.getAsString(ExpenseEntry.COLUMN_EXPENSE_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Expense requires a name");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ExpenseEntry.TABLE_NAME, values, selection, selectionArgs);

        // Return the number of rows updated
        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Track the number of rows that were deleted
        int rowsDeleted = 0;

        // This cursor will hold results according to every relation pair
        Cursor cursor;
        String[] projection;
        List<String> segments;
        Set<String> PersonNotInTripSet;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                // Delete all rows that match the selection and selection args
                // Not available right now. TODO: add this feature in the future

                break;
            case TRIP_ID:
                // Delete a single trip row from trips table given by the ID in the URI
                selection = TripEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(TripEntry.TABLE_NAME, selection, selectionArgs);


                // Query all the expense ID using the trip ID
                selection = TripExpenseEntry.COLUMN_TRIP_ID + "=?";
                projection = new String[]{TripExpenseEntry.COLUMN_EXPENSE_ID};
                cursor = database.query(TripExpenseEntry.TABLE_NAME, projection,
                        selection, selectionArgs, null, null, null);
                // Loop to delete all expense and expense-person using expense ID
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            // Get the expense ID and update the selectionArgs
                            selectionArgs = new String[] { cursor.getString(cursor.getColumnIndex(TripExpenseEntry.COLUMN_EXPENSE_ID)) };
                            selection = ExpenseEntry._ID + "=?";
                            // Delete an expense in expenses table using its ID
                            database.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs);
                            // Delete an expense-person pair in expense-person table
                            database.delete(ExpensePersonEntry.TABLE_NAME, selection, selectionArgs);
                        } while ( cursor.moveToNext() );
                    }
                }
                // Query person ID in trip-person who is only in this trip
                projection = new String[]{ TripPersonEntry.COLUMN_PERSON_ID };
                selection = TripExpenseEntry.COLUMN_TRIP_ID + "!=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                cursor = database.query(TripPersonEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                PersonNotInTripSet = new HashSet<String>();
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String personNotInTrip = cursor.getString(cursor.getColumnIndex(TripPersonEntry.COLUMN_PERSON_ID));
                            PersonNotInTripSet.add(personNotInTrip);
                        } while ( cursor.moveToNext() );
                    }
                }

                // Delete these persons in person table
                selection = TripExpenseEntry.COLUMN_TRIP_ID + "=?";
                cursor = database.query(TripPersonEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, null);
                if (cursor != null) {
                    if (cursor.moveToFirst()) {
                        do {
                            String personInTrip = cursor.getString(cursor.getColumnIndex(TripPersonEntry.COLUMN_PERSON_ID));
                            if (!PersonNotInTripSet.contains(personInTrip)) {
                                // Delete the person in the person table
                                selection = PersonEntry._ID + "=?";
                                selectionArgs = new String[] { personInTrip };
                                database.delete(PersonEntry.TABLE_NAME, selection, selectionArgs);
                            }
                        } while ( cursor.moveToNext() );
                    }
                }

                // Delete trip-person with trip ID
                selection = TripPersonEntry.COLUMN_TRIP_ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                database.delete(TripPersonEntry.TABLE_NAME, selection, selectionArgs);

                // Delete trip-expense with trip ID
                selection = TripExpenseEntry.COLUMN_TRIP_ID + "=?";
                database.delete(TripExpenseEntry.TABLE_NAME, selection, selectionArgs);

                break;
            case TRIP_PERSONS:
                // TODO: add the feature to delete multiple persons in the future
                break;
            case TRIP_PERSON_ID:
                // The person can be deleted only if he/she isn't involved in any expense.
                boolean canBeDeleted = true;

                segments = uri.getPathSegments();

                projection = new String[] { DebtorEntry.DEBTOR_ID };
                selection = DebtorEntry.TRIP_ID + "=? AND " + DebtorEntry.DEBTOR_ID
                        + "=?";
                selectionArgs = new String[] { segments.get(1), segments.get(3) };
                cursor = database.query(DebtorEntry.VIEW_NAME, projection, selection,
                        selectionArgs, null, null, null);

                if (cursor !=  null && cursor.getCount() >= 1) {
                    // Person is a debtor
                    canBeDeleted = false;
                }

                projection = new String[] { CreditorEntry.CREDITOR_ID };
                selection = CreditorEntry.TRIP_ID + "=? AND " + CreditorEntry.CREDITOR_ID
                        + "=?";
                cursor = database.query(CreditorEntry.VIEW_NAME, projection, selection,
                        selectionArgs, null, null, null);

                if (cursor !=  null && cursor.getCount() >= 1) {
                    // Person is a creditor
                    canBeDeleted = false;
                }

                if ( canBeDeleted ) {
                    // Check if the person is in another trip
                    selection = TripPersonEntry.COLUMN_TRIP_ID + "!=? AND "
                            + TripPersonEntry.COLUMN_PERSON_ID + "=?";
                    cursor = database.query(TripPersonEntry.TABLE_NAME, null, selection,
                            selectionArgs, null, null, null);

                    // Delete the person in person table if not in another trip
                    if (cursor.getCount() == 0) {
                        selection = PersonEntry._ID + "=?";
                        selectionArgs = new String[] { segments.get(3) };
                        database.delete(PersonEntry.TABLE_NAME, selection, selectionArgs);
                    }

                    // Delete the relation in trip-person pair
                    selection = TripPersonEntry.COLUMN_TRIP_ID + "=? AND " +
                            TripPersonEntry.COLUMN_PERSON_ID + "=?";
                    selectionArgs = new String[] { segments.get(1), segments.get(3) };
                    rowsDeleted = database.delete(TripPersonEntry.TABLE_NAME, selection, selectionArgs);
                }

                break;
            case TRIP_EXPENSES:
                // TODO: add the feature to delete multiple expenses in the future
                break;
            case TRIP_EXPENSE_ID:
                segments = uri.getPathSegments();
                // Delete the trip-expense relation
                selection = TripExpenseEntry.COLUMN_TRIP_ID + "=? AND " +
                        TripExpenseEntry.COLUMN_EXPENSE_ID + "=?";
                selectionArgs = new String[] { segments.get(1), segments.get(3) };
                database.delete(TripExpenseEntry.TABLE_NAME, selection, selectionArgs);

                // Delete the expense-person relation
                selection = ExpensePersonEntry.COLUMN_EXPENSE_ID + "=?";
                selectionArgs = new String[] { segments.get(3) };
                database.delete(ExpensePersonEntry.TABLE_NAME, selection, selectionArgs);

                // Delete the expense in the expense table
                selection = ExpenseEntry._ID + "=?";
                rowsDeleted = database.delete(ExpenseEntry.TABLE_NAME, selection, selectionArgs);

                Long tripId = Long.parseLong(segments.get(1));
                //notifyTripExpense(tripId);

                Uri newUri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, tripId);
                getContext().getContentResolver().notifyChange(newUri, null);

                break;
            case EXPENSE_PERSON_ID:
                // delete all rows according to the expense id
                selection = ExpensePersonEntry.COLUMN_EXPENSE_ID + " = "
                        + ContentUris.parseId(uri);
                rowsDeleted = database.delete(ExpensePersonEntry.TABLE_NAME, selection, null);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows deleted
        return rowsDeleted;
    }

    private void notifyTripExpense(long tripId) {
        Uri newUri = ContentUris.withAppendedId(TripEntry.CONTENT_URI, tripId);
        newUri = Uri.withAppendedPath(newUri, TripContract.PATH_EXPENSES);
        getContext().getContentResolver().notifyChange(newUri, null);
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case TRIPS:
                return TripEntry.CONTENT_LIST_TYPE;
            case TRIP_ID:
                return TripEntry.CONTENT_ITEM_TYPE;
            case TRIP_PERSONS:
                return TripPersonEntry.CONTENT_LIST_TYPE;
            case TRIP_PERSON_ID:
                return TripPersonEntry.CONTENT_ITEM_TYPE;
            case TRIP_EXPENSES:
                return TripExpenseEntry.CONTENT_LIST_TYPE;
            case TRIP_EXPENSE_ID:
                return TripExpenseEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}

