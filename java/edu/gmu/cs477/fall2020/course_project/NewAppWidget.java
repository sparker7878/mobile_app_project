package edu.gmu.cs477.fall2020.course_project;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        String calorie_amount="0";
        DatabaseOpenHelper dbHelper= new DatabaseOpenHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Calendar rightNow = Calendar.getInstance();
        int day = rightNow.get(Calendar.DAY_OF_MONTH);
        int month = rightNow.get(Calendar.MONTH) + 1;
        int year = rightNow.get(Calendar.YEAR);
        String MY_QUERY = "SELECT sum(calories) as daily_calories FROM tblFoods a INNER JOIN tblLoggedFoods b ON a._id=b.foodID WHERE monthID = " + month  + " AND dayOFMonth = " + day + " AND year = " + year;
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
        db.close();
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.calorie_label,"Calorie Count for "+Integer.toString(month)+"/"+Integer.toString(day)+"/"+Integer.toString(year)+": "+calorie_amount);
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

