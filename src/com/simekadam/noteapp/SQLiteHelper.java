package com.simekadam.noteapp;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/18/12
 * Time: 3:22 PM
 * To change this template use File | Settings | File Templates.
 */
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_NOTES = "notes";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TEXT = "text";
    public static final String COLUMN_TIMESTAMP = "timestamp";
    public static final String COLUMN_START = "start";
    public static final String COLUMN_END = "end";

    private static final String DATABASE_NAME = "notesapp.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_NOTES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TEXT
            + " text not null, "+COLUMN_TIMESTAMP
            + " datetime not null, "+ COLUMN_START
            + " integer not null, "+ COLUMN_END + " integer not null); ";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTES);
        onCreate(db);
    }

}