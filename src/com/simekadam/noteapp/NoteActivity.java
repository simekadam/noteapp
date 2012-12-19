package com.simekadam.noteapp;

import android.app.Activity;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingActivity;

import java.util.ArrayList;
import java.util.HashMap;

public class NoteActivity extends SlidingActivity implements GestureDetector.OnGestureListener {

    public static final String TAG = NoteActivity.class.getSimpleName();
    public DataSource database;
    public Cursor cursor;
    public Point size;
    public EditText notes;
    public int newNoteCursorIndex;
    public GestureDetector gestureDetector;
    public HashMap<Integer, Note> noteshashmap;
    public DataSetObserver observer;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setBehindContentView(R.layout.list);
        database = new DataSource(getApplicationContext());
        Display display = getWindowManager().getDefaultDisplay();
        gestureDetector = new GestureDetector(getApplicationContext(), this);
        size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        notes = (EditText) findViewById(R.id.note);
        notes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        database.open();

        ArrayList<Note> notesList = database.getAllNotes();
        noteshashmap = new HashMap<Integer, Note>();
        for (Note n : notesList)
        {
            noteshashmap.put(n.start, n);
            notes.append(n.text);
            notes.append("\n\n");
        }
        newNoteCursorIndex = notes.getSelectionStart();
        cursor = database.getAllNotesCursor();


        ListView sidebar = (ListView) findViewById(R.id.noteslist);
        sidebar.setAdapter(new NoteAdapter(getApplicationContext(), cursor, true));
        observer = new DataSetObserver() {
            @Override
            public void onChanged() {

                super.onChanged();    //To change body of overridden methods use File | Settings | File Templates.

            }
        };
        sidebar.getAdapter().registerDataSetObserver(observer);
        sidebar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView start = (TextView) view.findViewById(R.id.startIndex);
                int index = Integer.parseInt(start.getText().toString());
                notes.setSelection(index);
                getSlidingMenu().showContent();
            }
        });

        SlidingMenu menu = getSlidingMenu();
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setBehindOffset(200);
        menu.setShadowDrawable(R.drawable.defaultshadow);
        menu.setShadowWidth(10);
        menu.setFadeDegree(0.35f);

        setSlidingActionBarEnabled(false);



    }


    @Override
    public boolean onDown(MotionEvent e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onShowPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onLongPress(MotionEvent e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sync:
//                newGame();
                return true;
            case R.id.menu_addnote:
                saveNote();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("test", e1.getX()+" "+e2.getX());
        if(Math.abs(e1.getX() - e2.getX()) > size.x*0.8)
        {
            if(notes.getSelectionStart() < newNoteCursorIndex){return false;}
            saveNote();
        }
        return false;
    }


    public void saveNote()
    {
        Log.d(TAG, "saving note");
        String newNote = notes.getText().subSequence(newNoteCursorIndex, notes.getSelectionStart()).toString();
        ListView sidebar = (ListView) findViewById(R.id.noteslist);

        database.addNote(newNote, newNoteCursorIndex, notes.getSelectionStart());
        noteshashmap.put(newNoteCursorIndex, database.getLastNote());
        notes.append("\n\n");
        newNoteCursorIndex = notes.getSelectionStart();
        notes.setSelection(notes.getText().length());
        observer.onChanged();
//            sidebar.invalidate();
        cursor = database.getAllNotesCursor();
        sidebar.setAdapter(new NoteAdapter(getApplicationContext(), cursor, true));
        sidebar.invalidate();
        Toast toast = Toast.makeText(getApplicationContext(), "Note saved", 5);
        toast.show();
    }
}
