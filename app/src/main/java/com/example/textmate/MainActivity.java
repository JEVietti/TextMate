
package com.example.textmate;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import com.example.textmate.sqlitehelper.DatabaseHelper;
import com.example.textmate.sqlite.models.textMateData;
import com.example.textmate.sqlite.models.textMateScores;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

import android.view.View;
import android.widget.Button;

import java.util.Vector;



public class MainActivity extends ActionBarActivity {
 /*   private ProgressDialog progressDialogInbox;
    DatabaseHelper dbHelper;
    textMateData dbData;
    alg scoreData;
    Calendar c = Calendar.getInstance(); */
    //textDB myTextDB;
    //textScoreDB myScoreDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
   /*     dbHelper = new DatabaseHelper(this);
        progressDialogInbox = new ProgressDialog(this);
        fetchInboxMessages();
        populateData(dbHelper);
        populateScores(dbHelper);*/

          //  ListView listView = (ListView) findViewById(R.id.SMSList);
       // final ArrayList<Long> contacts = dbHelper.getThreadID();
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, contacts);
        //listView.setAdapter(adapter);

        //Assigning an adapter
       // ArrayAdapter<DatabaseHelper> adapter = new ArrayAdapter<DatabaseHelper>(this,android.R.layout.simple_list_item_1, android.R.id.text1,  dbHelper);
       // listView.setAdapter(adapter);
    //    TextView tv1 = (TextView)findViewById(R.id.textView2);
    //    tv1.setText(updEmple.getSMSData(contentResolver));
        Button contactButton = (Button)findViewById(R.id.contact_button);
        contactButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                switch(v.getId())
                {
                    case R.id.contact_button:
                        Intent newActivity = new Intent("android.intent.action.CONTACTS");
                       // newActivity.putExtra("list", contacts);
                        startActivity(newActivity);
                        break;
                }
            }
        });

        //getSMSData2 updEmple = new getSMSData2();
        //TextView tv1 = (TextView)findViewById(R.id.textView2);
        //tv1.setText(updEmple.getSMSData(contentResolver));
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
    /*private void showProgressDialog(String message) {
        //
        progressDialogInbox.setMessage(message);
        progressDialogInbox.setIndeterminate(true);
        progressDialogInbox.setCancelable(true);
        progressDialogInbox.setButton(ProgressDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stopThread();
            }
        });
        progressDialogInbox.show();
    }

    private void fetchInboxMessages() {
        showProgressDialog("TextMate is Fetching Inbox Messages...");
        startThread();
    }

    private FetchMessageThread fetchMessageThread;
    private int currentCount = 0;

    // FetchMessageThread to create a parallel thread in order for
    // the application to fetch the sms data and populate thread data.
    public class FetchMessageThread extends Thread {
        //
        public int tag = -1;

        public FetchMessageThread(int tag) {
            this.tag = tag;
        }

        @Override
        public void run() {
            fetchSms();
            progressDialogInbox.dismiss();
        }
    }

    public synchronized void startThread() {
        if (fetchMessageThread == null) {
            fetchMessageThread = new FetchMessageThread(currentCount);
            fetchMessageThread.start();
        }
    }

    public synchronized void stopThread() {
        if (fetchMessageThread != null) {
            Log.i("Cancel thread", "stop thread");
            FetchMessageThread moribund = fetchMessageThread;
            currentCount = fetchMessageThread.tag == 0 ? 1 : 0;
            fetchMessageThread = null;
            moribund.interrupt();
        }
    }

    public void fetchSms() {
        ContentResolver contentResolver = getContentResolver();

        Cursor cursor = contentResolver.query(
                Uri.parse("content://sms"),
                new String[]{"thread_id", "address", "person", "date", "body", "type"},
                null,
                null,
                null);

        try {
            if (cursor.moveToLast()) {
                do {
                    // to grab the data
                    long t_id = cursor.getLong(cursor.getColumnIndex("thread_id"));
                    String address = cursor.getString(cursor.getColumnIndex("address"));
                    int person = cursor.getInt(cursor.getColumnIndex("person"));
                    long date = cursor.getLong(cursor.getColumnIndex("date"));
                    String body = cursor.getString(cursor.getColumnIndex("body"));
                    int type = cursor.getInt(cursor.getColumnIndex("type"));
                    //long dateSent = cursor.getLong(cursor.getColumnIndex("date_sent"));
                    //
                    try {
                        switch (type) {
                            case 1:
                                String flag = "INCOMING";
                                dbHelper.insert_sms(t_id, address, person, date, body, type, flag);
                                break;
                            case 2:
                                flag = "OUTGOING";
                                dbHelper.insert_sms(t_id, address, person, date, body, type, flag);
                                break;
                        }
                    } catch (SQLException e) {
                        Log.d("fetchSMS -> ", "INSERTION Failed!");
                    }
                } while (cursor.moveToPrevious());
                cursor.close();
            }
        } catch (SQLException e) {
            Log.d("fetchSms->", "INSERTION Failed!");
        }

        /* After fetchSMS is done, the dbHelper object will invoke the
        * populateThread function to correctly populate the Thread table.
        */
  /*      try {
            dbHelper.populateThread();
        } catch (SQLException e) {
            Log.d("fetchSMS(pThread) -> ", "INSERTION Failed!");
        }
    }

    //Use the textMateData Class to find the averages needed for the Algorithm.
    //Then put the data into the sms data table.
    public void populateData(DatabaseHelper dbHelper){
        ArrayList<Long> threadIDs = dbHelper.getThreadID();
        for(int pos=0;pos<threadIDs.size();pos++) {
            dbData = new textMateData(threadIDs.get(pos),dbHelper);
            dbData = null;
        }
    }

    //Use the Algorithm Class to Find the scores based on the data found in
    // populate data. Then takes the scores and populate the thread table.
    public void populateScores(DatabaseHelper dbHelper){
        // Pass the values into the Constructor
        ArrayList<Long> threadIDs = dbHelper.getThreadID();
        for(int pos=0;pos<threadIDs.size();pos++) {
            scoreData = new alg(threadIDs.get(pos),dbHelper);
            scoreData=null;
        }
    } */
}



