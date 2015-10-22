// SQLiteOpenHelper has predefined methods for creating the Database
// which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate;

import java.lang.String;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


// Create a helper object to create, open, and/or manage a database.
public class DatabaseHelper extends SQLiteOpenHelper {

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

    // Class Constructor
    public DatabaseHelper(Context context) {
        super(context, Database_Name, null, DATABASE_VERSION);
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
        db.execSQL("DROP TABLE IF EXISTS " + Table_Name);
        onCreate(db);
    }

    //Inserts the Actual Data into the DB, boolean instead of void to ensure that data is inserted correctly
    //and the db.insert method returns -1 if it is not so we check to make sure it is not to ensure that
    //the data is inserted correctly
    public boolean insertData(String ID, String name, int charCount,
                              int messageCount, double diffSentTime, double diffReturnTime) {
        ContentValues val = new ContentValues();

        val.put(Column1, ID);               //The ID of the Contacts
        val.put(Column2, name);             //The Name of the Contacts
        val.put(Column3, charCount);        //Initial Character Count per Day
        val.put(Column4, messageCount);     //Initial Message Count per Day
        val.put(Column5, diffSentTime);     //Initial Average time between Sending for each Contact for the day
        val.put(Column6, diffReturnTime);   //Initial Average time between receiving a message for each Contact for the day

        SQLiteDatabase db = this.getWritableDatabase();

        long check = db.insert(Table_Name, null, val);
        db.close();
        return check != -1;
    }

    //The Update Data will be the values passed to it
    public boolean updateData(String ID, String name, int charCount,
                              int messageCount, double diffSentTime, double diffReturnTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(Column1, ID);               //The ID of the Contacts
        val.put(Column2, name);             //The Name of the Contacts
        val.put(Column3, charCount);        //The Average Character Count Per Message per Day
        val.put(Column4, messageCount);     //The Average Message Count for the the Day
        val.put(Column5, diffSentTime);     //Average Amount of time between Sent Messages from Received
        val.put(Column6, diffReturnTime);


        db.update(Table_Name, val, "ID = ?", new String[]{ID}); //Update based on the ID
        return true;
    }

}
