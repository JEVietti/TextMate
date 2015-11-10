package com.example.textmate.sqlite.models;

import java.lang.String;
import java.lang.Math;
import com.example.textmate.MainActivity;
import com.example.textmate.sqlitehelper.DatabaseHelper;

//Class for creating an object to set all of the columns
//for each Contact in the TextMateData Table

public class textMateData {
    //Class Variables
    private int id, numUpdate, charCount, msgCount;
    private String name;
    int count;
    private int[] diffTimeSent = new int [count];
    private int[] diffTimeReceive = new int[count];
    String createdTime; //keeps track when the database is created
    //Constructors
    public textMateData(){} //Empty Constructor
    //Constructor to initialize the data into the object of the class
    public textMateData(int id, String name, int charCount,int msgCount,int diffSentVal[],int diffReceiveVal[], int numUpdate,int count){
        this.count = count;
        this.id = id;
        this.name = name;
        this.charCount = charCount;
        this.msgCount = msgCount;
        for(int i=0;i<this.count;i++){
        this.diffTimeSent[i] = diffSentVal[i];
        this.diffTimeReceive[i] = diffReceiveVal[i];
        }
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
    public int[] fetchDiffTimeSent(){return this.diffTimeSent;}

    //
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
    //
    public int[] fetchDiffTimeReceive(){return this.diffTimeReceive;}

    //
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
