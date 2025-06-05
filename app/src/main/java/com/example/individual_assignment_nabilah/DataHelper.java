package com.example.individual_assignment_nabilah;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataHelper extends SQLiteOpenHelper {

    //Database Name
    private static final String DATABASE_NAME = "bill.db";
    //Database Version
    private static final int DATABASE_VERSION = 1;
    //Create Constructor for Data Helper

    public DataHelper(Context context){
        super(context, DATABASE_NAME,null,DATABASE_VERSION);
    }
    @Override
    //Create Table
    public void onCreate(SQLiteDatabase db){
        String sql = "CREATE TABLE bill (" +
                "no INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "month TEXT, " +
                "unit REAL, " +
                "rebate REAL, " +
                "totalCharges REAL, " +
                "finalCost REAL);";

        Log.d("Data","onCreate: "+sql);
        db.execSQL(sql);
    }
    //create method to upgrade database version if database exist

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS bill");
        onCreate(db);
    }
}
