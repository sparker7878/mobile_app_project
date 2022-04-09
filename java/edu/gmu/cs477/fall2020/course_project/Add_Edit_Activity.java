package edu.gmu.cs477.fall2020.course_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CheckBox;

import java.util.Calendar;

import static edu.gmu.cs477.fall2020.course_project.MainActivity.BREAKFAST;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.DAYOFMONTH;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.DINNER;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_BRAND;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_CAL;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_NAME;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.F_SERVING;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.LUNCH;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.MEALTYPE;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.MONTH_ID;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.NOTES;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.SNACK;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.YEAR;
import static edu.gmu.cs477.fall2020.course_project.MainActivity._ID;
import static edu.gmu.cs477.fall2020.course_project.MainActivity.LOGGED_ID;

public class Add_Edit_Activity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private TextView mDisplayDate;
    private TextView title;
    private Button btnEditDate;
    private Button btnCreate_Save;
    private int requestType;
    private ImageView display_img; //for displaying the image
    //private Button camera_open; //for opening the camera
    static final int REQUEST_IMAGE_CAPTURE = 1;

    int currentFoodID;

    int selectedDay;
    int selectedMonth;
    int selectedYear;
    int selectedMealType;

    EditText inputName;
    EditText inputBrand;
    EditText inputCalories;
    EditText inputServings;
    EditText inputNotes;

    RadioButton rbBreakfast;
    RadioButton rbLunch;
    RadioButton rbDinner;
    RadioButton rbSnack;

    int changeAll = 2; //default to 2 - change a single entry

    String orgfName;
    String orgfBrand;
    String orgfCals;
    String orgfServing;

    int selectedLogEntryId;

    CheckBox checked;
    TextView txtCheckPrompt;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__edit_);

        inputName = (EditText) findViewById(R.id.etInputName);
        inputBrand = (EditText) findViewById(R.id.etInputBrand);
        inputCalories = (EditText) findViewById(R.id.etInputCalories);
        inputServings = (EditText) findViewById(R.id.etInputServing);
        inputNotes = (EditText) findViewById(R.id.etInputNotes);

        mDisplayDate = (TextView) findViewById(R.id.txtDate);
        title = (TextView) findViewById(R.id.txtTitleAdd);
        btnEditDate = findViewById(R.id.btnEditDate);
        btnCreate_Save = findViewById(R.id.btnCreateSave);

        rbBreakfast = (RadioButton) findViewById(R.id.rbBreakfast);
        rbLunch = (RadioButton) findViewById(R.id.rbLunch);
        rbDinner= (RadioButton) findViewById(R.id.rbDinner);
        rbSnack = (RadioButton) findViewById(R.id.rbSnack);
        //this was added
        txtCheckPrompt = (TextView) findViewById(R.id.txtCheckPrompt);
        checked = (CheckBox) findViewById(R.id.checkbox);
        checked.setChecked(false);

        //camera_open = (Button) findViewById(R.id.takePicture); //getting component by ID
        display_img = (ImageView) findViewById(R.id.imageView); //getting component by ID

        //camera_open button opens the camera; added the setOnClickListener in this button
        display_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { open(); }
        });

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Intent intent = getIntent();
        requestType = intent.getIntExtra(MainActivity.REQUEST_TYPE, MainActivity.ADD);
        selectedDay = intent.getIntExtra(MainActivity.SELECTED_DAY, day);
        selectedMonth = intent.getIntExtra(MainActivity.SELECTED_MONTH, month);
        selectedYear = intent.getIntExtra(MainActivity.SELECTED_YEAR, year);
        //put the page in create mode
        btnCreate_Save.setText("CREATE");
        title.setText("Add New");
        //user does not need to see this since it pertains to edit
        txtCheckPrompt.setVisibility(View.GONE);
        checked.setVisibility(View.GONE);

        mDisplayDate.setText(selectedMonth + "/" + selectedDay + "/" + selectedYear);
        if (requestType == MainActivity.EDIT){
            //put page in edit mode
            btnCreate_Save.setText("SAVE CHANGES");
            title.setText("Edit Food Item");
            txtCheckPrompt.setVisibility(View.VISIBLE);
            checked.setVisibility(View.VISIBLE);

            currentFoodID = intent.getExtras().getInt(_ID, -1);
            if(currentFoodID != -1) {
                selectedLogEntryId = intent.getExtras().getInt(LOGGED_ID);
                String fName = intent.getExtras().getString(F_NAME);
                orgfName = fName;
                String fBrand = intent.getExtras().getString(F_BRAND);
                orgfBrand = fBrand;
                String fCal = intent.getExtras().getString(F_CAL);
                orgfCals = fCal;
                String fServings = intent.getExtras().getString(F_SERVING);
                orgfServing = fServings;
                //this was changed
                selectedMealType = intent.getExtras().getInt(MEALTYPE);
                System.out.println("we got this meal type: " + selectedMealType);
                String mNotes = intent.getExtras().getString(NOTES);
                //set the edit texts
                inputName.setText(fName);
                inputBrand.setText(fBrand);
                inputCalories.setText(fCal);
                inputServings.setText(fServings);
                inputNotes.setText(mNotes);

                switch (selectedMealType) {
                    case BREAKFAST:
                        rbBreakfast.setChecked(true);
                        break;

                    case LUNCH:
                        rbLunch.setChecked(true);
                        break;

                    case DINNER:
                        rbDinner.setChecked(true);
                        break;

                    case SNACK:
                        rbSnack.setChecked(true);
                        break;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Error Loading Item", Toast.LENGTH_SHORT).show();
                setResult(Activity.RESULT_CANCELED);
                finish();
            }

        }

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

    public void onRadioButtonClicked(View v) {
        boolean checked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.rbBreakfast:
                if (checked)
                    selectedMealType = MainActivity.BREAKFAST;
                    break;
            case R.id.rbLunch:
                if (checked)
                    selectedMealType = LUNCH;
                    break;
            case R.id.rbDinner:
                if (checked)
                    selectedMealType = DINNER;
                    break;
            case R.id.rbSnack:
                if (checked)
                    selectedMealType = SNACK;
                    break;
        }
    }

    public void changeDate(View view){
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                Add_Edit_Activity.this,
                android.R.style.Theme_DeviceDefault_Light_Dialog,
                mDateSetListener,
                selectedYear, selectedMonth - 1, selectedDay);
        dialog.show();
    }

    public void onCancel(View v){
        onBackPressed();
    }

    @Override
    public void onBackPressed(){
        setResult(Activity.RESULT_CANCELED);
        finish();
    }

    public void onSave(View v){
            String fName = inputName.getText() != null ? inputName.getText().toString() : "";
            if (fName.equals("")) {
                //show error message
                Toast.makeText(getApplicationContext(), "Name is required", Toast.LENGTH_SHORT).show();
                //return
                return;
            }

            int fCalories = inputCalories.getText().toString().equals("") ? 0 : Integer.parseInt(inputCalories.getText().toString());
            String fBrand = inputBrand.getText().toString();
            String fServing = inputServings.getText().toString();
            String mNotes = inputNotes.getText().toString();

            Intent intent = new Intent();
            intent.putExtra(LOGGED_ID, selectedLogEntryId);
            intent.putExtra(_ID, currentFoodID);
            intent.putExtra(F_NAME, fName);
            intent.putExtra(F_BRAND, fBrand);
            intent.putExtra(F_CAL, fCalories);
            intent.putExtra(F_SERVING, fServing);
            intent.putExtra(MEALTYPE, selectedMealType);
            intent.putExtra(NOTES, mNotes);
            intent.putExtra(DAYOFMONTH, selectedDay);
            intent.putExtra(MONTH_ID, selectedMonth);
            intent.putExtra(YEAR, selectedYear);

            if (requestType == MainActivity.ADD) {

                //call addExercise
                intent.putExtra("requestType", MainActivity.ADD);
                setResult(Activity.RESULT_OK, intent);
                finish();
                //add check here that no duplicate names and that
            } else {

                setChangeAll(MainActivity.CHANGE_ALL_ENTRIES);
                //update entry
                if (!fName.equals(orgfName) || !fBrand.equals(orgfBrand) || !(fCalories == Integer.parseInt(orgfCals)) || !fServing.equals(orgfServing)) {
                    System.out.println("fName: " + fName + " != " + orgfName);
                    System.out.println("fBrand: " + fBrand + " != " + orgfBrand);
                    System.out.println("fCalories: " + fCalories + " != " + orgfCals);
                    System.out.println("fServing: " + fServing + " != " + orgfServing);

                    System.out.println("changed");
                    //prompt
                    if (checked.isChecked()){
                        setChangeAll(MainActivity.CHANGE_ALL_ENTRIES);
                    }else{
                        setChangeAll(MainActivity.CHANGE_SINGE_ENTRY);
                    }



                }
                intent.putExtra("updateSettings", changeAll);
                intent.putExtra("requestType", MainActivity.EDIT);
                setResult(Activity.RESULT_OK, intent);
                finish();




            }


            }

            public void setChangeAll(int ans) {
                changeAll = ans;

            }

    //this method will retrieves the image taken with the camera
    @Override
    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bp = (Bitmap) data.getExtras().get("data");
        display_img.setImageBitmap(bp);
    }

    public void open() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    }

