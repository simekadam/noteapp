package com.simekadam.noteapp;

import android.content.Context;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/18/12
 * Time: 3:30 PM
 * To change this template use File | Settings | File Templates.
 */



public class NoteAdapter extends CursorAdapter {

    private Context context;
    private Cursor cursor;
    private LayoutInflater layoutInflater;

    public NoteAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.context = context;
        this.cursor = cursor;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {

        View view = layoutInflater.inflate(R.layout.note, viewGroup, false);
        bindView(view , context, cursor);

        return view;

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView time = (TextView) view.findViewById(R.id.noteTime);
        TextView title = (TextView) view.findViewById(R.id.noteTitle);
        TextView start = (TextView) view.findViewById(R.id.startIndex);
//        editor.setScroller(new Scroller(context));
//        editor.setHeight(50);
//        editor.setVerticalScrollBarEnabled(true);
//        editor.setMovementMethod(new ScrollingMovementMethod());
        time.setText((getDate(cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TIMESTAMP)))));
        String titleText = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TEXT));
        titleText = titleText.trim();
         if(titleText.length()>20) titleText = titleText.substring(0, 18)+"..";
        title.setText(titleText);
        start.setText(String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_ID))));


    }

    private String getDate(String timeStamp){
        DateFormat objFormatter = new SimpleDateFormat("dd-MM-yyyy");
        objFormatter.setTimeZone(TimeZone.getDefault());

//        Calendar objCalendar =
//                Calendar.getInstance(TimeZone.getDefault());
//        objFormatter.format(timeStamp);
//        String result = objFormatter.format(objCalendar.getTime());
//        objCalendar.clear();
        return timeStamp;
    }
}
