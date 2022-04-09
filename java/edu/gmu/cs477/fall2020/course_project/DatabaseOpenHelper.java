package edu.gmu.cs477.fall2020.course_project;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
    final private static String CREATE_CMD_TBL1=
            "CREATE TABLE tblFoods (" + MainActivity._ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MainActivity.F_NAME + " TEXT NOT NULL, "
                    + MainActivity.F_BRAND + " TEXT, "
                    + MainActivity.F_CAL + " INTEGER, "
                    + MainActivity.F_SERVING + " TEXT) ";

    final private static String CREATE_CMB_TBL2 =
            "CREATE TABLE tblLoggedFoods (" + MainActivity.LOGGED_ID +
                    " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + MainActivity.FOOD_ID + " INTEGER, "
                    + MainActivity.MONTH_ID + " INTEGER, "
                    + MainActivity.DAYOFMONTH + " INTEGER, "
                    + MainActivity.YEAR + " TEXT, "
                    + MainActivity.MEALTYPE + " INTEGER, "
                    + MainActivity.NOTES + " TEXT) ";

    final public static String TBL1_NAME = "tblFoods";
    final public static String TBL2_NAME = "tblLoggedFoods";
    final public static String DATABASE_NAME = "food_database";
    final private static Integer VERSION = 2;
    final private Context context;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_CMD_TBL1);
        ContentValues values = new ContentValues();

        values.put(MainActivity.F_NAME, "Yogurt");
        values.put(MainActivity.F_BRAND, "Activia Light");
        values.put(MainActivity.F_CAL, 70);
        values.put(MainActivity.F_SERVING, "1 container");
        db.insert(MainActivity.TABLE_FOOD, null, values);

        values.clear();
        values.put(MainActivity.F_NAME, "Tangerine");
        values.put(MainActivity.F_BRAND, "None");
        values.put(MainActivity.F_CAL, 38);
        values.put(MainActivity.F_SERVING, "3 oz");
        db.insert(MainActivity.TABLE_FOOD, null, values);

        values.clear();
        values.put(MainActivity.F_NAME, "Italian Ice");
        values.put(MainActivity.F_BRAND, "Lindy's");
        values.put(MainActivity.F_CAL, 200);
        values.put(MainActivity.F_SERVING, "2 cups");
        db.insert(MainActivity.TABLE_FOOD, null, values);

        values.clear();
        values.put(MainActivity.F_NAME, "Peanut Butter");
        values.put(MainActivity.F_BRAND, "None");
        values.put(MainActivity.F_CAL, 189);
        values.put(MainActivity.F_SERVING, "2 tbsp");
        db.insert(MainActivity.TABLE_FOOD, null, values);

        values.clear();

        db.execSQL(CREATE_CMB_TBL2);
        values.put(MainActivity.FOOD_ID, 1);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 1);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 1);
        values.put(MainActivity.NOTES, "this is some info");
        db.insert(MainActivity.TABLE_LOGGED, null, values);

        values.clear();
        values.put(MainActivity.FOOD_ID, 2);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 1);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 2);
        values.put(MainActivity.NOTES, "more stuff maybe");
        db.insert(MainActivity.TABLE_LOGGED, null, values);

        values.clear();
        values.put(MainActivity.FOOD_ID, 3);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 2);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 1);
        values.put(MainActivity.NOTES, " oo oo good food");
        db.insert(MainActivity.TABLE_LOGGED, null, values);

        values.clear();
        values.put(MainActivity.FOOD_ID, 4);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 2);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 2);
        values.put(MainActivity.NOTES, "yeehaw that was good");
        db.insert(MainActivity.TABLE_LOGGED, null, values);
        values.clear();
        values.put(MainActivity.FOOD_ID, 1);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 3);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 1);
        values.put(MainActivity.NOTES, "delicious");
        db.insert(MainActivity.TABLE_LOGGED, null, values);

        values.clear();
        values.put(MainActivity.FOOD_ID, 4);
        values.put(MainActivity.MONTH_ID, 12);
        values.put(MainActivity.DAYOFMONTH, 3);
        values.put(MainActivity.YEAR, "2020");
        values.put(MainActivity.MEALTYPE, 2);
        values.put(MainActivity.NOTES, "must make again");
        db.insert(MainActivity.TABLE_LOGGED, null, values);
        values.clear();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ TBL1_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ TBL2_NAME);
        onCreate(db);


    }

    void deleteDatabase () {

        context.deleteDatabase(DATABASE_NAME);
    }
}
