package com.example.android.expensesettlement.data;

/**
 * Created by ming1 on 2/28/2017.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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

/**
 *  Database helper for expensesettlement app. Manage database creation and version.
 */
public class TripDbHelper extends SQLiteOpenHelper{
    public static final String LOG_TAG = TripDbHelper.class.getSimpleName();

    /** Name of the db file */
    private static final String DATABASE_NAME = "trips.db";

    /**
     * Database version. If you change the database schema, you must increment the database version.
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link TripDbHelper}.
     *
     * @param context of the app
     */
    public TripDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is created for the first time.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create different String that contains the SQL statement to create tables
        String SQL_CREATE_TRIPS_TABLE =  "CREATE TABLE " + TripEntry.TABLE_NAME + " ("
                + TripEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TripEntry.COLUMN_TRIP_NAME + " TEXT NOT NULL);";

        String SQL_CREATE_PERSON_TABLE = "CREATE TABLE " + PersonEntry.TABLE_NAME + " ("
                + PersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PersonEntry.COLUMN_PERSON_NAME + " TEXT NOT NULL);";

        String SQL_CREATE_EXPENSE_TABLE = "CREATE TABLE " + ExpenseEntry.TABLE_NAME + " ("
                + ExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpenseEntry.COLUMN_EXPENSE_VALUE + " INTEGER NOT NULL DEFAULT 0, "
                + ExpenseEntry.COLUMN_NUM_OF_DEBTORS + " INTEGER NOT NULL DEFAULT 1, "
                + ExpenseEntry.COLUMN_EXPENSE_NAME + " TEXT NOT NULL, "
                + ExpenseEntry.COLUMN_EXPENSE_TYPE + " INTEGER NOT NULL, "
                + ExpenseEntry.COLUMN_EXPENSE_DATE + " INTEGER NOT NULL);";

        String SQL_CREATE_TRIP_PERSON_TABLE = "CREATE TABLE " + TripPersonEntry.TABLE_NAME + " ("
                + TripPersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TripPersonEntry.COLUMN_TRIP_ID + " INTEGER NOT NULL, "
                + TripPersonEntry.COLUMN_PERSON_ID + " INTEGER NOT NULL);";

        String SQL_CREATE_EXPENSE_PERSON_TABLE = "CREATE TABLE " + ExpensePersonEntry.TABLE_NAME + " ("
                + ExpensePersonEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ExpensePersonEntry.COLUMN_EXPENSE_ID + " INTEGER NOT NULL, "
                + ExpensePersonEntry.COLUMN_CREDITOR_ID + " INTEGER NOT NULL, "
                + ExpensePersonEntry.COLUMN_DEBTOR_ID + " INTEGER NOT NULL);";

        String SQL_CREATE_TRIP_EXPENSE_TABLE = "CREATE TABLE " + TripExpenseEntry.TABLE_NAME
                + " (" + TripExpenseEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TripExpenseEntry.COLUMN_TRIP_ID + " INTEGER NOT NULL, "
                + TripExpenseEntry.COLUMN_EXPENSE_ID + " INTEGER NOT NULL);";

        // Create different String to create views
        String SQL_CREATE_SUMMARY_VIEW = "CREATE VIEW " + SummaryEntry.VIEW_NAME
                + " AS SELECT DISTINCT " + TripPersonEntry.TABLE_NAME + "."
                + TripPersonEntry.COLUMN_TRIP_ID + " AS " + SummaryEntry.TRIP_ID + ", "
                + ExpensePersonEntry.TABLE_NAME + "." + ExpensePersonEntry.COLUMN_CREDITOR_ID
                + " AS " + SummaryEntry.CREDITOR_ID + ", "+ ExpensePersonEntry.TABLE_NAME
                + "." + ExpensePersonEntry.COLUMN_EXPENSE_ID + " AS "
                + SummaryEntry.EXPENSE_ID + " FROM " + TripPersonEntry.TABLE_NAME + ", "
                + TripExpenseEntry.TABLE_NAME + ", " + ExpensePersonEntry.TABLE_NAME
                + " WHERE " + TripExpenseEntry.TABLE_NAME + "."
                + TripExpenseEntry.COLUMN_TRIP_ID + " == " + TripPersonEntry.TABLE_NAME + "."
                + TripPersonEntry.COLUMN_TRIP_ID + " AND " + TripExpenseEntry.TABLE_NAME + "."
                + TripExpenseEntry.COLUMN_EXPENSE_ID + " == " + ExpensePersonEntry.TABLE_NAME
                + "." + ExpensePersonEntry.COLUMN_EXPENSE_ID + " AND "
                + TripPersonEntry.TABLE_NAME + "." + TripPersonEntry.COLUMN_PERSON_ID
                + " == " + ExpensePersonEntry.TABLE_NAME + "."
                + ExpensePersonEntry.COLUMN_CREDITOR_ID
                + ";";

