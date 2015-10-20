//SQLiteOpenHelper has predefined methods for creating the Database
//which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.String;


//This DataBase will store the Averages for the Values needed for the Algorithm
public class textDB extends SQLiteOpenHelper {

//Table and Column Name Declarations
    public static final String DataBase_Name = "TextMate.db";
    public static final String Table_Name = "TextMateData";
    public static final String Column1 = "ContactID";
    public static final String Column2 = "ContactName";
    public static final String Column3= "CharCount";
    public static final String Column4 = "MessageCount";
    public static final String Column5 = "TimeDiffSent";
    public static final String Column6 = "TimeDiffReturn";
    
    //Class Constructor
    public textDB(Context context) {
        super(context, DataBase_Name, null, 1);
    }
    //onCreate Method is basically the Constructor of the DB Class
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable="CREATE TABLE "+Table_Name+"("+Column1+ "INTEGER PRIMARY KEY AUTOINCREMENT," +Column2+ "TEXT "+Column3+" INTEGER "
                +Column4+" INTEGER"+Column5+"DOUBLE" +Column6+"DOUBLE" + ")";
        db.execSQL(createTable);
    }
    //onUpgrade Method is updating the Database Version when Creating a new Version of an existing DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS "+Table_Name);
         onCreate(db);
    }
    //Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    //and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    //the data is inserted correctly
    public boolean insertData(String ID,String name,int charCount, int messageCount, double diffSentTime, double diffReturnTime){
        ContentValues val = new ContentValues();

        val.put(Column1, ID);          //The ID of the Contacts
        val.put(Column2, name);       //The Name of the Contacts
        val.put(Column3,charCount);    //Initial Character Count per Day
        val.put(Column4,messageCount);  //Initial Message Count per Day
        val.put(Column5,diffSentTime);   //Initial Average time between Sending for each Contact for the day
        val.put(Column6,diffReturnTime);  //Initial Average time between receiving a message for each Contact for the day

        SQLiteDatabase db = this.getWritableDatabase();

        long check = db.insert(Table_Name, null, val);
        db.close();
        return check != -1;
    }
     //The Update Data will be the values passed to it
    public boolean updateData(String ID,String name,int charCount, int messageCount, double diffSentTime, double diffReturnTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(Column1, ID);          //The ID of the Contacts
        val.put(Column2, name);         //The Name of the Contacts
        val.put(Column3,charCount);      //The Average Character Count Per Message per Day
        val.put(Column4,messageCount);   //The Average Message Count for the the Day
        val.put(Column5,diffSentTime);  // Average Amount of time between Sent Messages from Received
        val.put(Column6,diffReturnTime);


        db.update(Table_Name, val, "ID = ?", new String[]{ID}); //Update based on the ID
        return true;
    }

}
