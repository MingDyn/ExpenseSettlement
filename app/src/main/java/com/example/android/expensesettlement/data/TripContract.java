package com.example.android.expensesettlement.data;

/**
 * Created by ming1 on 2/26/2017.
 */

import android.net.Uri;
import android.content.ContentResolver;
import android.provider.BaseColumns;

public class TripContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private TripContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the
     * device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.expensesettlement";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.expensesettlement/trips/ is a valid path for
     * looking at trip data. content://com.example.android.expensesettlement/staff/ will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_TRIPS = "trips";
    public static final String PATH_PERSONS = "persons";
    public static final String PATH_EXPENSES = "expenses";
    public static final String PATH_TRIP_PERSON = "trip_person";
    public static final String PATH_TRIP_EXPENSE = "trip_expense";
    public static final String PATH_EXPENSE_PERSON = "expense_person";
    public static final String PATH_SUMMARY = "summary";
    public static final String PATH_CREDITOR = "creditor";
    public static final String PATH_DEBTOR = "debtor";
    public static final String PATH_BALANCE = "balance";

    public static final String PATH_EXPENSE_DATE = "date";
    public static final String PATH_EXPENSE_TYPE = "type";

    /**
     * Inner class that defines constant values for the trips database table.
     * Each entry in the table represents a single trip.
     */
    public static final class TripEntry implements BaseColumns {

        /** The content URI to access the trip data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRIPS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trips.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single trip.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIPS;

        /** Name of database table for trips */
        public final static String TABLE_NAME = "trips";

        /**
         * Unique ID number for the trip (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the trip.
         *
         * Type: TEXT
         */
        public final static String COLUMN_TRIP_NAME ="trip_name";
    }

    /**
     * Inner class that defines constant values for the persons database table.
     * Each entry in the table represents a single person.
     */
    public static final class PersonEntry implements BaseColumns {

        /** The content URI to access the person data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PERSONS);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of persons.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single person.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PERSONS;

        /** Name of database table for persons */
        public final static String TABLE_NAME = "persons";

        /**
         * Unique ID number for the person (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the person.
         *
         * Type: TEXT
         */
        public final static String COLUMN_PERSON_NAME ="person_name";
    }

    /**
     * Inner class that defines constant values for the expenses database table.
     * Each entry in the table represents a single expense.
     */
    public static final class ExpenseEntry implements BaseColumns {

        /**
         * The content URI to access the expense data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSES);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of expenses.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single expense.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSES;

        /**
         * Name of database table for expenses
         */
        public final static String TABLE_NAME = "expenses";

        /**
         * Unique ID number for the expense (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the expense.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EXPENSE_NAME = "expense_name";

        /**
         * Date of the expense.
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_EXPENSE_DATE = "date";

        /**
         * Type of the expense.
         * <p>
         * The only possible values are {@link #TYPE_HOTEL}, {@link #TYPE_SHOPPING},
         * {@link #TYPE_TRANSPORTATION}. {@link #TYPE_ENTERTAINMENT}, {@link #TYPE_FOOD},
         * {@link #TYPE_OTHER}
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_EXPENSE_TYPE = "type";

        /**
         * Value of the expense.
         * <p>
         * Type: INTEGER NOT NULL DEFAULT 0
         */
        public final static String COLUMN_EXPENSE_VALUE = "value";

        /**
         * Number of the debtors for this expense, may include the creditor himself.
         * <p>
         * Type: INTEGER NOT NULL DEFAULT 1
         */
        public final static String COLUMN_NUM_OF_DEBTORS = "num_of_debtors";

        /**
         * Possible values for the type of the expense.
         */
        public static final int TYPE_HOTEL = 0;
        public static final int TYPE_SHOPPING = 1;
        public static final int TYPE_TRANSPORTATION = 2;
        public static final int TYPE_ENTERTAINMENT = 3;
        public static final int TYPE_FOOD = 4;
        public static final int TYPE_OTHER = 5;

        /**
         * Returns whether or not the given gender is {@link #TYPE_HOTEL}, {@link #TYPE_SHOPPING},
         * {@link #TYPE_TRANSPORTATION}. {@link #TYPE_ENTERTAINMENT}, {@link #TYPE_FOOD},
         * {@link #TYPE_OTHER}
         */
        public static boolean isValidExpense(int expense) {
            if (expense == TYPE_HOTEL || expense == TYPE_SHOPPING || expense == TYPE_TRANSPORTATION
                    || expense == TYPE_ENTERTAINMENT || expense == TYPE_FOOD || expense == TYPE_OTHER) {
                return true;
            }
            return false;
        }
    }

    /**
     * Inner class that defines constant values for the trip_person database table.
     * Each entry in the table represents a single trip_person pair.
     */
    public static final class TripPersonEntry implements BaseColumns {

        /**
         * The content URI to access the trip_person data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRIP_PERSON);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trip_person pairs.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_PERSON;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single trip_person pair.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_PERSON;

        /**
         * Name of database table for trip_person
         */
        public final static String TABLE_NAME = "trip_person";

        /**
         * Unique ID number for the trip_person (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * ID of the person.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PERSON_ID = "person_id";

        /**
         * ID of the trip.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_TRIP_ID = "trip_id";
    }

    /**
     * Inner class that defines constant values for the trip_expense database table.
     * Each entry in the table represents a single trip_expense pair.
     */
    public static final class TripExpenseEntry implements BaseColumns {

        /**
         * The content URI to access the trip_expense data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_TRIP_EXPENSE);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of trip_expense pairs.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_EXPENSE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single trip_expense pair.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRIP_EXPENSE;

        /**
         * Name of database table for trip_expense
         */
        public final static String TABLE_NAME = "trip_expense";

        /**
         * Unique ID number for the trip_expense (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * ID of the expense.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_EXPENSE_ID = "expense_id";

        /**
         * ID of the trip.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_TRIP_ID = "trip_id";
    }

    /**
     * Inner class that defines constant values for the expense_person database table.
     * Each entry in the table represents a single expense_person pair.
     */
    public static final class ExpensePersonEntry implements BaseColumns {

        /**
         * The content URI to access the expense_person data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_EXPENSE_PERSON);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of expense_person pairs.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE_PERSON;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single expense_person pair.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_EXPENSE_PERSON;

        /**
         * Name of database table for expense_person
         */
        public final static String TABLE_NAME = "expense_person";

        /**
         * Unique ID number for the expense_person (only for use in the database table).
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * ID of the expense.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_EXPENSE_ID = "expense_id";

        /**
         * ID of the creditor.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_CREDITOR_ID = "creditor_id";

        /**
         * ID of the debtor.
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_DEBTOR_ID = "debtor_id";
    }

    /**
     * Inner class that defines constant values for the summary database view.
     */
    public static final class SummaryEntry {

        /**
         * The content URI to access the summary data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SUMMARY);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of summary.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single summary.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SUMMARY;

        /**
         * Name of database view for summary
         */
        public final static String VIEW_NAME = "summary";

        /**
         * ID of trip
         */
        public final static String TRIP_ID = "trip_id";

        /**
         * ID of creditor
         */
        public final static String CREDITOR_ID = "creditor_id";

        /**
         * ID of expense
         */
        public final static String EXPENSE_ID = "expense_id";
    }

    /**
     * Inner class that defines constant values for the creditor database view.
     */
    public static final class CreditorEntry {

        /**
         * The content URI to access the creditor data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_CREDITOR);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of creditor.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CREDITOR;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single creditor.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_CREDITOR;

        /**
         * Name of database view for creditor
         */
        public final static String VIEW_NAME = "creditor";

        /**
         * ID of trip
         */
        public final static String TRIP_ID = "trip_id";

        /**
         * ID of creditor
         */
        public final static String CREDITOR_ID = "creditor_id";

        /**
         * Value of balance
         *
         * Type: INTEGER
         */
        public final static String BALANCE_VAL = "value";
    }

    /**
     * Inner class that defines constant values for the debtor database view.
     */
    public static final class DebtorEntry {

        /**
         * The content URI to access the debtor data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_DEBTOR);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of debtor.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEBTOR;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single debtor.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_DEBTOR;

        /**
         * Name of database view for debtor
         */
        public final static String VIEW_NAME = "debtor";

        /**
         * ID of trip
         */
        public final static String TRIP_ID = "trip_id";

        /**
         * ID of debtor
         */
        public final static String DEBTOR_ID = "debtor_id";

        /**
         * Value of balance
         *
         * Type: INTEGER
         */
        public final static String BALANCE_VALUE = "value";
    }

    /**
     * Inner class that defines constant values for the balance database view.
     */
    public static final class BalanceEntry {

        /**
         * The content URI to access the balance data in the provider
         */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BALANCE);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of balance.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single balance.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BALANCE;

        /**
         * Name of database view for balance
         */
        public final static String VIEW_NAME = "balance";

        /**
         * ID of trip
         */
        public final static String TRIP_ID = "trip_id";

        /**
         * ID of debtor
         */
        public final static String PERSON_ID = "person_id";

        /**
         * Value of balance
         *
         * Type: INTEGER
         */
        public final static String BALANCE_VALUE = "value";
    }
}
