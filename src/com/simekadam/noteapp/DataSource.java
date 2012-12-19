package com.simekadam.noteapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/18/12
 * Time: 5:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataSource  {

    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_TEXT,
            SQLiteHelper.COLUMN_TIMESTAMP, SQLiteHelper.COLUMN_ID, SQLiteHelper.COLUMN_START, SQLiteHelper.COLUMN_END };


    public DataSource(Context context) {
        dbHelper = new SQLiteHelper(context);

    }

    public void open()
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }

    public Note addNote(String note, int start, int end)
    {
        ContentValues cv = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());
        cv.put(SQLiteHelper.COLUMN_TEXT, note);
        cv.put(SQLiteHelper.COLUMN_TIMESTAMP, strDate);
        cv.put(SQLiteHelper.COLUMN_START, start);
        cv.put(SQLiteHelper.COLUMN_END, end);
        long insertId = database.insert(SQLiteHelper.TABLE_NOTES, null,
                cv);
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        return  cursorConverter(cursor);
    }

    public Note getNote(int id)
    {
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + id, null,
                null, null, null);
        cursor.moveToFirst();
        return  cursorConverter(cursor);
    }

    public Note changeNote(int id, String note, int start, int end)
    {
        ContentValues cv = new ContentValues();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(new Date());

        ContentValues values = new ContentValues();
        cv.put(SQLiteHelper.COLUMN_TEXT, note);
        cv.put(SQLiteHelper.COLUMN_TIMESTAMP, strDate);
        cv.put(SQLiteHelper.COLUMN_START, start);
        cv.put(SQLiteHelper.COLUMN_END, end);
        long updateId = database.update(SQLiteHelper.TABLE_NOTES, cv, SQLiteHelper.COLUMN_ID+"="+id, null);
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + updateId, null,
                null, null, null);
        cursor.moveToFirst();
        return  cursorConverter(cursor);
    }

    public ArrayList<Note> getAllNotes()
    {
        ArrayList<Note> notes = new ArrayList<Note>();

        Cursor cursor = getAllNotesCursor();
        while(!cursor.isAfterLast())
        {
            notes.add(cursorConverter(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return  notes;
    }

    public Cursor getAllNotesCursor()
    {
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTES,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        return cursor;
    }

    public Note getLastNote()
    {
        Cursor cursor = database.query(SQLiteHelper.TABLE_NOTES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + "(SELECT MAX(_id) FROM "+SQLiteHelper.TABLE_NOTES+")", null,
                null, null, null);
        cursor.moveToFirst();
        return  cursorConverter(cursor);
    }

    public void deleteNote(int noteID)
    {
         database.delete(SQLiteHelper.TABLE_NOTES, "_id="+noteID, null);
    }



    private Note cursorConverter(Cursor cursor)
    {
        Note note = new Note();
        note.id = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_ID));
        note.text = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TEXT));
        note.timestamp = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TIMESTAMP));
        note.start = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_START));
        note.end = cursor.getInt(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_END));
        return note;
    }
}
