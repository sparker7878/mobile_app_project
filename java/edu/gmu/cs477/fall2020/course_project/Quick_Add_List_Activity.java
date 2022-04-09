package edu.gmu.cs477.fall2020.course_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.Calendar;

import static edu.gmu.cs477.fall2020.course_project.MainActivity.ACTIVITY_RESULT;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.DINNER;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_BRAND;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_CAL;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_NAME;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_SERVING;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.LOGGED_ID;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.LUNCH;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.MEALTYPE;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.NOTES;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.REQUEST_TYPE;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.SELECTED_DAY;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.SELECTED_MONTH;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.SELECTED_YEAR;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.SNACK;
import static edu.gmu.cs477.fall2020.course_project.MainActivity._ID;

public class Quick_Add_List_Activity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mDisplayDate;

    private ListView foodList;
    private TextView txtQuickSelectedName;
    private EditText etQuickInputNotes;
    SimpleCursorAdapter adapter;

    protected SQLiteDatabase db = null;
    DatabaseOpenHelper dbHelper = null;
    Cursor c;

    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private Button btnEditDate;
    private Button btnCreate_Save;

    RadioButton rbBreakfast;
    RadioButton rbLunch;
    RadioButton rbDinner;
    RadioButton rbSnack;

    int selectedDay;
    int selectedMonth;
    int selectedYear;
    int selectedMealType;

    int selectedID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick__add__list_);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        txtQuickSelectedName = (TextView) findViewById(R.id.txtQuickSelectedName);
        etQuickInputNotes = (EditText) findViewById(R.id.etQuickInputNotes);

        Intent intent = getIntent();
        selectedDay = intent.getIntExtra(SELECTED_DAY, day);
        selectedMonth = intent.getIntExtra(SELECTED_MONTH, month);
        selectedYear = intent.getIntExtra(SELECTED_YEAR, year);

        mDisplayDate = (TextView) findViewById(R.id.txtQuickDate);
        mDisplayDate.setText(selectedMonth + "/" + selectedDay + "/" + selectedYear);

        btnEditDate = findViewById(R.id.btnQuickEditDate);
        btnCreate_Save = findViewById(R.id.btnQuickCreateSave);

        rbBreakfast = (RadioButton) findViewById(R.id.rbQuickBreakfast);
        rbLunch = (RadioButton) findViewById(R.id.rbQuickLunch);
        rbDinner= (RadioButton) findViewById(R.id.rbQuickDinner);
        rbSnack = (RadioButton) findViewById(R.id.rbQuickSnack);

        dbHelper = new DatabaseOpenHelper(this);

        foodList = (ListView) findViewById(R.id.QuickFoodList);

        foodList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //get database
                db = dbHelper.getWritableDatabase();

                //get cursor for position
                Cursor q = (Cursor) parent.getAdapter().getItem(position);
                selectedID = q.getInt(q.getColumnIndex(_ID));
                String fName = q.getString(q.getColumnIndex(F_NAME));
                String fCal = q.getString(q.getColumnIndex(F_CAL));
                String fServing = q.getString(q.getColumnIndex(F_SERVING));
                String fBrand = q.getString(q.getColumnIndex(F_BRAND));

                txtQuickSelectedName.setText(fName + "\nBrand: " + fBrand + " Serving: " + fServing +" Cal: "+ fCal );

            };
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener(){
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day){
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);

                String date = month  + "/" + day + "/" + year;
                mDisplayDate.setText(date);
                //this was added
                selectedDay = day;
                selectedMonth = month;
                selectedYear = year;
            }
        };
    }

    public void quickChangeDate(View view){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Quick_Add_List_Activity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDateSetListener,
                selectedYear, selectedMonth - 1, selectedDay);
        dialog.show();
    }
    public void onQuickRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.rbQuickBreakfast:
                if (checked)
                    selectedMealType = MainActivity.BREAKFAST;
                break;
            case R.id.rbQuickLunch:
                if (checked)
                    selectedMealType = LUNCH;
                break;
            case R.id.rbQuickDinner:
                if (checked)
                    selectedMealType = DINNER;
                break;
            case R.id.rbQuickSnack:
                if (checked)
                    selectedMealType = SNACK;
                break;
        }
    }

    public void onQuickCreate(View v){
        ContentValues values = new ContentValues();


        values.put(MainActivity.FOOD_ID, (int)selectedID);
        values.put(MainActivity.MONTH_ID, selectedMonth);
        values.put(MainActivity.DAYOFMONTH,selectedDay);
        values.put(MainActivity.YEAR, selectedYear);
        values.put(MainActivity.MEALTYPE,selectedMealType);
        values.put(MainActivity.NOTES, etQuickInputNotes.getText().toString());
        db.insert(MainActivity.TABLE_LOGGED, null, values);

        setResult(Activity.RESULT_CANCELED);
        finish();

    }


    public void onQuickCancel(View v){onBackPressed();
    }

    @Override
    public void onBackPressed(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onResume(){
        super.onResume();
        //use Async Task
        Quick_Add_List_Activity.LoadDB task = new Quick_Add_List_Activity.LoadDB();
        task.execute();
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

            String MY_QUERY = "SELECT * FROM tblFoods ORDER BY " + F_NAME;
            return db.rawQuery(MY_QUERY, new String[]{});
        }
    }
}
