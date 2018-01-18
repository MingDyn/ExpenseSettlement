package com.example.android.expensesettlement;

import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.GridView;

import com.squareup.timessquare.CalendarCellDecorator;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;
import com.squareup.timessquare.DefaultDayViewAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.expensesettlement.data.TripContract;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

/**
 * Display list of persons, a calendar with trip dates selected, a pie chart for expense types
 */

public class CatalogActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the data loader */
    private static final int PERSON_LOADER = 0;
    private static final int EXPENSE_LOADER = 1;

    /** Content URI for the trip */
    private Uri mCurrentTripUri;

    // Grid view for all persons
    private MyGridView personGridView;

    // Adapter for the GridView
    private PersonCursorAdapter mCursorAdapter;

    // calendar picker view to show all dates within the trip
    private CalendarPickerView mDateView;
    private List<Date> selectedDates;

    // Pie chart to display the expense types
    private PieChart mChart;
    private String[] xValues = { "Hotel", "Shopping", "Transportation", "Entertainment",
            "Food", "Other"};

    // Button to show all expenses
    private Button mButton;

    // ListView to show all expenses
    private MyListView allExpenseList;
    private ExpenseCursorAdapter mExpenseCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Examine the intent that was used to launch this activity,
        Intent intent = getIntent();
        mCurrentTripUri = intent.getData();

        /*
         * Setup the grid view
         * */
        // Find the GridView which will be populated with the person data
        personGridView = (MyGridView) findViewById(R.id.grid);
        personGridView.setExpanded(true);

        // Find and set empty view on the GridView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_person_text);
        personGridView.setEmptyView(emptyView);

        // Setup an Adapter to create a grid item for each row of person data in the Cursor.
        // There is no person data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new PersonCursorAdapter(this, null);
        personGridView.setAdapter(mCursorAdapter);

        // Setup the item click listener
        personGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link ExpenseDisplayActivity}
                Intent intent = new Intent(CatalogActivity.this, ExpenseDisplayActivity.class);

                // Append the person path and the person id which is clicked on
                Uri currentPersonUri = Uri.withAppendedPath(mCurrentTripUri, TripContract.PATH_PERSONS);
                currentPersonUri = ContentUris.withAppendedId(currentPersonUri, id);

                // Set the URI on the data field of the intent
                intent.setData(currentPersonUri);

                // Launch the {@link EditorActivity} to display the data for the current pet.
                startActivity(intent);
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(PERSON_LOADER, null, this);

        /*
         * Setup the calendar
         * */
        mDateView = (CalendarPickerView) findViewById(R.id.calendar_view);
        initializeCalendar();

        mDateView.setCellClickInterceptor(new CalendarPickerView.CellClickInterceptor() {
            @Override
            public boolean onCellClicked(Date date) {

                // Check if the date is selected, do nothing when the clicked date is unselected TODO
                if (!selectedDates.contains(date))
                    return true;

                // Create new intent to go to {@link ExpenseDisplayActivity}
                Intent intent = new Intent(CatalogActivity.this, ExpenseDisplayActivity.class);

                // Get the date id(long) from date(Date)
                long id = date.getTime();

                // Append the person path and the person id which is clicked on
                Uri currentDateUri = Uri.withAppendedPath(mCurrentTripUri,
                        TripContract.PATH_EXPENSE_DATE);
                currentDateUri = ContentUris.withAppendedId(currentDateUri, id);

                // Set the URI on the data field of the intent
                intent.setData(currentDateUri);

                // Launch the {@link ExpenseDisplayActivity} to display all expenses for the date.
                startActivity(intent);
                return true;
            }
        });

        // Kick off the loader
        getLoaderManager().initLoader(EXPENSE_LOADER, null, this);

        /*
         * Setup the pie chart
         * */
        mChart = (PieChart) findViewById(R.id.type_chart);
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(0, 10, 0, 10);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setNoDataText("No expense added yet.");
        mChart.setRotationEnabled(true);
        mChart.setHoleColor(Color.WHITE);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setHoleRadius(10f);
        mChart.setTransparentCircleRadius(15f);
        mChart.setDrawCenterText(false);
        mChart.setRotationAngle(0);
        // enable rotation of the chart by touch
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        // add a selection listener
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                if (e == null)
                    return;
                Log.i("VAL SELECTED",
                        "Value: " + e.getY() + ", index: " + h.getX()
                                + ", DataSet index: " + h.getDataSetIndex());
                // Create new intent to go to {@link ExpenseDisplayActivity}
                Intent intent = new Intent(CatalogActivity.this, ExpenseDisplayActivity.class);

                // Get the type id(long) from the entry
                String label = ((PieEntry)e).getLabel();
                int id;
                if (label.equals(getString(R.string.type_hotel))) {
                    id = TripContract.ExpenseEntry.TYPE_HOTEL;
                } else if (label.equals(getString(R.string.type_shopping))) {
                    id = TripContract.ExpenseEntry.TYPE_SHOPPING;
                } else if (label.equals(getString(R.string.type_transportation))) {
                    id = TripContract.ExpenseEntry.TYPE_TRANSPORTATION;
                } else if (label.equals(getString(R.string.type_entertainment))) {
                    id = TripContract.ExpenseEntry.TYPE_ENTERTAINMENT;
                } else if (label.equals(getString(R.string.type_food))) {
                    id = TripContract.ExpenseEntry.TYPE_FOOD;
                } else {
                    id = TripContract.ExpenseEntry.TYPE_OTHER;
                }

                // Append the type path and the type id which is clicked on
                Uri currentTypeUri = Uri.withAppendedPath(mCurrentTripUri, TripContract.PATH_EXPENSE_TYPE);
                currentTypeUri = ContentUris.withAppendedId(currentTypeUri, id);

                // Set the URI on the data field of the intent
                intent.setData(currentTypeUri);

                // Launch the {@link ExpenseDisplayActivity} to display all expenses for the type.
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {
                Log.i("PieChart", "nothing selected");
            }
        });
        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        // Disable the legends
        mChart.getLegend().setEnabled(false);

        // entry label styling
        mChart.setDrawEntryLabels(false);

        /*
         * Setup the button to show the list of all expenses
         * */
        mButton = (Button) findViewById(R.id.button_expense);
        allExpenseList = (MyListView) findViewById(R.id.list_all_expenses);
        allExpenseList.setExpanded(true);

        // boolean to indicate if the button is clicked
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ( allExpenseList.getVisibility() == View.GONE ) {
                    mButton.setText("HIDE ALL EXPENSES");
                    // Display all expenses
                    allExpenseList.setVisibility(View.VISIBLE);
                } else {
                    mButton.setText("SHOW ALL EXPENSES");
                    // Hide all expenses
                    allExpenseList.setVisibility(View.GONE);
                }
            }
        });
        // Setup the ListView with the expense adapter
        mExpenseCursorAdapter = new ExpenseCursorAdapter(this, null);
        allExpenseList.setAdapter(mExpenseCursorAdapter);
        // Setup the item click listenter
        allExpenseList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                // Create new intent to go to {@link ExpenseDisplayActivity}
                Intent intent = new Intent(CatalogActivity.this, ExpenseDetailsActivity.class);

                // Append the type path and the expense id which is clicked on
                Uri currentExpenseUri = Uri.withAppendedPath(mCurrentTripUri, TripContract.PATH_EXPENSES);
                currentExpenseUri = ContentUris.withAppendedId(currentExpenseUri, id);

                // Set the URI on the data field of the intent
                intent.setData(currentExpenseUri);

                // Launch the {@link ExpenseDisplayActivity} to display the specific expense
                startActivity(intent);
            }
        });

        /*
         * Setup FAB to open PersonEditorActivity
         * */
        FloatingActionButton fabPerson = (FloatingActionButton) findViewById(R.id.fab_person);
        fabPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, PersonEditorActivity.class);
                intent.setData(mCurrentTripUri);
                startActivity(intent);
            }
        });

        /*
         * Setup FAB to open AddExpenseActivity
         * */
        FloatingActionButton fabExpense = (FloatingActionButton) findViewById(R.id.fab_expense);
        fabExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there is no person added, toast error when no person added
                if ( personGridView.getCount() == 0 ) {
                    Toast.makeText(CatalogActivity.this, "Should have at least one person",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CatalogActivity.this, AddExpenseActivity.class);
                    intent.setData(mCurrentTripUri);
                    startActivity(intent);
                }
            }
        });

        /*
         * Setup FAB to open CalculateActivity
         * */
        FloatingActionButton fabCalculate = (FloatingActionButton) findViewById(R.id.fab_calculate);
        fabCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check if there is no expense added, toast error when no expense added
                if ( allExpenseList.getCount() == 0 ) {
                    Toast.makeText(CatalogActivity.this, "No expense to settle",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(CatalogActivity.this, CalculateActivity.class);
                    intent.setData(mCurrentTripUri);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    /**
     * Prompt the user to confirm that they want to delete this pet.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deleteTrip();
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

    // To delete this trip
    private void deleteTrip() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentTripUri != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            getContentResolver().delete(mCurrentTripUri, null, null);

            Toast.makeText(this, getString(R.string.delete_successful),
                    Toast.LENGTH_SHORT).show();
        }

        // Close the activity
        finish();
    }

    // To change the trip name
    private void changeTripName() {
        Intent intent = new Intent(CatalogActivity.this, TripEditorActivity.class);

        // Set the URI on the data field of the intent
        intent.setData(mCurrentTripUri);

        // Launch the {@link EditorActivity} to display the data for the current pet.
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete_trip:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit_trip_name:
                changeTripName();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        Uri queryURI;

        if ( id == PERSON_LOADER ) {
            queryURI = Uri.withAppendedPath(mCurrentTripUri, TripContract.PATH_PERSONS);
        } else {
            queryURI = Uri.withAppendedPath(mCurrentTripUri, TripContract.PATH_EXPENSES);
        }

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                queryURI,               // Provider content URI to query
                null,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (data == null || data.getCount() < 1) {
            if ( data != null) {
                // data size is 0
                if (loader.getId() == PERSON_LOADER) {
                    mCursorAdapter.swapCursor(data);
                } else {
                    mExpenseCursorAdapter.swapCursor(data);

                    // reset calendar
                    initializeCalendar();

                    // refresh pie chart
                    mChart.clear();
                }
            }
            return;
        }

        if ( loader.getId() == PERSON_LOADER ) {
            // Update {@link PersonCursorAdapter} with this new cursor containing updated person data
            mCursorAdapter.swapCursor(data);
        } else {
            int[] cntType = new int[6];
            Set<Date> tripDates = new HashSet<Date>();
            Date firstDate = null, lastDate = null;
            ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

            // Get the data from the cursor and update parameters above
            if (data.moveToFirst()) {
                do {
                    // Get the date(Long)
                    long dateLong = data.getLong(data.getColumnIndex(TripContract.ExpenseEntry.COLUMN_EXPENSE_DATE));
                    Date newDate = new Date(dateLong);
                    tripDates.add(newDate);
                    if (firstDate == null || firstDate.after(newDate)) {
                        firstDate = newDate;
                    }
                    if (lastDate == null || lastDate.before(newDate)) {
                        lastDate = newDate;
                    }

                    // Get the expense type ID (int)
                    int typeId = data.getInt(data.getColumnIndex(TripContract.ExpenseEntry.COLUMN_EXPENSE_TYPE));
                    //cntType[typeId]++;
                    int cost = data.getInt(data.getColumnIndex(TripContract.ExpenseEntry.COLUMN_EXPENSE_VALUE));
                    cntType[typeId] += cost;
                } while ( data.moveToNext() );
            }

            // lastday + 1 since the last day is exclusive
            Calendar cTmp = Calendar.getInstance();
            cTmp.setTime(lastDate);
            cTmp.add(Calendar.DATE, 1);
            lastDate = cTmp.getTime();

            // Update the calendar
            mDateView.setCustomDayView(new DefaultDayViewAdapter());
            mDateView.setDecorators(Collections.<CalendarCellDecorator>emptyList());
            mDateView.init(firstDate, lastDate)
                    .inMode(SelectionMode.MULTIPLE)
                    .withSelectedDates(tripDates);

            selectedDates = mDateView.getSelectedDates();

            // icon set
            int[] iconSet = new int[]{ R.drawable.hotel_icon, R.drawable.shopping_icon,
                    R.drawable.transportation_icon, R.drawable.entertainment_icon,
                    R.drawable.food_icon, R.drawable.other_icon };

            // Update the pie chart
            for(int i = 0; i < xValues.length; ++i) {
                if (cntType[i] > 0) {
                    entries.add(new PieEntry((float)cntType[i], xValues[i], getResources().getDrawable(iconSet[i])));
                }
            }

            PieDataSet dataSet = new PieDataSet(entries, "");
            dataSet.setDrawIcons(true);
            dataSet.setSliceSpace(3f);
            dataSet.setIconsOffset(new MPPointF(0, 40));
            dataSet.setSelectionShift(5f);

            // add a lot of colors

            ArrayList<Integer> colors = new ArrayList<Integer>();

            for (int c : ColorTemplate.COLORFUL_COLORS)
                colors.add(c);

            for (int c : ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            dataSet.setColors(colors);
            //dataSet.setSelectionShift(0f);

            PieData pieData = new PieData(dataSet);
            pieData.setValueFormatter(new PercentFormatter());
            pieData.setValueTextSize(14f);
            pieData.setValueTextColor(Color.WHITE);
            pieData.setValueTypeface(Typeface.createFromAsset(getAssets(), "OpenSans-Regular.ttf"));
            mChart.setData(pieData);
            // undo all highlights and refresh
            mChart.highlightValues(null);
            mChart.invalidate();

            // Update expense cursor adapter with new data cursor
            mExpenseCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        if (loader.getId() == PERSON_LOADER) {
            mCursorAdapter.swapCursor(null);
        } else {
            mExpenseCursorAdapter.swapCursor(null);
        }
    }

    private void initializeCalendar() {
        // Initialize the calendar within this month
        Date today = new Date();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        mDateView.init(today, tomorrow.getTime())
                .withSelectedDate(today);

        mDateView.clearHighlightedDates();

        selectedDates = mDateView.getSelectedDates();
    }
}