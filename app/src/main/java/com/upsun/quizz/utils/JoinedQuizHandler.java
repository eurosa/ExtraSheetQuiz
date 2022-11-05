package com.upsun.quizz.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Developed by Binplus Technologies pvt. ltd.  on 12,July,2020
 */
public class JoinedQuizHandler extends SQLiteOpenHelper {
    private static String DB_NAME = "uq_db";
    private static int DB_VERSION = 3;
    private SQLiteDatabase db;

    public static final String TABLE_NAME = "join_tbl";
    public static final String COLUMN_ID = "join_id";
    public static final String COLUMN_CID = "c_id";
    public static final String COLUMN_QUIZ_ID = "quiz_id";
    public static final String COLUMN_DESCRIPTION = "description";
    public static final String COLUMN_ENTRY_FEE = "entry_fee";
    public static final String COLUMN_PARTICIPANTS = "participents";
    public static final String COLUMN_QUES = "questions";
    public static final String COLUMN_QUIZ_DATE = "quiz_date";
    public static final String COLUMN_END_TIME = "quiz_end_time";
    public static final String COLUMN_START_TIME = "quiz_start_time";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_INSTRUCTIONS = "instruction";
    public static final String COLUMN_LANG = "language";


    public JoinedQuizHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db=db;
        String exe = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME
                + "(" + COLUMN_CID + " integer primary key , "
                + COLUMN_ID + " TEXT NOT NULL,"
                + COLUMN_QUIZ_ID + " TEXT NOT NULL,"
                + COLUMN_DESCRIPTION + " TEXT NOT NULL,"
                + COLUMN_ENTRY_FEE + " TEXT NOT NULL,"
                + COLUMN_PARTICIPANTS + " TEXT NOT NULL,"
                + COLUMN_QUES + " TEXT NOT NULL,"
                + COLUMN_QUIZ_DATE + " TEXT NOT NULL,"
                + COLUMN_END_TIME + " TEXT NOT NULL,"
                + COLUMN_START_TIME + " TEXT NOT NULL,"
                + COLUMN_TITLE + " TEXT NOT NULL,"
                + COLUMN_INSTRUCTIONS + " TEXT NOT NULL,"
                + COLUMN_LANG + " TEXT NOT NULL "
                + ")";

        db.execSQL(exe);

    }
    public boolean isAlreadyJoin(String id) {
        db = getReadableDatabase();
        String qry = "Select *  from " + TABLE_NAME + " where " + COLUMN_QUIZ_ID + " = '" + id +"'";
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        if (cursor.getCount() > 0) return true;
        return false;
    }

    public int getJoinedQuizCount() {
        db = getReadableDatabase();
        String qry = "Select *  from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);
        return cursor.getCount();
    }

    public boolean setJoinQuiz(HashMap<String, String> map) {
        db = getWritableDatabase();
        if (isAlreadyJoin(map.get(COLUMN_QUIZ_ID))) {
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COLUMN_CID, map.get(COLUMN_CID));
            values.put(COLUMN_ID, map.get(COLUMN_ID));
            values.put(COLUMN_QUIZ_ID, map.get(COLUMN_QUIZ_ID));
            values.put(COLUMN_DESCRIPTION, map.get(COLUMN_DESCRIPTION));
            values.put(COLUMN_ENTRY_FEE, map.get(COLUMN_ENTRY_FEE));
            values.put(COLUMN_PARTICIPANTS, map.get(COLUMN_PARTICIPANTS));
            values.put(COLUMN_QUES, map.get(COLUMN_QUES));
            values.put(COLUMN_QUIZ_DATE, map.get(COLUMN_QUIZ_DATE));
            values.put(COLUMN_END_TIME, map.get(COLUMN_END_TIME));
            values.put(COLUMN_START_TIME, map.get(COLUMN_START_TIME));
            values.put(COLUMN_TITLE, map.get(COLUMN_TITLE));
            values.put(COLUMN_INSTRUCTIONS, map.get(COLUMN_INSTRUCTIONS));
            values.put(COLUMN_LANG, map.get(COLUMN_LANG));
            db.insert(TABLE_NAME, null, values);
            return true;
        }
    }



    public ArrayList<HashMap<String, String>> getAllJoinedQuiz() {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_QUIZ_ID, cursor.getString(cursor.getColumnIndex(COLUMN_QUIZ_ID)));
            map.put(COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            map.put(COLUMN_ENTRY_FEE, cursor.getString(cursor.getColumnIndex(COLUMN_ENTRY_FEE)));
            map.put(COLUMN_PARTICIPANTS, cursor.getString(cursor.getColumnIndex(COLUMN_PARTICIPANTS)));
            map.put(COLUMN_QUES, cursor.getString(cursor.getColumnIndex(COLUMN_QUES)));
            map.put(COLUMN_QUIZ_DATE, cursor.getString(cursor.getColumnIndex(COLUMN_QUIZ_DATE)));
            map.put(COLUMN_END_TIME, cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));
            map.put(COLUMN_START_TIME, cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
            map.put(COLUMN_INSTRUCTIONS, cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS)));
            map.put(COLUMN_LANG, cursor.getString(cursor.getColumnIndex(COLUMN_LANG)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            list.add(map);
            cursor.moveToNext();
        }
        return list;
    }

    public ArrayList<HashMap<String, String>> getSingleJoinedQuiz(String id) {
        ArrayList<HashMap<String, String>> list = new ArrayList<>();
        db = getReadableDatabase();
        String qry = "Select *  from " + TABLE_NAME+ " where " + COLUMN_QUIZ_ID + " = '" + id +"'";
        Cursor cursor = db.rawQuery(qry, null);
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++) {
            HashMap<String, String> map = new HashMap<>();
            map.put(COLUMN_CID, cursor.getString(cursor.getColumnIndex(COLUMN_CID)));
            map.put(COLUMN_ID, cursor.getString(cursor.getColumnIndex(COLUMN_ID)));
            map.put(COLUMN_QUIZ_ID, cursor.getString(cursor.getColumnIndex(COLUMN_QUIZ_ID)));
            map.put(COLUMN_DESCRIPTION, cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            map.put(COLUMN_ENTRY_FEE, cursor.getString(cursor.getColumnIndex(COLUMN_ENTRY_FEE)));
            map.put(COLUMN_PARTICIPANTS, cursor.getString(cursor.getColumnIndex(COLUMN_PARTICIPANTS)));
            map.put(COLUMN_QUES, cursor.getString(cursor.getColumnIndex(COLUMN_QUES)));
            map.put(COLUMN_QUIZ_DATE, cursor.getString(cursor.getColumnIndex(COLUMN_QUIZ_DATE)));
            map.put(COLUMN_END_TIME, cursor.getString(cursor.getColumnIndex(COLUMN_END_TIME)));
            map.put(COLUMN_START_TIME, cursor.getString(cursor.getColumnIndex(COLUMN_START_TIME)));
            map.put(COLUMN_INSTRUCTIONS, cursor.getString(cursor.getColumnIndex(COLUMN_INSTRUCTIONS)));
            map.put(COLUMN_LANG, cursor.getString(cursor.getColumnIndex(COLUMN_LANG)));
            map.put(COLUMN_TITLE, cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));

            list.add(map);
            cursor.moveToNext();
        }
        return list;
    }


    public void clearDb() {
        db = getReadableDatabase();
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void removeItemJoinedQuiz(String id) {
        db = getReadableDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + COLUMN_ID + " = " + id);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
