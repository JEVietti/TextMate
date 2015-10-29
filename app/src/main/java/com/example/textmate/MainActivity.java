
package com.example.textmate;

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


public class MainActivity extends ActionBarActivity {
    DatabaseHelper textMateDB;
    //textDB myTextDB;
    //textScoreDB myScoreDB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textMateDB = new DatabaseHelper(this);
        getSMSData();
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


    public void getSMSData() {
        //Note, date coming from http://developer.android.com/reference/java/text/SimpleDateFormat.html
        ArrayList<String[]> SMSData = new ArrayList<String[]> ();
        String [] smsMessage = {};
        String [] emptyVar = {};

        ContentResolver contentResolver = getContentResolver();
        //Inbox = "content://sms/inbox"
        //Sent = "content://sms/sent"

        Cursor smsInboxCursor = contentResolver.query(Uri.parse("content://sms/inbox"), null, null, null, null); //Point to inbox
        int indexBody = smsInboxCursor.getColumnIndex("body"); //Point to body column
        int indexAddress = smsInboxCursor.getColumnIndex("address"); //Point to sender's # column
        int indexDate = smsInboxCursor.getColumnIndex("date"); //point to date column

        if (indexBody < 0 || !smsInboxCursor.moveToFirst()) return; //no messages
        //arrayAdapter.clear();
        do {
            smsMessage = emptyVar;
            String addressSender = smsInboxCursor.getString(indexAddress); //sender's #
            String body = smsInboxCursor.getString(indexBody); //body of text
            Long dateMs = Long.parseLong(smsInboxCursor.getString(indexDate), 10);
            Date dateFromSms = new Date(dateMs);
            SimpleDateFormat simpleDate =  new SimpleDateFormat("dd/MM/yyyy HH:mm:ss.SSSZ");
            String strDt = simpleDate.format(dateFromSms); //date

            //Pushing strings into local array
            smsMessage = push(smsMessage, addressSender);
            smsMessage = push(smsMessage, body);
            smsMessage = push(smsMessage, strDt);
            SMSData.add(smsMessage); //pushes local array into SMS list
        } while (smsInboxCursor.moveToNext());

        //Printing to make sure it works
        TextView tv1 = (TextView)findViewById(R.id.textView2);
        String str2 = " ";
        for(int i=0; i < SMSData.size(); i++){
            str2 += "Address = " + SMSData.get(i)[0] + "\n" +
                    "Body = " + SMSData.get(i)[1] + "\n" +
                    "Date = " + SMSData.get(i)[2] + "\n";
        }
        tv1.setText(str2);
        return;

    }

    private static String[] push(String[] array, String push) { //push function
        String[] longer = new String[array.length + 1];
        System.arraycopy(array, 0, longer, 0, array.length);
        longer[array.length] = push;
        return longer;
    }

}
