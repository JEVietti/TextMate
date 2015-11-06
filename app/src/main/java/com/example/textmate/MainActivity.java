package com.example.textmate;

import com.example.textmate.sqlitehelper.DatabaseHelper;

import android.database.SQLException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;


public class MainActivity extends ActionBarActivity {
    // Create instance of Database
    // and, instance of ArrayList to query and store
    // sms data from Android mmssms.db
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DatabaseHelper(this);
        //fetchThread();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //
    public void fetchThread() {
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                Uri.parse("content://mms-sms/conversations/"),
                new String[]{"date", "message_count", "recipient_ids"},
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {
                do {
                    // to grab the data
                    int init_date = cursor.getInt(cursor.getColumnIndex("date"));
                    int msg_count = cursor.getInt(cursor.getColumnIndex("message_count"));
                    String recipient = cursor.getString(cursor.getColumnIndex("recipient_ids"));
                    //
                    try {
                        dbHelper.insert_thread(init_date, msg_count, recipient);
                        //fetchSMS(t_id);
                    } catch(SQLException e) {
                        Log.d("Failure", "Failed to insert!");
                    }
                } while (cursor.moveToPrevious());
            }
        }
    }

    /*
    public void fetchSMS(long t_id) {
        //ArrayList<Object> smsDataArray = new ArrayList<Object>();
        Uri sms_uri = Uri.parse("content://sms");

        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                sms_uri,
                new String[]{"thread_id", "address", "person", "data", "data_sent", "body", "type"},
                "thread_id=" + t_id,
                null,
                "date" + "COLLATE LOCALIZED ASC");

        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {
                do {
                    // to grab the data
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("_id")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("thread_id")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("address")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("person")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("date")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("date_sent")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("body")));
                    smsDataArray.add(cursor.getString(cursor.getColumnIndex("type")));
                } while (cursor.moveToPrevious());
            }
        }
    }
    */
}
