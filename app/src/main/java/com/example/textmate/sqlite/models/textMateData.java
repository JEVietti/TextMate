package com.example.textmate.sqlite.models;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import com.example.textmate.sqlitehelper.DatabaseHelper;
import java.lang.String;
import java.lang.Math;
import java.util.Iterator;

//Class for creating an object to set all of the columns
//for each Contact in the TextMateData Table

public class textMateData {
    //
    SQLiteDatabase sdb;
    DatabaseHelper tdb;

    public static final String MSG_RECEIVE_TYPE = "1";
    public static final String MSG_SENT_TYPE = "2";

    //Class Variables
    private Long id;
    private int numUpdate, wordCount, msgCount;
    private String name;
    private double newAvgTimeSent, newAvgTimeRec, newAvgWordCount;
    private String createdTime;     //keeps track when the database is created

    //Constructor to initialize the data into the object of the class
    public textMateData(Long id) {
        this.id = id;
    }

    // This will populate both diff_sent_time and diff_return_time column
    // In threads table.
    public void initialize() {
        //
        setDiff(tdb.DATE_RECEIVED);
        setDiff(tdb.DATE_SENT);
    }

    // Updates the diff_time columns in the database
    public void setDiff(String dateType) {
        //
        ContentValues val = new ContentValues();

        String fetchSMSList =   "SELECT strftime('%s', %s) AS diff " +
                                "FROM sms " +
                                "WHERE type=%s AND thread_id=%s;";

        if (dateType.equals(tdb.DATE_RECEIVED)) {
            Log.i("INFO(setDiff/IF)", tdb.DATE_RECEIVED);
            Log.i("INFO(setDiff/IF)", Long.toString(this.id));

            fetchSMSList = String.format(fetchSMSList,
                    "%s", dateType, MSG_RECEIVE_TYPE, Long.toString(this.id));

            Log.i("INFO(setDiff/IF)", fetchSMSList);

            ArrayList<Long> diffReceiveTimeList = tdb.executeQuery(fetchSMSList);

            Double diffReturnTime = calcDiffTime(diffReceiveTimeList);

            val.put(tdb.DIFF_RETURN_TIME, diffReturnTime);
            try {
                sdb.update(tdb.THREAD_TABLE, val, "_id=" + this.id, null);
            } catch (SQLException e) {
                Log.d("setDiff(IF)->", "UPDATE Failed!");
            }
        }
        else {
            Log.i("INFO(setDiff/ELSE)", tdb.DATE_SENT);
            Log.i("INFO(setDiff/ELSE)", Long.toString(this.id));

            fetchSMSList = String.format(fetchSMSList,
                    "%s", dateType, MSG_SENT_TYPE, Long.toString(this.id));

            ArrayList<Long> diffSentTimeList = tdb.executeQuery(fetchSMSList);

            Double diffSentTime = calcDiffTime(diffSentTimeList);

            val.put(tdb.DIFF_SENT_TIME, diffSentTime);
            try {
                sdb.update(tdb.THREAD_TABLE, val, "_id=" + this.id, null);
            } catch (SQLException e) {
                Log.d("setDiff(ELSE)->", "UPDATE Failed!");
            }
        }
    }

    //Calculates and Sets the Average Difference in time between Sent Messages of a Single Contact
    public Double calcDiffTime(ArrayList<Long> diffList) {
        Double ans = 0.0;
        Iterator<Long> it = diffList.iterator();

        Log.i("INFO(setDiffTime)", diffList.toString());

        while (it.hasNext()) {
            ans = Math.abs(ans - it.next());
            //temp = Math.abs(this.list.get(j) - this.list.get(j + 1));
            it.remove();
        }
        ans = ans / diffList.size();

        return ans;
    }

    //Class Methods for setting the Values of the Columns for the textMateData Table

    // Get the id of a row
    //public int fetchID(){return this.id;}

    // Set a new ID
    //public void setId(int newID){this.id=newID;}

    // Get the Name of row
    // public String fetchName(){return this.name;}

    // Set a new Name for a row
    //public void setName(String newName){this.name=newName;}

    //Returns the Number of Updates which may be useful because of incrementing
    /*public int fetchNumUpdate() { return this.numUpdate; }

    //Increment the number of updates so the Averages for diff time receive and sent can be calculated
    public void incNumUpdate(){
        int ans;
        ans = fetchNumUpdate()+1;
        this.numUpdate=ans;}

    //Gives access to AvgWordCount per Message fpr each Contact to put back into Database
    public double fetchAvgWordCount() { return this.newAvgWordCount; }

    //Calculates the Average amount of words per message for each contact in the SMS table
    public double setAvgWordCount() {
        //
        double ans;
        ans = this.wordCount/this.msgCount;
        return ans;
    }

    //Gives access to AvgTimeSent to put back into Database
    public double fetchDiffTimeRSent() { return this.newAvgTimeSent; }

    //Gives access to the AvgTimeReceived to put it back into a database
    public double fetchDiffTimeReceive() { return this.newAvgTimeRec; }*/

    //Calculates and sets the Average Difference in time bewtween Received Messages of a Single Contact
    /*public double setDiffTimeReceive() {
        //
        double temp=0.0,ans = 0.0;
        for (int i = 0; i < (this.list.size()); i++) { ans+=temp;
            for (int j = i; j < i+1; j++) {
                temp = Math.abs(this.list.get(j) - this.list.get(j + 1));
            }
        }
        ans = ans / this.list.size();
        return ans;
    }*/
}
