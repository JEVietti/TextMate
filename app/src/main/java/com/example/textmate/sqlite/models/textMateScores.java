package com.example.textmate.sqlite.models;

import java.lang.String;

public class textMateScores {
    private int id, numUpdate;
    private int numScores = 30;
    private String name;
    private double[] score = new double[numScores];

   //Constructor
   //Empty Constructor
    public textMateScores(){}
    //Base Constructor
    public textMateScores(int id,String name,double[] score, int numUpdate){
        this.id=id;
        this.name=name;
        for(int i=0;i<numScores;i++){
        this.score[i]=0;}
        this.numUpdate=0;
    }

    //Class Methods
    public int fetchID(){return this.id;}
    public void setID(int ID){this.id=ID;}

    public String fetchName(){return this.name;}
    public void setName(String newName){this.name=newName;}

    public int fetchNumUpdate(){return this.numUpdate;}
    public void setNumUpdate(int newNumUpdate){this.numUpdate=newNumUpdate;}

    public double fetchScore(int posScore){return this.score[(this.numUpdate % numScores)];}
    public void setScore(double newScore){
        this.score[this.numUpdate%numScores] = newScore;
        this.setNumUpdate(this.numUpdate+1);
    }


}
