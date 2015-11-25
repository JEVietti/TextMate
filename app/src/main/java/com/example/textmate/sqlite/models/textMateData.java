package com.example.textmate.sqlite.models;

import android.database.SQLException;
import android.util.Log;

import java.util.ArrayList;
import com.example.textmate.sqlitehelper.DatabaseHelper;
import java.lang.String;
import java.lang.Math;

//Class for creating an object to set all of the columns
//for each Contact in the TextMateData Table

public class textMateData implements DatabaseHelper {
    //
    DatabaseHelper db;

    //Class Variables
    private int id, numUpdate, wordCount, msgCount;
    private String name;
    private double newAvgTimeSent, newAvgTimeRec, newAvgWordCount;
    private String createdTime;     //keeps track when the database is created

    //Constructor to initialize the data into the object of the class
    public textMateData(int id) {
        this.id = id;
    }

    //
    public void initialize() {
        //
        setDiff(db.DATE_RECEIVED);
        setDiff(db.DATE_SENT);
    }

    public void setDiff(String dateType) {
        //
        if (dateType.equals(db.DATE_RECEIVED)) {
            ArrayList<Double> diffReceived =
                    db.querySMSList(dateType, 1, this.id);
            double diffReturnTime = setDiffTimeReceive(diffReceived);
            try {
                db.update(diffReturnTime);
            } catch (SQLException e) {
                Log.d("setDiff->", "UPDATE Failed!");
            }
        }
        else {
            ArrayList<Double> diffSent =
                    db.querySMSList(dateType, 2, this.id);
            double diffSentTime = setDiffTimeSent(diffSent);
            try {
                db.update(diffSentTime);
            } catch (SQLException e) {
                Log.d("setDiff->", "UPDATE Failed!");
            }
        }
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
    public int fetchNumUpdate() { return this.numUpdate; }

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

    //Calculates and Sets the Average Difference in time between Sent Messages of a Single Contact
    public double setDiffTimeSent() {
        double temp=0.0,ans = 0.0;
        for (int i = 0; i < (list.size()); i++) { ans+=temp;
            for (int j = i; j < i+1; j++) {
                temp = Math.abs(this.list.get(j) - this.list.get(j+1));
            }
        }
        ans = ans / this.list.size();
        return ans;
    }
    //Gives access to the AvgTimeReceived to put it back into a database
    public double fetchDiffTimeReceive() { return this.newAvgTimeRec; }

    //Calculates and sets the Average Difference in time bewtween Received Messages of a Single Contact
    public double setDiffTimeReceive() {
        //
        double temp=0.0,ans = 0.0;
        for (int i = 0; i < (this.list.size()); i++) { ans+=temp;
            for (int j = i; j < i+1; j++) {
                temp = Math.abs(this.list.get(j) - this.list.get(j + 1));
            }
        }
        ans = ans / this.list.size();
        return ans;
    }
}
