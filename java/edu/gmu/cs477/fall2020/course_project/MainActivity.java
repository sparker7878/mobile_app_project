package edu.gmu.cs477.fall2020.course_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.SearchView;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;


public class MainActivity extends AppCompatActivity {
    TextView txtDisplayDate;
    TextView txtTotalCals;
    CalendarView calendar;
    int curDay;
    int curMonth;
    int curYear;

    public final static String NAME_LIST = "edu.gmu.cs477.mainActivity.NAME_LIST";
    public final static String REQUEST_TYPE = "edu.gmu.cs477.mainActivity.REQUEST_TYPE";
    public final static int ACTIVITY_RESULT = 1;

    public final static int ADD = 0;
    public final static int EDIT = 1;

    public final static int CHANGE_ALL_ENTRIES = 1;
    public final static int CHANGE_SINGE_ENTRY = 2;

    public final static int BREAKFAST = 1;
    public final static int LUNCH = 2;
    public final static int DINNER = 3;
    public final static int SNACK = 4;

    public final static String SELECTED_DAY = "edu.gmu.cs477.mainActivity.SELECTED_DAY";
    public final static String SELECTED_MONTH = "edu.gmu.cs477.mainActivity.SELECTED_MONTH";
    public final static String SELECTED_YEAR = "edu.gmu.cs477.mainActivity.SELECTED_YEAR";

    private ListView foodList;
    SimpleCursorAdapter adapter;

    //removed some stuff here

    protected SQLiteDatabase db = null;
    DatabaseOpenHelper dbHelper = null;
    Cursor c;


    final static String TABLE_FOOD = "tblFoods";
    final static String F_NAME=  "name";
    final static String _ID = "_id";
    final static String F_BRAND = "brand";
    final static String F_CAL = "calories";
    final static String F_SERVING = "serving";
    //put the photo here
    final static String[] tblFoodColumns = {_ID, F_NAME, F_BRAND, F_CAL, F_SERVING};

    final static String TABLE_LOGGED = "tblLoggedFoods";
    final static String LOGGED_ID = "loggedID";
    final static String FOOD_ID =  "foodID";
    final static String MONTH_ID= "monthID";
    final static String YEAR = "year";
    final static String DAYOFMONTH= "dayOfMonth";
    final static String NOTES = "notes";
    final static String MEALTYPE = "mealType";
    //put the photo here
    final static String[] tblLoggedColumns = {LOGGED_ID, FOOD_ID, MONTH_ID, YEAR, DAYOFMONTH, NOTES, MEALTYPE};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        txtDisplayDate = (TextView)(findViewById(R.id.txtDisplayDate));
        txtTotalCals = (TextView) (findViewById(R.id.txtTotalCals));
        dbHelper = new DatabaseOpenHelper(this);

        //get the current date to display
        Calendar rightNow = Calendar.getInstance();
        curDay = rightNow.get(Calendar.DAY_OF_MONTH);
        curMonth = rightNow.get(Calendar.MONTH) + 1;
        curYear = rightNow.get(Calendar.YEAR);
        String currentDate = curMonth + "/" + curDay + "/" + curYear;


        txtDisplayDate.setText("Food Logged for " + currentDate);
        txtTotalCals.setText("Total Calories: " + getCalories());

        calendar = (CalendarView)(findViewById(R.id.calendarView));
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                curYear = year;
                curMonth = month + 1;
                curDay = dayOfMonth;

                txtDisplayDate.setText("Food Logged for " + curMonth + "/" + curDay + "/" + curYear);
                txtTotalCals.setText("Total Calories: " + getCalories());

