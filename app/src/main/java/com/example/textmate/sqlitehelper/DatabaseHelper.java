// SQLiteOpenHelper has predefined methods for creating the Database
// which we need to store values from the SMSDataBase we need for the application/algorithm
package com.example.textmate.sqlitehelper;

import java.lang.String;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.ArrayList;
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
    public static final int DATABASE_VERSION = 36;

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
    public static final String WORD_COUNT = "word_count";
    public static final String TYPE = "type";

    // THREADS_TABLE columns
    public static final String THREAD_ID = "_id";
    public static final String THREAD_DATE = "date_initiated";           // Created date for thread
    public static final String MESSAGE_COUNT = "message_count";
    public static final String RECIPIENT = "recipient";
    public static final String TOTAL_WORD_COUNT = "total_word_count";
    public static final String DIFF_RECEIVE_TIME = "diff_receive_time";
    public static final String DIFF_SENT_TIME = "diff_sent_time";
    public static final String SCORE_TODAY = "today_score";
    public static final String SCORE_YESTERDAY = "yesterday_score";
    public static final String SCORE_AVERAGE = "average_score";
    public static final String NUM_OF_UPDATES = "num_of_updates";
    public static final String STATUS = "status";
    public static final String BIRTH = "birth";
    public static final String TIMESTAMP = "Timestamp";


    public static final String CREATE_SMS_TABLE = "CREATE TABLE " + SMS_TABLE + "("
            + SMS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + THREAD_ID_REF + " INTEGER NOT NULL, "
            + ADDRESS + " TEXT, "
            + PERSON + " INTEGER DEFAULT 0, "
            + DATE_RECEIVED + " DATETIME DEFAULT 0, "
            + DATE_SENT + " DATETIME DEFAULT 0, "
            + BODY + " TEXT, "
            + WORD_COUNT + " INTEGER DEFAULT 0, "
            + TYPE + " INTEGER, "
            + BIRTH + " DATETIME, "
            + TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP, "
            + "UNIQUE(" + DATE_RECEIVED + ") ON CONFLICT IGNORE, "
            + "FOREIGN KEY(" + THREAD_ID_REF + ") REFERENCES " + THREAD_TABLE + "(_id));";


    public static final String CREATE_THREADS_TABLE = "CREATE TABLE " + THREAD_TABLE + "("
            + THREAD_ID + " INTEGER PRIMARY KEY, "
            + THREAD_DATE + " DATETIME, "
            + MESSAGE_COUNT + " INTEGER DEFAULT 0, "
            + RECIPIENT + " TEXT DEFAULT NULL, "
            + TOTAL_WORD_COUNT + " INTEGER DEFAULT 0, "
            + DIFF_RECEIVE_TIME + " INTEGER DEFAULT 0, "
            + DIFF_SENT_TIME + " INTEGER DEFAULT 0, "
            + SCORE_TODAY + " REAL DEFAULT 0, "
            + SCORE_YESTERDAY + " REAL DEFAULT 0, "
            + SCORE_AVERAGE + " REAL DEFAULT 0, "
            + NUM_OF_UPDATES + " INTEGER DEFAULT 0, "
            + STATUS + " TEXT, "
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

    // Counts the number of words in the body of a SMS.
    private int wordCount(String body) {
        //
        int wordCount = 0;

        if (body.trim().equals("")) return wordCount;
        else wordCount = 1;

        for (int i = 0; i < body.length(); i++) {
            char ch = body.charAt(i);
            String str = String.format("%s", ch);
            if (i+1 != body.length() && str.equals(" ") && !(""+ body.charAt(i+1)).equals(" ")) {
                wordCount++;
            }
        }

        return wordCount;
    }

    // Inserts the sms data from the built-in Android database.
    public void insert_sms(long t_id, String address, int person,
                              long date, String body, int type, String flag) {
        //
        db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        //address = getContactName(android.content.Context, address);
        val.put(THREAD_ID_REF, t_id);
        val.put(ADDRESS, address);
        val.put(PERSON, person);
        if (flag.equals("INCOMING")) {
            val.put(DATE_RECEIVED, getDateTime(date));
        }
        else {
            val.put(DATE_SENT, getDateTime(date));
        }
        val.put(BODY, body);
        val.put(WORD_COUNT, wordCount(body));
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
                            "SELECT thread_id, address, " +
                                    "COUNT(thread_id) AS cnt, " +
                                    "SUM(word_count) AS totalWordCount, " +
                                    "MIN(date_received) AS firstMsg " +
                                    "FROM sms WHERE thread_id=%s;",
                                cursor1.getLong(cursor1.getColumnIndex("thread_id")));
                    Cursor cursor2 = db.rawQuery(FETCH_THREAD_DATA, null);
                    if (cursor2 != null) {
                        // retrieve the data related to a thread_id
                        cursor2.moveToFirst();
                        long thread_id = cursor2.getLong(cursor2.getColumnIndex("thread_id"));
                        String recipient = cursor2.getString(cursor2.getColumnIndex("address"));
                        int totalWordCount = cursor2.getInt(cursor2.getColumnIndex("totalWordCount"));
                        int msgCount = cursor2.getInt(cursor2.getColumnIndex("cnt"));
                        String lastSent = cursor2.getString(cursor2.getColumnIndex("firstMsg"));
                        // Insert into THREAD table
                        try {
                            insert_thread(thread_id, recipient, totalWordCount, msgCount, lastSent);
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
    public void insert_thread(long t_ID, String recipient, int totalWord, int msgCount, String t_date) {
        //
        db = this.getWritableDatabase();
        ContentValues val = new ContentValues();

        val.put(THREAD_ID, t_ID);
        val.put(RECIPIENT, recipient);
        val.put(TOTAL_WORD_COUNT, totalWord);
        val.put(MESSAGE_COUNT, msgCount);  // Number of the messages in the thread
        val.put(THREAD_DATE, t_date);
        val.put(BIRTH, getDateTime(java.lang.System.currentTimeMillis()));

        try {
            db.insertOrThrow(THREAD_TABLE, null, val);
        } catch (SQLException e) {
            Log.d("insertThread ->", "INSERTION Failed!");
        }
    }

    // Returns a list of all available threads within the threads table.
    public ArrayList<Long> getThreadIDs() {
        //
        db = this.getReadableDatabase();

        // ArrayList of variable size to store distinct id in threads tables.
        ArrayList<Long> threadIdsList = new ArrayList<Long>();

        String THREAD_IDS_LIST = "SELECT DISTINCT _id AS id from threads;";

        Cursor cursor = db.rawQuery(THREAD_IDS_LIST, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    threadIdsList.add(cursor.getLong(cursor.getColumnIndex("id")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Log.i("INFO(getThreadIds)", threadIdsList.toString());

        return threadIdsList;
    }

    // Executes query string
    public ArrayList<Long> executeQuery(String query) {
        //
        Log.i("INFO(executeQuery)", query);

        db = this.getReadableDatabase();

        ArrayList<Long> dateTimes = new ArrayList<Long>();

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    dateTimes.add(cursor.getLong(cursor.getColumnIndex("diff")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Log.i("INFO(executeQuery", dateTimes.toString());

        return dateTimes;
    }

    //Query  the database from a SQLite Execution String
    // Make sure to change to NON VOID once FIXED
    /*public ArrayList<Long> querySMSList(String dateType, String msgType, String id) {
        // array of double to store date_received columns
        Log.i("INFO(querSMSList)", dateType);

        db = this.getReadableDatabase();

        ArrayList<Long> dateTimes = new ArrayList<Long>();

        //String fetchTimeReceivedList = String.format(
        //        "SELECT strftime('%s', %s) AS diff FROM sms WHERE type=%s AND thread_id=%s", "%s", dateType, msgType, id);

        String fetchSMSList = String.format(
                "SELECT %s AS diff FROM sms WHERE type=%s AND thread_id=%s;", dateType, msgType, id);

        Log.i("INFO(querySMSList)", fetchSMSList);

        Cursor cursor = db.rawQuery(fetchSMSList, null);

        if (cursor != null) {
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                do {
                    //
                    dateTimes.add(cursor.getLong(cursor.getColumnIndex("diff")));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        Log.i("INFO(querySMSList)", dateTimes.toString());

        return dateTimes;
    }*/

    /*public String getContactName(Context _context, String number) {
        String name;
        if(number != null && !number.equals("")){
            // define the columns I want the query to return
            String[] projection = new String[] {
                    ContactsContract.PhoneLookup.DISPLAY_NAME,
                    ContactsContract.PhoneLookup._ID};

            // encode the phone number and build the filter URI
            Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));

            // query time
            Cursor cursor = _context.getContentResolver().query(contactUri, projection, null, null, null);

            if(cursor != null) {
                if (cursor.moveToFirst()) {
                    name =      cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
                }
                cursor.close();
            }
        }
        return name;
    }*/
}