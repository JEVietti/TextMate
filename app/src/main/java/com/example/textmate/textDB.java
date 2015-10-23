package com.example.textmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.Class;
import java.lang.String;

public class textDB extends SQLiteOpenHelper {

    public static final String DataBase_Name = "TextMate.db";
    public static final String Table_Name = "TextMateData";
    public static final String Column1 = "ContactID";
    public static final String Column2 = "ContactName";
    public static final String Column3= "CharCount";
    public static final String Column4 = "MessageCount";
    public static final String Column5 = "TimeDiff";
    //public static final String Column6 = "NumUpdates";
    public textDB(Context context) {
        super(context, DataBase_Name, null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+Table_Name+"("+Column1+ "INTEGER PRIMARY KEY AUTOINCREMENT," +Column2+ "TEXT "+Column3+" INTEGER "
                +Column4+" INTEGER"+Column5+"DOUBLE" + ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
        onCreate(db);
    }

    public boolean insertData(String ID,String name,int charCount, int messageCount, double diffTime){
        ContentValues val = new ContentValues();

        val.put(Column1, ID);
        val.put(Column2, name);
        val.put(Column3,charCount);
        val.put(Column4,messageCount);
        val.put(Column5,diffTime);
        SQLiteDatabase db = this.getWritableDatabase();

        long check = db.insert(Table_Name, null, val);
        db.close();
        return check != -1;
    }

    public boolean updateData(String ID,String name,int charCount, int messageCount, double diffTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();
        val.put(Column1, ID);
        val.put(Column2, name);
        val.put(Column3,charCount);
        val.put(Column4,messageCount);
        val.put(Column5,diffTime);
        db.update(Table_Name, val, "ID = ?", new String[]{ID});
        return true;
    }

}