                LoadDB task = new LoadDB();
                task.execute();
            }
        });



        foodList = (ListView) findViewById(R.id.allFoodList);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get database
                db = dbHelper.getWritableDatabase();

                //get cursor for position
                Cursor q = (Cursor) parent.getAdapter().getItem(position);
                int fId = q.getInt(q.getColumnIndex(_ID));
                int loggedId = q.getInt(q.getColumnIndex(LOGGED_ID));
                String fName = q.getString(q.getColumnIndex(F_NAME));
                String fBrand = q.getString(q.getColumnIndex(F_BRAND));
                String fCal = q.getString(q.getColumnIndex(F_CAL));
                String fServing = q.getString(q.getColumnIndex(F_SERVING));
                String mNotes = q.getString(q.getColumnIndex(NOTES));
                int mMealType = q.getInt(q.getColumnIndex(MEALTYPE));

                Intent intent = new Intent(getApplicationContext(), Add_Edit_Activity.class);
                intent.putExtra(_ID,fId);
                intent.putExtra(LOGGED_ID,loggedId);
                intent.putExtra(F_NAME, fName);
                intent.putExtra(F_BRAND, fBrand);
                intent.putExtra(F_CAL, fCal);
                intent.putExtra(F_SERVING, fServing);
                intent.putExtra(NOTES, mNotes);
                intent.putExtra(MEALTYPE, mMealType);
                intent.putExtra(REQUEST_TYPE, MainActivity.EDIT);
                intent.putExtra(SELECTED_DAY, curDay);
                intent.putExtra(SELECTED_YEAR, curYear);
                intent.putExtra(SELECTED_MONTH, curMonth);
                startActivityForResult(intent, ACTIVITY_RESULT);


            };
        });
        foodList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                //get item name that was clicked
                Cursor q = (Cursor) parent.getAdapter().getItem(position);
                int loggedId = q.getInt(q.getColumnIndex(LOGGED_ID));

                //get database
                db = dbHelper.getWritableDatabase();

                //delete foodLogged entry
                db.delete(MainActivity.TABLE_LOGGED,LOGGED_ID + " =?", new String[]{String.valueOf(loggedId)});

                String MY_QUERY = "SELECT * FROM tblFoods a INNER JOIN tblLoggedFoods b ON a._id=b.foodID WHERE monthID = " + curMonth  + " AND dayOFMonth = " + curDay + " AND year = " + curYear;
                c = db.rawQuery(MY_QUERY, new String[]{});
                adapter.swapCursor(c);
                txtTotalCals.setText("Total Calories: " + getCalories());
                return true;
            }
        });

    }

    public String getCalories(){
        db = dbHelper.getWritableDatabase();
        String calorie_amount ="";
        String MY_QUERY = "SELECT sum(calories) as daily_calories FROM tblFoods a INNER JOIN tblLoggedFoods b ON a._id=b.foodID WHERE monthID = " + curMonth  + " AND dayOFMonth = " + curDay + " AND year = " + curYear;
        Cursor cursor = db.rawQuery(MY_QUERY, new String[]{});
        if(cursor==null || cursor.getCount()==0){
            calorie_amount="0";
        }
        else {
            if (cursor.moveToFirst()) {
                calorie_amount = cursor.getString(cursor.getColumnIndex("daily_calories"));
                if(calorie_amount==null){
                    calorie_amount="0";
                }
            }
        }
        cursor.close();

        return calorie_amount;
    }

    public void onAddNew(View v){//go to new food page
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.prompt_on_add, null);
        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this, R.style.AlertDialog);
        alertdialog.setView(promptView);
        alertdialog.setCancelable(true)
                .setNegativeButton("Create New Entry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, Add_Edit_Activity.class);
                        intent.putExtra(REQUEST_TYPE, ADD);
                        intent.putExtra(SELECTED_DAY, curDay);
                        intent.putExtra(SELECTED_YEAR, curYear);
                        intent.putExtra(SELECTED_MONTH, curMonth);
                        startActivityForResult(intent, ACTIVITY_RESULT);
                    }
                })
                .setPositiveButton("Quick Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, Quick_Add_List_Activity.class);
                        intent.putExtra(SELECTED_DAY, curDay);
                        intent.putExtra(SELECTED_YEAR, curYear);
                        intent.putExtra(SELECTED_MONTH, curMonth);
                        startActivityForResult(intent, ACTIVITY_RESULT);
                    }
                });
        alertdialog.create();
        alertdialog.show();
    }


    public void onResume(){
        super.onResume();
        //use Async Task
        LoadDB task = new LoadDB();
        task.execute();
        txtTotalCals.setText("Total Calories: " + getCalories());

    }

    private final class LoadDB extends AsyncTask<String, Void, Cursor> {

        // runs on the UI thread
        @Override protected void onPostExecute(Cursor data) {
            adapter = new SimpleCursorAdapter(getApplicationContext(),
                    android.R.layout.simple_list_item_1,
                    data,
                    new String[] { "name" },
                    new int[] {android.R.id.text1},0);
            c = data;
            foodList.setAdapter(adapter);
        }
        // runs on its own thread
        @Override
        protected Cursor doInBackground(String... args) {
            db = dbHelper.getWritableDatabase();

            String MY_QUERY = "SELECT * FROM tblFoods a INNER JOIN tblLoggedFoods b ON a._id=b.foodID WHERE monthID = " + curMonth  + " AND dayOFMonth = " + curDay + " AND year = " + curYear;
            return db.rawQuery(MY_QUERY, new String[]{});
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ACTIVITY_RESULT && resultCode != Activity.RESULT_CANCELED) {

            String fName = data.getExtras().getString(F_NAME);
            String fBrand = data.getExtras().getString(F_BRAND);
            int fCal = data.getExtras().getInt(F_CAL);
            String fServing = data.getExtras().getString(F_SERVING);
            int selectedLogEntryId = data.getExtras().getInt(LOGGED_ID);
            String logID = String.valueOf(selectedLogEntryId);
            int selectedFoodId = data.getExtras().getInt(_ID);
            String foodID = String.valueOf(selectedFoodId);
            //this was changed
            int mealType = data.getExtras().getInt(MEALTYPE);
            System.out.println("we got this meal type after save: " + mealType);
            String mNotes = data.getExtras().getString(NOTES);
            int mDay = data.getExtras().getInt(DAYOFMONTH, 0);
            int mMonth = data.getExtras().getInt(MONTH_ID, 0);
            String year = Integer.toString(data.getExtras().getInt(YEAR));


            if(data.getExtras().getInt("requestType") == MainActivity.ADD){
                ContentValues values = new ContentValues();
                values.put(MainActivity.F_NAME, fName);
                values.put(MainActivity.F_BRAND, fBrand);
                values.put(MainActivity.F_CAL, fCal);
                values.put(MainActivity.F_SERVING, fServing);
                long foodId = db.insert(MainActivity.TABLE_FOOD, null, values);


                values.clear();
                values.put(MainActivity.FOOD_ID, (int)foodId);
                values.put(MainActivity.MONTH_ID, mMonth);
                values.put(MainActivity.DAYOFMONTH, mDay);
                values.put(MainActivity.YEAR, year);
                values.put(MainActivity.MEALTYPE, mealType);
                values.put(MainActivity.NOTES, mNotes);
                db.insert(MainActivity.TABLE_LOGGED, null, values);

            }else if (data.getExtras().getInt("requestType") == MainActivity.EDIT){
                if(data.getExtras().getInt("updateSettings") == MainActivity.CHANGE_SINGE_ENTRY){
                    //create new entry for that food
                    ContentValues values = new ContentValues();
                    values.put(MainActivity.F_NAME, fName);
                    values.put(MainActivity.F_BRAND, fBrand);
                    values.put(MainActivity.F_CAL, fCal);
                    values.put(MainActivity.F_SERVING, fServing);
                    long foodId = db.insert(MainActivity.TABLE_FOOD, null, values);

                    //delete foodLogged entry
                    db.delete(MainActivity.TABLE_LOGGED,LOGGED_ID + " =?", new String[]{logID});
                    //create new one with
                    values.clear();
                    values.put(MainActivity.FOOD_ID, (int)foodId);
                    values.put(MainActivity.MONTH_ID, mMonth);
                    values.put(MainActivity.DAYOFMONTH, mDay);
                    values.put(MainActivity.YEAR, year);
                    values.put(MainActivity.MEALTYPE, mealType);
                    values.put(MainActivity.NOTES, mNotes);
                    db.insert(MainActivity.TABLE_LOGGED, null, values);

                }else if (data.getExtras().getInt("updateSettings") == MainActivity.CHANGE_ALL_ENTRIES){
                    //update foodTable with new data
                    ContentValues cv = new ContentValues();
                    cv.put(MainActivity.F_NAME, fName);
                    cv.put(MainActivity.F_BRAND, fBrand);
                    cv.put(MainActivity.F_CAL, fCal);
                    cv.put(MainActivity.F_SERVING, fServing);

                    db.update(MainActivity.TABLE_FOOD, cv, _ID + " =?", new String[]{foodID});

                    cv.clear();
                    cv.put(MainActivity.MONTH_ID, mMonth);
                    cv.put(MainActivity.DAYOFMONTH, mDay);
                    cv.put(MainActivity.YEAR, year);
                    cv.put(MainActivity.MEALTYPE, mealType);
                    cv.put(MainActivity.NOTES, mNotes);
                    //update loggedTable with any data
                    db.update(MainActivity.TABLE_LOGGED, cv, LOGGED_ID + " =?", new String[]{logID});

                }
            }
            LoadDB task = new LoadDB();
            task.execute();
            txtTotalCals.setText("Total Calories: " + getCalories());


        }

    }

}
