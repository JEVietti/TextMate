//SQLiteOpenHelper has predefined methods for creating the Database
//which we need to store values from the Algorithm we need for the application/algorithm
package com.example.textmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.String;

//Database for the Past Scores given after each time period of the Contact given by the Algorithm
public class textScoreDB extends SQLiteOpenHelper {

    //The Declarations for the Column and Database/Table Name
    public static final String DataBase_Name = "TextMateScores.db";
    public static final String Table_Name = "TextMateScoresData";
    public static final String Column1 = "ContactID";
    public static final String Column2 = "ContactName";
    public static final String Column3 = "Scores";

    //Class Constructor
    public textScoreDB(Context context) {
        super(context, DataBase_Name, null, 1);
    }


    @Override
    //On Create is the Constructor for the Database of the Past Scores for each Contact
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+Table_Name+"("+Column1+ "INTEGER PRIMARY KEY AUTOINCREMENT," +Column2+ "TEXT "+Column3+" DOUBLE "+ ")";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
        onCreate(db);
    }
    //Inserting the Scores from the Algorithm for each contact the boolean is used for the same
    //reason mentioned in textDB.java
    public boolean insertData(String ID,String name,double algScore){
        ContentValues val = new ContentValues();

        val.put(Column1, ID);          //The ID of the Contacts
        val.put(Column2, name);       //The Name of the Contacts
        val.put(Column3,algScore);   //The Score


        SQLiteDatabase db = this.getWritableDatabase();

        long check = db.insert(Table_Name, null, val);
        db.close();
        return check != -1;
    }
    //Update is for after the number fo columns in each contact is used so we override the values
    //starting over again in a looping fashion
    public boolean updateData(String ID,String name, double algScore) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(Column1, ID);          //The ID of the Contacts
        val.put(Column2, name);         //The Name of the Contacts
        val.put(Column3,algScore);      //The Average Character Count Per Message per Day


        db.update(Table_Name, val, "ID = ?", new String[]{ID});
        return true;
    }

}