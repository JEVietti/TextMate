package com.example.textmate.sqlite.models;

import java.lang.String;

//Class for creating an object to set all of the columns
//for each Contact in the TextMateData Table

public class textMateData {
    //Class Variables
    int id, numUpdate, charCount, msgCount;
    String name;
    double diffTimeSent, diffTimeReceive;
    String createdTime; //keeps track when the database is created
    //Constructors
    public textMateData(){} //Empty Constructor
    //Constructor to initialize the data into the object of the class
    public textMateData(int id, String name, int charCount,int msgCount,double diffTimeSent,double diffTimeReceive, int numUpdate){
        this.id = id;
        this.name = name;
        this.charCount = charCount;
        this.msgCount = msgCount;
        this.diffTimeSent = diffTimeSent;
        this.diffTimeReceive = diffTimeReceive;
        this.numUpdate = numUpdate;
    }

    //Class Methods for setting the Values of the Columns for the textMateData Table

    // Get the id of a row
    public int fetchID(){return this.id;}

    // Set a new ID
    public void setId(int newID){this.id=newID;}

    // Get the Name of row
    public String fetchName(){return this.name;}

    // Set a new Name for a row
    public void setName(String newName){this.name=newName;}

    //
    public int fetchNumUpdate(){return this.numUpdate;}

    //
    public void setNumUpdate(){
        int ans;
        ans = fetchNumUpdate()+1;
        this.numUpdate=ans;}

    // Get the Character count of a row
    public int fetchCharCount(){return this.charCount;}

    // Set the New Character Count
    public void setCharCount(int newCharCount){this.charCount = newCharCount;}

    // get the current Message Count
    public int fetchMsgCount(){return this.msgCount;}

    // Set the new Message Count
    public void setMsgCount(int newMsgCount){this.msgCount = newMsgCount;}

    //
    public double fetchDiffTimeSent(){return this.diffTimeSent;}

    //
    public void setDiffTimeSent(double newDiffTimeSent,int numUpdate){
        double input1 = fetchDiffTimeSent();
        double temp,ans;
        temp = input1 * numUpdate;
        ans = ((temp + newDiffTimeSent)/(numUpdate+1));
        this.diffTimeSent = ans;
    }

    //
    public double fetchDiffTimeReceive(){return this.diffTimeReceive;}

    //
    public void setDiffTimeReceive(double newDiffTimeReceive){
        int numUpdate = fetchNumUpdate();
        double input1 = fetchDiffTimeReceive();
        double temp, ans;
        temp = input1 * numUpdate;
        ans = ((temp+newDiffTimeReceive)/(numUpdate+1));
        this.diffTimeReceive = ans;
    }

}
