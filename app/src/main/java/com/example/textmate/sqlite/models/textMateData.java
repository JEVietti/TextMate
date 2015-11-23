package com.example.textmate.sqlite.models;

import java.lang.String;
import java.lang.Math;
import com.example.textmate.MainActivity;
import com.example.textmate.sqlitehelper.DatabaseHelper;

//Class for creating an object to set all of the columns
//for each Contact in the TextMateData Table

public class textMateData {
    //Class Variables
    private int id, numUpdate, wordCount, msgCount;
    private String name;
    int count;
    private double newAvgTimeSent, newAvgTimeRec, newAvgWordCount;
    private int[] diffTimeSent = new int [count];
    private int[] diffTimeReceive = new int[count];
    String createdTime; //keeps track when the database is created
    //Constructors
    public textMateData(){} //Empty Constructor
    //Constructor to initialize the data into the object of the class
    public textMateData(int wordCount,int msgCount,int diffSentVal[],int diffReceiveVal[],int numUpdate,int count){
        this.count = count;
        //this.id = id;
        //this.name = name;
        this.wordCount = wordCount;
        this.msgCount = msgCount;
        for(int i=0;i<this.count;i++){
        this.diffTimeSent[i] = diffSentVal[i];
        this.diffTimeReceive[i] = diffReceiveVal[i];
        }
        this.newAvgTimeRec=this.setDiffTimeReceive();
        this.newAvgTimeSent=this.setDiffTimeSent();
        this.newAvgWordCount=this.setAvgWordCount();
        this.numUpdate = numUpdate;
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
    public int fetchNumUpdate(){return this.numUpdate;}

    //Increment the number of updates so the Averages for diff time receive and sent can be calculated
    public void incNumUpdate(){
        int ans;
        ans = fetchNumUpdate()+1;
        this.numUpdate=ans;}

    //Gives access to AvgWordCount per Message fpr each Contact to put back into Database
    public double fetchAvgWordCount(){return this.newAvgWordCount;}

    //Calculates the Average amount of words per message for each contact in the SMS table
    public double setAvgWordCount(){
        double ans;
        ans = this.wordCount/this.msgCount;
        return ans;
    }

    //Gives access to AvgTimeSent to put back into Database
    public double fetchDiffTimeRSent(){return this.newAvgTimeSent;}

    //Calculates and Sets the Average Difference in time between Sent Messages of a Single Contact
    public double setDiffTimeSent() {
        double temp=0.0,ans = 0.0;
        for (int i = 0; i < (this.count); i++) { ans+=temp;
            for (int j = i; j < i+1; j++) {
                temp = Math.abs(this.diffTimeSent[j] - this.diffTimeSent[j + 1]);
            }
        }
        ans = ans / this.count;
        return ans;
    }
    //Gives access to the AvgTimeReceived to put it back into a database
    public double fetchDiffTimeReceive(){return this.newAvgTimeRec;}

    //Calculates and sets the Average Difference in time bewtween Received Messages of a Single Contact
    public double setDiffTimeReceive(){
        double temp=0.0,ans = 0.0;
        for (int i = 0; i < (this.count); i++) { ans+=temp;
            for (int j = i; j < i+1; j++) {
                temp = Math.abs(this.diffTimeReceive[j] - this.diffTimeReceive[j + 1]);
            }
        }
        ans = ans / this.count;
        return ans;
    }

}
