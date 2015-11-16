// SQLiteOpenHelper has predefined methods for creating the Database
// which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate.sqlitehelper;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// Create a helper object to create, open, and/or manage a database.
public class DatabaseHelper extends SQLiteOpenHelper {
    //
    SQLiteDatabase db;

    // Database name and Version for in-app use
    public static final String DATABASE_NAME = "TextMate.db";
    public static final int DATABASE_VERSION = 16;

    // Database table name
    /* Threads are continuous conversations with a foreign recipient
    * store in /data/data/com.android.providers.telephony/mmssms.db
    * each thread stores metadata of a conversation namely SMS.
    * SMS are individual text messages sent/received within each Thread.*/
    public static final String THREAD_TABLE = "threads";
    public static final String SMS_TABLE = "sms";

    // SMS_TABLE columns
    public static final String SMS_ID = "_id";
    public static final String THREAD_ID_REF = "thread_id";     // Foreign key to reference THREAD_TABLE
    public static final String ADDRESS = "address";             // Recipient address
    public static final String PERSON = "person";               // Recipient contact name
    public static final String DATE_RECEIVED = "date_received";
    public static final String DATE_SENT = "date_sent";
    public static final String BODY = "body";
    public static final String TYPE = "type";
    public static final String DIFF_SENT_TIME = "diff_sent_time";
    public static final String DIFF_RETURN_TIME = "diff_return_time";

    // THREADS_TABLE columns
    public static final String THREAD_ID = "_id";
    public static final String THREAD_DATE = "date_initiated";           // Created date for thread
    public static final String MESSAGE_COUNT = "message_count";
    public static final String RECIPIENT = "recipient";
    public static final String SCORE_TODAY = "today_score";
    public static final String SCORE_YESTERDAY = "yesterday_score";
    public static final String SCORE_AVERAGE = "average_score";
    public static final String NUM_OF_UPDATES = "num_of_updates";
    public static final String BIRTH = "birth";
    public static final String TIMESTAMP = "Timestamp";


    public static final String CREATE_SMS_TABLE = "CREATE TABLE " + SMS_TABLE + "("
            + SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + THREAD_ID_REF + " INTEGER NOT NULL, "
            + ADDRESS + " TEXT, "
            + PERSON + " INTEGER DEFAULT 0, "
            + DATE_RECEIVED + " DATETIME, "
            + DATE_SENT + " DATETIME, "
            + BODY + " TEXT, "
            + TYPE + " INTEGER, "
            + DIFF_SENT_TIME + " INTEGER DEFAULT 0, "
            + DIFF_RETURN_TIME + " INTEGER DEFAULT 0, "
            + BIRTH + " DATETIME, "
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "UNIQUE(" + DATE_RECEIVED + ") ON CONFLICT IGNORE, "
            + "FOREIGN KEY(" + THREAD_ID_REF + ") REFERENCES " + THREAD_TABLE + "(_id));";


    public static final String CREATE_THREADS_TABLE = "CREATE TABLE " + THREAD_TABLE + "("
            + THREAD_ID + " INTEGER PRIMARY KEY, "
            + THREAD_DATE + " DATETIME, "
            + MESSAGE_COUNT + " INTEGER DEFAULT 0, "
            + RECIPIENT + " INTEGER DEFAULT 0, "
            + SCORE_TODAY + " REAL, "
            + SCORE_YESTERDAY + " REAL, "
            + SCORE_AVERAGE + " REAL, "
            + NUM_OF_UPDATES + " INTEGER DEFAULT 0, "
            + BIRTH + " DATETIME, "
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP" + ");";

    // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        db = this.getWritableDatabase();
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
        db.execSQL(CREATE_SMS_TABLE);
        db.execSQL(CREATE_THREADS_TABLE);
    }

    // onUpgrade Method is updating the Database Version when Creating a new Version of an existing DB
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + THREAD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + SMS_TABLE);
        onCreate(db);
    }

    // Converts from UNIX datetime format into SQLite readable format
    private String getDateTime(long data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        Date date = new Date(data);
        return dateFormat.format(date);
    }

    // Inserts the sms data from the built-in Android database.
    public void insert_sms(long t_id, String address, int person,
                              long date, String body, int type, String flag) {
        //
        db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(THREAD_ID_REF, t_id);
        val.put(ADDRESS, address);
        val.put(PERSON, person);
        if (flag.equals("INCOMING")) {
            val.put(DATE_RECEIVED, getDateTime(date));
        }
        else
            val.put(DATE_SENT, getDateTime(date));
        val.put(BODY, body);
        val.put(TYPE, type);
        val.put(BIRTH, getDateTime(java.lang.System.currentTimeMillis()));
        //
        try {
            db.insertOrThrow(SMS_TABLE, null, val);
        } catch (SQLException e) {
            Log.d("insert-sms ->", "INSERTION Failed!");
        }
    }

    // populateThread will read SMS data from the SMS_table and
    // auto-populate the thread table base on schema design.
    public void populateThread() {
        //
        db = this.getWritableDatabase();

        String FETCH_THREAD_ID = "SELECT thread_id FROM sms GROUP BY thread_id;";
        Cursor cursor1 = db.rawQuery(FETCH_THREAD_ID, null);
        try {
            if (cursor1.moveToFirst()) {
                do {
                    // Query to get the data related to specific thread_id
                    String FETCH_THREAD_DATA = String.format(
                            "SELECT thread_id, count(thread_id) AS cnt, MIN(date_received) AS firstMsg FROM sms WHERE thread_id=%s;",
                                cursor1.getLong(cursor1.getColumnIndex("thread_id")));
                    Cursor cursor2 = db.rawQuery(FETCH_THREAD_DATA, null);
                    if (cursor2 != null) {
                        // retrieve the data related to a thread_id
                        cursor2.moveToFirst();
                        long thread_id = cursor2.getLong(cursor2.getColumnIndex("thread_id"));
                        int msgCount = cursor2.getInt(cursor2.getColumnIndex("cnt"));
                        String lastSent = cursor2.getString(cursor2.getColumnIndex("firstMsg"));
                        // Insert into THREAD table
                        try {
                            insert_thread(thread_id, msgCount, lastSent);
                        } catch (SQLException e) {
                            Log.d("in-dbHelper(pThread) ->", "INSERTION Failed!");
                        }
                        cursor2.close();
                    }
                } while (cursor1.moveToNext());
                cursor1.close();
            }
        } catch (SQLException e) {
            Log.d("dbHelper(pThread) -> ", "INSERTION Failed!");
        }
    }

    // Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    // and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    // the data is inserted correctly
    public void insert_thread(long tID, int msgCount, String t_date) {
        //
        db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(THREAD_ID, tID);
        val.put(MESSAGE_COUNT, msgCount);  // Number of the messages in the thread
        val.put(THREAD_DATE, t_date);
        val.put(BIRTH, getDateTime(java.lang.System.currentTimeMillis()));

        try {
            db.insertOrThrow(THREAD_TABLE, null, val);
        } catch (SQLException e) {
            Log.d("insertThread ->", "INSERTION Failed!");
        }
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
