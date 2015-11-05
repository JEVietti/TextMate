package com.example.textmate;

import android.widget.ArrayAdapter;
import com.example.textmate.sqlitehelper.DatabaseHelper;
import com.example.textmate.sqlite.models.textMateData;
import com.example.textmate.sqlite.models.textMateScores;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.example.textmate.sqlitehelper.DatabaseHelper;

public class MainActivity extends ActionBarActivity {
    // Create instance of Database
    // and, instance of ArrayList to query and store
    // sms data from Android mmssms.db
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textMateDB = new DatabaseHelper(this);
        ContentResolver contentResolver = getContentResolver();
        getSMSData2 updEmple = new getSMSData2();
        TextView tv1 = (TextView)findViewById(R.id.textView2);
        tv1.setText(updEmple.getSMSData(contentResolver));
      //myTextDB = new textDB(this);
      //myScoreDB = new textScoreDB(this);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //
    public void fetchThread() {
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                Uri.parse("content://mms-sms/complete-conversations"),
                new String[]{"date", "message_count", "recipient_ids", "snippet"},
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
                    String snippet = cursor.getString(cursor.getColumnIndex("snippet"));
                    //
                    try {
                        dbHelper.insert_thread(init_date, msg_count, recipient, snippet);
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
