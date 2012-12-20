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

public class NoteActivity extends SlidingActivity implements GestureDetector.OnGestureListener, SyncCompleteListener {

    public static final String TAG = NoteActivity.class.getSimpleName();
    public DataSource database;
    public Cursor cursor;
    public Point size;
    public EditText notePanel;
    public int newNoteCursorIndex;
    public GestureDetector gestureDetector;
    public HashMap<Integer, Note> noteshashmap;
    public DataSetObserver observer;
    public int currentNoteId;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
//        return super.onCreateOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onRestart() {
        super.onRestart();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onResume() {
        database.open();
        super.onResume();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onPause() {
        database.close();
        super.onPause();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onStop() {
        database.close();
        super.onStop();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    protected void onDestroy() {
        database.close();
        super.onDestroy();    //To change body of overridden methods use File | Settings | File Templates.
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
        notePanel = (EditText) findViewById(R.id.note);
        notePanel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        });

        notePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView view = (TextView) v;
                if(!view.isEnabled())    toggleEditor();
            }
        });

        currentNoteId = -1;
        database.open();


        newNoteCursorIndex = notePanel.getSelectionStart();
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
                notePanel.setText((loadNote(index)).text);
                currentNoteId = index;
                getSlidingMenu().showContent();
            }
        });


        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        sidebar,
                        new SwipeDismissListViewTouchListener.OnDismissCallback() {
                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                    for(int index : reverseSortedPositions)
                                    {
                                        View view = listView.getChildAt(index);
                                        TextView textView = (TextView) view.findViewById(R.id.startIndex);
                                        int deletedId = Integer.parseInt(textView.getText().toString());
                                        database.deleteNote(deletedId);
                                        ListView sidebar = (ListView) findViewById(R.id.noteslist);
                                        cursor = database.getAllNotesCursor();
                                        sidebar.setAdapter(new NoteAdapter(getApplicationContext(), cursor, true));
                                        sidebar.invalidate();
                                        if(currentNoteId == deletedId){
                                            currentNoteId = -1;
                                            notePanel.setText("");
                                        }
                                    }
                            }
                        });
        sidebar.setOnTouchListener(touchListener);
        // Setting this scroll listener is required to ensure that during ListView scrolling,
        // we don't look for swipes.
        sidebar.setOnScrollListener(touchListener.makeScrollListener());

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
                sync();
                return true;
            case R.id.menu_addnote:
                saveNote();
                return true;
            case R.id.menu_edit:
                toggleEditor();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sync()
    {
        Connection connection = new Connection(getApplicationContext(), this);
        connection.downloadAllNotes();
    }


    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.d("test", e1.getX()+" "+e2.getX());
        if(Math.abs(e1.getX() - e2.getX()) > size.x*0.8)
        {
            saveNote();
        }
        return false;
    }


    public void saveNote()
    {
        if(notePanel.getText().length()==0){}
        else{


        if(currentNoteId == -1){

        Log.d(TAG, "saving note");
        String newNote = notePanel.getText().toString();


        database.addNote(newNote, 0, 0);
//        noteshashmap.put(newNoteCursorIndex, database.getLastNote());
        newNoteCursorIndex = notePanel.getSelectionStart();
//        notes.setSelection(notes.getText().length());
        observer.onChanged();
//            sidebar.invalidate();

        notePanel.setText("");
            currentNoteId = -1;
        Toast toast = Toast.makeText(getApplicationContext(), "Note saved", 5);
        toast.show();
        }
        else
        {
            Log.d("test", currentNoteId+"");
            database.changeNote(currentNoteId, notePanel.getText().toString(), 0 , 0);
            notePanel.setText("");
            currentNoteId = -1;
            Toast toast = Toast.makeText(getApplicationContext(), "Note updated", 5);
            toast.show();
        }
        ListView sidebar = (ListView) findViewById(R.id.noteslist);
        cursor = database.getAllNotesCursor();
        sidebar.setAdapter(new NoteAdapter(getApplicationContext(), cursor, true));
        sidebar.invalidate();
        }
    }

    public Note loadNote(int noteID)
    {
        return database.getNote(noteID);

    }


    public void toggleEditor()
    {
        if(notePanel.isEnabled()){
            notePanel.setEnabled(false);
        }
        else
        {
            notePanel.setEnabled(true);
        }

    }

    @Override
    public void onSyncComplete() {
        ListView sidebar = (ListView) findViewById(R.id.noteslist);
        cursor = database.getAllNotesCursor();
        sidebar.setAdapter(new NoteAdapter(getApplicationContext(), cursor, true));
        sidebar.invalidate();
    }
}
