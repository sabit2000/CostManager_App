package com.cscorner.cse_489_sabit;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EventDB extends SQLiteOpenHelper {

    public EventDB(Context context) {
        super(context, "EventDBDB.db", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("DB@OnCreate");
        String sql = "CREATE TABLE items  ("
                + "ID TEXT PRIMARY KEY,"
                + "itemName TEXT,"
                + "date INT,"
                + "cost REAL"
                + ")";
        db.execSQL(sql);
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        System.out.println("Write code to modify database schema here");

    }
    public void insertItem(String ID, String itemName, long date, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("itemName", itemName);
        cols.put("date", date);
        cols.put("cost", cost);
        db.insert("items", null , cols);
        db.close();
    }
    public void updateItem(String ID, String itemName, long date, double cost) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cols = new ContentValues();
        cols.put("ID", ID);
        cols.put("itemName", itemName);
        cols.put("date", date);
        cols.put("cost", cost);
        db.insert("items", null ,  cols);
        db.update("items", cols, "ID=?", new String[ ] {ID} );
        db.close();
    }
    public void deleteItem(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("items", "ID=?", new String[ ] {ID} );
        db.close();
    }
    public Cursor selectItems(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=null;
        try {
            res = db.rawQuery(query, null);
        } catch (Exception e){
            e.printStackTrace();
        }
        return res;
    }
}