        String SQL_CREATE_CREDITOR_VIEW = "CREATE VIEW " + CreditorEntry.VIEW_NAME
                + " AS SELECT " + SummaryEntry.VIEW_NAME + "." + SummaryEntry.TRIP_ID
                + " AS " + CreditorEntry.TRIP_ID + ", "
                + SummaryEntry.CREDITOR_ID + " AS " + CreditorEntry.CREDITOR_ID + ", "
                + ExpenseEntry.COLUMN_EXPENSE_VALUE + " AS "
                + CreditorEntry.BALANCE_VAL + " FROM " + SummaryEntry.VIEW_NAME + ", "
                + ExpenseEntry.TABLE_NAME + " WHERE " + SummaryEntry.VIEW_NAME + "."
                + SummaryEntry.EXPENSE_ID + " == " + ExpenseEntry.TABLE_NAME + "."
                + ExpenseEntry._ID
                + ";";

        String SQL_CREATE_DEBTOR_VIEW = "CREATE VIEW " + DebtorEntry.VIEW_NAME
                + " AS SELECT " + SummaryEntry.TRIP_ID + " AS " + DebtorEntry.TRIP_ID + ", "
                + ExpensePersonEntry.COLUMN_DEBTOR_ID + " AS " + DebtorEntry.DEBTOR_ID + ", "
                + "-" + ExpenseEntry.COLUMN_EXPENSE_VALUE + "/" + ExpenseEntry.COLUMN_NUM_OF_DEBTORS
                + " AS " + DebtorEntry.BALANCE_VALUE + " FROM " + SummaryEntry.VIEW_NAME + ", "
                + ExpensePersonEntry.TABLE_NAME + ", " + ExpenseEntry.TABLE_NAME + " WHERE "
                + SummaryEntry.VIEW_NAME + "." + SummaryEntry.EXPENSE_ID + " == "
                + ExpenseEntry.TABLE_NAME + "." + ExpenseEntry._ID + " AND "
                + ExpensePersonEntry.TABLE_NAME + "." + ExpensePersonEntry.COLUMN_EXPENSE_ID
                + " == " + ExpenseEntry.TABLE_NAME + "." + ExpenseEntry._ID
                + ";";

        String SQL_CREATE_BALANCE_VIEW = "CREATE VIEW " + BalanceEntry.VIEW_NAME + " AS SELECT "
                + BalanceEntry.TRIP_ID + ", " + BalanceEntry.PERSON_ID + ", SUM("
                + BalanceEntry.BALANCE_VALUE + ") AS " + BalanceEntry.BALANCE_VALUE
                + " FROM ( SELECT " + CreditorEntry.TRIP_ID + ", "
                + CreditorEntry.CREDITOR_ID + " AS " + BalanceEntry.PERSON_ID + ", "
                + CreditorEntry.BALANCE_VAL + " FROM " + CreditorEntry.VIEW_NAME
                + " UNION ALL SELECT " + DebtorEntry.TRIP_ID + ", " + DebtorEntry.DEBTOR_ID
                + " AS " + BalanceEntry.PERSON_ID + ", " + DebtorEntry.BALANCE_VALUE + " FROM "
                + DebtorEntry.VIEW_NAME + ") GROUP BY " + BalanceEntry.TRIP_ID + ", "
                + BalanceEntry.PERSON_ID
                + ";";

        // Execute the SQL statement
        db.execSQL(SQL_CREATE_TRIPS_TABLE);
        db.execSQL(SQL_CREATE_PERSON_TABLE);
        db.execSQL(SQL_CREATE_EXPENSE_TABLE);
        db.execSQL(SQL_CREATE_TRIP_PERSON_TABLE);
        db.execSQL(SQL_CREATE_TRIP_EXPENSE_TABLE);
        db.execSQL(SQL_CREATE_EXPENSE_PERSON_TABLE);
        db.execSQL(SQL_CREATE_SUMMARY_VIEW);
        db.execSQL(SQL_CREATE_CREDITOR_VIEW);
        db.execSQL(SQL_CREATE_DEBTOR_VIEW);
        db.execSQL(SQL_CREATE_BALANCE_VIEW);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
