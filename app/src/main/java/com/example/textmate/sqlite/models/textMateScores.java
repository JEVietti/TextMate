package com.example.textmate.sqlite.models;

import java.lang.String;

public class textMateScores {
    private int id, numUpdate;
    private String name;

    private double newScore,yestScore,avgScore;
   //Constructor
   //Empty Constructor
    public textMateScores(){}
    //Base Constructor
    public textMateScores(int id,String newName,double newScore,double yesterdayScore,double avgScore, int numUpdate){
        this.id = id;
        this.name = newName;
        this.newScore = newScore;
        this.yestScore = yesterdayScore;
        this.avgScore = avgScore;
        this.numUpdate = numUpdate;
    }

    //Class Methods
    public int fetchID(){return this.id;}
    public void setID(int ID){this.id=ID;}

    public String fetchName(){return this.name;}
    public void setName(String newName){this.name=newName;}

    public int fetchNumUpdate(){return this.numUpdate;}
    public void setNumUpdate(int newNumUpdate){this.numUpdate=newNumUpdate;}


    public double fetchCurrScore(){return this.newScore;}

    public void setNewYestScore(){this.yestScore=this.fetchCurrScore();}

    public double fetchAvgScore(){return this.avgScore;}
    public void setAvgScore(){
        double oldAvg = fetchAvgScore();
        double temp1,ans;
        temp1 = oldAvg*this.numUpdate;
        ans=temp1+fetchCurrScore();
        this.setNumUpdate(this.numUpdate+1);
        ans = ans/this.numUpdate;
        this.avgScore=ans;
    }

}
