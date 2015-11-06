// SQLiteOpenHelper has predefined methods for creating the Database
// which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate.sqlitehelper;


import java.lang.String;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// Create a helper object to create, open, and/or manage a database.
public class DatabaseHelper extends SQLiteOpenHelper {

    // Database name and Version for in-app use
    public static final String DATABASE_NAME = "TextMate.db";
    public static final int DATABASE_VERSION = 2;

    // Database table name
    /* Threads are continuous conversations with a foreign recipient
    * store in /data/data/com.android.providers.telephony/mmssms.db
    * each thread stores metadata of a conversation namely SMS.
    * SMS are individual text messages sent/received within each Thread.*/
    public static final String THREAD_TABLE = "threads";
    public static final String SMS_TABLE = "sms";

    // THREADS_TABLE columns
    public static final String THREAD_ID = "_id";
    public static final String THREAD_DATE = "date";           // Created date for thread
    public static final String MESSAGE_COUNT = "message_count";
    public static final String RECIPIENT = "recipient";
    public static final String SCORE_TODAY = "today_score";
    public static final String SCORE_YESTERDAY = "yesterday_score";
    public static final String SCORE_AVERAGE = "average_score";
    public static final String NUM_OF_UPDATES = "num_of_updates";
    public static final String BIRTH = "birth";
    public static final String TIMESTAMP = "Timestamp";


    // SMS_TABLE columns
    public static final String SMS_ID = "_id";
    public static final String THREAD_ID_REF = "thread_id";      // Foreign key to reference THREAD_TABLE
    public static final String ADDRESS = "address";        // Recipient address
    public static final String PERSON = "person";         // Recipient contact name
    public static final String RECEIVED_DATE = "received_date";
    public static final String SENT_DATE = "sent_date";
    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String DIFF_SENT_TIME = "diff_sent_time";
    public static final String DIFF_RETURN_TIME = "diff_return_time";


    public static final String CREATE_THREADS_TABLE = "CREATE TABLE " + THREAD_TABLE + "("
            + THREAD_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + THREAD_DATE + " INTEGER DEFAULT 0, "
            + MESSAGE_COUNT + " INTEGER DEFAULT 0, "
            + RECIPIENT + " TEXT, "
            + SCORE_TODAY + " REAL, "
            + SCORE_YESTERDAY + " REAL, "
            + SCORE_AVERAGE + " REAL, "
            + NUM_OF_UPDATES + " INTEGER DEFAULT 0, "
            + BIRTH + " DATETIME, "
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ");";

    public static final String CREATE_SMS_TABLE = "CREATE TABLE " + SMS_TABLE + "("
            + SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ADDRESS + " TEXT, "
            + PERSON + " INTEGER, "
            + RECEIVED_DATE + " INTEGER, "
            + SENT_DATE + " INTEGER, "
            + BODY + " TEXT, "
            + TYPE + " TEXT, "
            + DIFF_SENT_TIME + " INTEGER DEFAULT 0, "
            + DIFF_RETURN_TIME + " INTEGER DEFAULT 0, "
            + BIRTH + " DATETIME, "
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + THREAD_ID_REF + " INTEGER, "
            + "FOREIGN KEY(" + THREAD_ID_REF + ") REFERENCES " + THREAD_TABLE + "(_id));";

    // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
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
    }

    // onUpgrade Method is updating the Database Version when Creating a new Version of an existing DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + THREAD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
        onCreate(db);
    }

    // Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    // and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    // the data is inserted correctly
    public long insert_thread(int date, int msgCount, String recipIds) {
        //
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(THREAD_DATE, date);        //The Name of the Contacts
        val.put(MESSAGE_COUNT, msgCount);  //Initial Character Count per Day
        val.put(RECIPIENT, recipIds);      //Initial Message Count per Day
        val.put(BIRTH, "(NOW)");

        return db.insert(THREAD_TABLE, null, val);
    }

    // Inserts the sms data from the built-in Android database.
    public boolean insert_sms(int t_id, String address, int person,
                              int recvDate, int sentDate, String body, String type) {
        //
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(THREAD_ID_REF, t_id);
        val.put(ADDRESS, address);
        val.put(PERSON, person);
        val.put(RECEIVED_DATE, recvDate);
        val.put(SENT_DATE, sentDate);
        val.put(BODY, body);
        val.put(TYPE, type);
        val.put(BIRTH, "(NOW)");

        long check = db.insert(SMS_TABLE, null, val);

        if (check != -1)
            return true;
        else
            return false;
    }
}
/*
    //////////////////////////////TextMateData Table/////////////////////////////////////////////

    //Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    //and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    //the data is inserted correctly
    public boolean Data_insertData(String ID, String name, int charCount,
                                   int messageCount, double diffSentTime, double diffReturnTime) {
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

        Log.e(LOG, selectQuery);

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
        val.put(Data_Column4, tmData.fetchMsgCount());
        //...

        return db.update(Data_Table_Name,val,Data_Column1 + "= ?",
                new String[] {String.valueOf(tmData.fetchID())});
    }


    //Deleting TextMate DataTable
    public void deleteTMData(long dataID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Data_Table_Name, Data_Column1 + "= ?",
                new String[]{String.valueOf(dataID)});
    db.close();
    }

        ///////////////////////// TextMate Algorithm Scores Table ///////////////////////////////////

    //Fetch a single row
    public textMateScores fetchScore(long dataID){
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT * FROM" + Data_Table_Name + "WHERE"
                +Scores_Column1+ "=" + dataID;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        //Use the model Functions for the Table Class
        textMateScores tmScores = new textMateScores();
        if(c != null && c.moveToFirst()) {
            tmScores.setID(c.getInt(c.getColumnIndex(Scores_Column1)));
            tmScores.setName(c.getString(c.getColumnIndex(Scores_Column2)));
            tmScores.setScore(c.getDouble(c.getColumnIndex(Scores_Column3)));
            tmScores.setNumUpdate(c.getInt(c.getColumnIndex(Scores_Column4)));
            //....
        }
        db.close();
        return tmScores;
    }
    //Fetch all rows data
    public List<textMateScores> fetchAllScores(){
        List<textMateScores> allTS = new ArrayList<textMateScores>();
        String selectQuery = "SELECT * FROM" + Scores_Table_Name;

        Log.e(LOG,selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery,null);

        if(c!=null && c.moveToFirst()) {
            do {
                textMateScores tmScore = new textMateScores();
                tmScore.setID(c.getInt(c.getColumnIndex(Scores_Column1)));
                tmScore.setName(c.getString(c.getColumnIndex(Scores_Column2)));
                tmScore.setScore(c.getDouble(c.getColumnIndex(Scores_Column3)));
                tmScore.setNumUpdate(c.getInt(c.getColumnIndex(Scores_Column4)));
                //....
                allTS.add(tmScore);
            }
            while (c.moveToNext());
        }
        db.close();
        return allTS;
    }

    //Updating the TextMate ScoresTable row
    public int updateScoreTable(textMateScores tmScores){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues val = new ContentValues();
        val.put(Scores_Column3, tmScores.fetchScore());
        val.put(Scores_Column4,tmScores.fetchNumUpdate());
        //...

        return db.update(Scores_Table_Name,val,Scores_Column1 + "= ?",
                new String[] {String.valueOf(tmScores.fetchID())});
    }


    //Deleting TextMate DataTable
    public void deleteTMScore(long scoreID){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Scores_Table_Name, Scores_Column1 + "= ?",
                new String[] {String.valueOf(scoreID)});
        db.close();
    }

}
*/
