package com.simekadam.noteapp;

import java.sql.Timestamp;

/**
 * Created with IntelliJ IDEA.
 * User: simekadam
 * Date: 12/18/12
 * Time: 3:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class Note {

    public String text;
    public String timestamp;
    public int id;
    public int start;
    public int end;

    public Note(){}

    public Note(String text, String timestamp, int id, int start, int end) {
        this.text = text;
        this.timestamp = timestamp;
        this.id = id;
        this.start = start;
        this.end = end;
    }



}
