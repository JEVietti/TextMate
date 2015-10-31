// SQLiteOpenHelper has predefined methods for creating the Database
// which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate.sqlitehelper;

import com.example.textmate.sqlite.models.textMateData;
import com.example.textmate.sqlite.models.textMateScores;
import java.lang.String;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


// Create a helper object to create, open, and/or manage a database.
public class DatabaseHelper extends SQLiteOpenHelper {
     //Logging Variable for Queries
    private static final String LOG = DatabaseHelper.class.getName();

    // Database name and Version for in-app use
    public static final String DATABASE_NAME    = "TextMate";
    public static final int DATABASE_VERSION    = 1;

    // Database table name
    /* Threads are continuous conversations with a foreign recipient
    * store in /data/data/com.android.providers.telephony/mmssms.db
    * each thread stores metadata of a conversation namely SMS.
    * SMS are individual text messages sent/received within each Thread.*/
    public static final String SMS_THREAD_TABLE = "thread";
    public static final String SMS_TABLE   = "sms";

    // SMS_THREADS_TABLE columns
    public static final String ID_THREAD        = "_id";
    public static final String THREAD_DATE      = "date";           // Created date for thread
    public static final String MESSAGE_COUNT    = "message_count";
    public static final String RECIPIENT        = "recipient";      //
    public static final String SNIPPET          = "snippet";        // Stores last sms in a thread

    // SMS_DATA_TABLE columns
    public static final String ID_SMS           = "_id";
    public static final String THREAD_ID        = "thread_id";      // Foreign key to reference THREAD_TABLE
    public static final String ADDRESS          = "address";        // Recipient address
    public static final String PERSON           = "person";         // Recipient contact name
    public static final String RECEIVED_DATE    = "received_date";
    public static final String SENT_DATE        = "sent_date";
    public static final String BODY             = "body";


    //TextMate Data Columns
    public static final String Data_Table_Name = "TextMateData";
    public static final String Data_Column1 = "ContactID";
    public static final String Data_Column2 = "ContactName";
    public static final String Data_Column3= "CharCount";
    public static final String Data_Column4 = "MessageCount";
    public static final String Data_Column5 = "DiffSentTime";
    public static final String Data_Column6 = "DiffReturnTime";
    public static final String Data_Column7 = "NumberOfUpdates";
    public static final int DataBase_Version = 1;

    //TextMate Scores Columns
    public static final String Scores_Table_Name = "TextMateScoresData";
    public static final String Scores_Column1 = "ContactID";
    public static final String Scores_Column2 = "ContactName";
    public static final String Scores_Column3 = "Scores";
    public static final String Scores_Column4 = "NumberOfUpdates";


    public static final String CREATE_THREADS_TABLE = "CREATE TABLE " + SMS_THREAD_TABLE + "("
            + ID_THREAD + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + THREAD_DATE + " INTEGER DEFAULT 0, "
            + MESSAGE_COUNT + " INTEGER DEFAULT 0, "
            + RECIPIENT + " TEXT, "
            + SNIPPET + " TEXT" + ")";

    public static final String CREATE_SMS_TABLE = "CREATE TABLE " + SMS_TABLE + "("
            + ID_SMS + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "FOREIGN KEY(" + THREAD_ID + ") REFERENCES " + SMS_THREAD_TABLE + "(_id), "
            + ADDRESS + " TEXT, "
            + PERSON + " INTEGER, "
            + RECEIVED_DATE + " INTEGER, "
            + SENT_DATE + " INTEGER, "
            + BODY + "TEXT" + ")";

    public static final String Create_Data_Table="CREATE TABLE "+Data_Table_Name+"("
            +Data_Column1+ "INTEGER PRIMARY KEY AUTOINCREMENT, "
            +Data_Column2+ "TEXT "
            +Data_Column3+ "INTEGER"
            +Data_Column4+ "INTEGER"
            +Data_Column5+ "DOUBLE"
            +Data_Column6+ "DOUBLE"
            +Data_Column7+ "INTEGER" + ")";

    public static final String Create_Score_Table = "CREATE TABLE "+Scores_Table_Name+"("
            +Scores_Column1+ "INTEGER PRIMARY KEY AUTOINCREMENT,"
            +Scores_Column2+ "TEXT "
            +Scores_Column3+" DOUBLE"
            +Scores_Column4+ "INTEGER" + ")";

    // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // onOpen method to enable the foreign key constraint
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraint
            db.execSQL("PRAGMA foreign_key=ON;");
        }
    }

    // onCreate Method is basically the Constructor of the DB Class
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_THREADS_TABLE);
        db.execSQL(CREATE_SMS_TABLE);
        db.execSQL(Create_Data_Table);
        db.execSQL(Create_Score_Table);

    }

    // onUpgrade Method is updating the Database Version when Creating a new Version of an existing DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SMS_THREAD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + Scores_Table_Name);
        db.execSQL("DROP TABLE IF EXISTS " + Data_Table_Name);
        //Create New Tables
        onCreate(db);
    }

    //////////////////////////////TextMateData Table/////////////////////////////////////////////

    //Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    //and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    //the data is inserted correctly
    public boolean Data_insertData(String ID, String name, int charCount, int messageCount, double diffSentTime, double diffReturnTime) {
        ContentValues val = new ContentValues();

        val.put(Data_Column1, ID);               //The ID of the Contacts
        val.put(Data_Column2, name);             //The Name of the Contacts
        val.put(Data_Column3, charCount);        //Initial Character Count per Day
        val.put(Data_Column4, messageCount);     //Initial Message Count per Day
        val.put(Data_Column5, diffSentTime);     //Initial Average time between Sending for each Contact for the day
        val.put(Data_Column6, diffReturnTime);   //Initial Average time between receiving a message for each Contact for the day

        SQLiteDatabase db = this.getWritableDatabase();

        long check = db.insert(Data_Table_Name, null, val);
        db.close();
        return check != -1;
    }

    //The Update Data will be the values passed to it
    public boolean Data_updateData(String ID, String name, int charCount,
                                   int messageCount, double diffSentTime, double diffReturnTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(Data_Column1, ID);               //The ID of the Contacts
        val.put(Data_Column2, name);             //The Name of the Contacts
        val.put(Data_Column3, charCount);        //The Average Character Count Per Message per Day
        val.put(Data_Column4, messageCount);     //The Average Message Count for the the Day
        val.put(Data_Column5, diffSentTime);     //Average Amount of time between Sent Messages from Received
        val.put(Data_Column6, diffReturnTime);


        db.update(Data_Table_Name, val, "ID = ?", new String[]{ID}); //Update based on the ID
        return true;
    }
    //Fetch a single row
    public textMateData fetchData(long dataID){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM" + Data_Table_Name + "WHERE"
                +Data_Column1+ "=" + dataID;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        //Use the model Functions for the Table Class
        textMateData tmData = new textMateData();
        if(c != null && c.moveToFirst()) {
            tmData.setId(c.getInt(c.getColumnIndex(Data_Column1)));
            tmData.setName(c.getString(c.getColumnIndex(Data_Column2)));
            tmData.setCharCount(c.getInt(c.getColumnIndex(Data_Column3)));
            tmData.setMsgCount(c.getInt(c.getColumnIndex(Data_Column4)));
            //....
        }

        return tmData;
    }
    //Fetch all rows data
    public List<textMateData> fetchAllData(){
        List<textMateData> allTD = new ArrayList<textMateData>();
        String selectQuery = "SELECT * FROM" + Data_Table_Name;

        Log.e(LOG,selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null && c.moveToFirst()) {
            do {
                textMateData tmData = new textMateData();
                tmData.setId(c.getInt(c.getColumnIndex(Data_Column1)));
                tmData.setName(c.getString(c.getColumnIndex(Data_Column2)));
                tmData.setCharCount(c.getInt(c.getColumnIndex(Data_Column3)));
                tmData.setMsgCount(c.getInt(c.getColumnIndex(Data_Column4)));
                //....
                allTD.add(tmData);
            }
            while (c.moveToNext());
        }
        return allTD;
    }

    //Updating the TextMate DataTable row
    public int updateDataTable(textMateData tmData){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(Data_Column3, tmData.fetchCharCount());
        val.put(Data_Column4,tmData.fetchMsgCount());
        //...

        return db.update(Data_Table_Name,val,Data_Column1 + "= ?",
                new String[] {String.valueOf(tmData.fetchID())});
    }


    //Deleting TextMate DataTable
    public void deleteTMData(long dataID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Data_Table_Name, Data_Column1 + "= ?",
                new String[] {String.valueOf(dataID)});
    }

        ///////////////////////// TextMate Algorithm Scores Table ///////////////////////////////////





}
