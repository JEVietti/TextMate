
package com.example.textmate;

//Unsure all the imports we will need to retrieve and manipulate data
// add more imports to the list if you know what we will be needing
import android.util.Log;

import java.lang.String;
import java.lang.Math;
import java.text.DecimalFormat;
import com.example.textmate.sqlitehelper.DatabaseHelper;
//This will serve as the bare bones for creating the algorithm
//from the data stored in the SMS database on the phone inherently


/* The algorithm is based on the idea of the lim as x->infinity 1/x = 0
* The metadata we are using is the amount of words, message count,words per message, and the
 * average change in time between the received and sent messages.
* So to reward a greater amount of words and messages and a smaller amount of time between said messages
* The average change in time needs to be the numerator and the other data be the denominator otherwise the user
* is rewarded for having a greater change in time instead of a smaller amount
* As the goal is to have the closest possible score to zero is a good relationship
* */
public class alg{

    //Constructor I'm thinking we need need this as a way to grab data
    //from the data base to store it into an object for each date we want
    //in order to compute this is just a thought of what it would be like

    //Class Variables
    private int numUpdate; //Used to keep track of divisor and multiplier in order to find the averages
    // For instance 100+100+50/3 = 83.3 != ((100+100/2)+50)/2 = 75
    // so to fix this the divisor will be counted and kept track with the number of updates to the values
    // using it to multiply the initial value then add the new val increment divisor to find the new average.
    private double valTimeNum,valTimeSize,valTimeWPM; //Store Value of Yesterday and Today to compare
    private double newValueYesterday, oldValueAvg, newValueAvg, valueToday,currScore;              //idea to store the average value
    private int valSize;
    //and use it as a way to interpret the progress of the relationship
    private String relStatus;  //Prints to the user how the relationship is going with a contact
    private int wordCount,msgCount, newNumUpdate;
    private double avgTimeRec,avgTimeSent,WordPerMessage;
    //Constructors
    public alg(Long ID,DatabaseHelper upDBScores){
        this.WordPerMessage = upDBScores.queryThreadIDWordPerMessageCount(ID);
        this.wordCount = upDBScores.queryThreadsWordCount(ID);
        this.numUpdate = upDBScores.queryThreadIDNumUpdates(ID);
        this.msgCount = upDBScores.queryThreadIDMessageCount(ID);
        this.avgTimeRec = upDBScores.queryThreadIDTimeAverages(ID, "diff_return_time");
        this.avgTimeSent = upDBScores.queryThreadIDTimeAverages(ID,"diff_sent_time");
        this.currScore = upDBScores.queryThreadIDScores(ID, "today_score");
        this.newValueYesterday = currScore;
        this.oldValueAvg = upDBScores.queryThreadIDScores(ID,"average_score");

        this.valSize = this.valSizeNumTxt(this.msgCount,this.wordCount) ;
        this.valTimeNum = this.valTimeTxtCount(this.avgTimeSent,this.avgTimeRec,this.msgCount);
        this.valTimeSize = this.valTimeTxtSize(this.avgTimeSent, this.avgTimeRec,this.wordCount);
        this.valTimeWPM = this.valTimeTxtWPM(this.avgTimeSent,this.avgTimeRec,this.WordPerMessage);
        this.valueToday = this.weighValues(this.valSize,this.valTimeNum,this.valTimeSize,this.valTimeWPM);
        this.newValueAvg = this.NewAvgScore(this.oldValueAvg,this.valueToday,this.numUpdate);
        this.relStatus = printStatusRel(this.valueToday,this.newValueYesterday,this.newValueAvg);
        upDBScores.updateScoresOfThreadTable(ID,this.valueToday,this.newValueYesterday,this.newValueAvg,(this.numUpdate),this.relStatus);
    }
    //Function Definitions and How the weights will be computed
/*  May not need this part because of Built in class functions for models

*/
// For the function valNumTxt -> the value assigned based on the # of texts and messages
    private int valSizeNumTxt(int MC,int WC){return MC+WC; }

    //Value for the average size of texts in a day divided by the words per message
    private double valTimeTxtWPM(double TS,double TR,double WPM){
        double TSR = Math.abs(TS-TR);
        return (TSR/WPM);
    }


    //Value for the time for both received and sent messages and the difference between times
    // divided by the number of total messages between the contact
    private double valTimeTxtCount(double tS,double tR,int numMessage) {
        double ans, tSR;

        tSR = Math.abs(tS - tR);
        ans = (tS + tR); //Temp = Avg timeSent + 2*timeRec
        ans = (ans - tSR);       //Temp = Temp - Avg
        return ans /numMessage;
    }
    //creates a score based on the amount of total words compared to the difference between
    //the average time between messages sent and received
    private double valTimeTxtSize(double tS,double tR,int wordCount){
        double ans, tSR;
        tSR = Math.abs(tS - tR);
        ans = tSR/wordCount;
        return ans;
    }

    /* The function weighValues is used to combine the values of the previously
    * defined functions: valNumSize, valTimeTxtSize,valTimeTxtCount, valTimeTxtWPM
    *  => into a single value score which will be stored into a variable valToday
    *  */
    //Combined value for the functions above
    private double weighValues(int size,double timeMSGC,double timeWC,double timeWPM){
        double ans = (((3*timeWPM)+(2*timeMSGC)+(3*timeWC))/size);
        ans = Double.parseDouble(new DecimalFormat("#.##").format(ans));
        return ans;
    }

    //Calculates the newAverage Score for
    private double NewAvgScore(double originalAvg, double newScore, int num){
        double temp1, temp2,ans;
        temp1 = originalAvg * this.numUpdate;
        temp2 = temp1 + newScore;
        this.incrementNumUpdate();
        ans = temp2/this.numUpdate;
        ans = Double.parseDouble(new DecimalFormat("#.##").format(ans));
        return ans;
    }

    /* The Function printStatusRel is used as a comparison function of the
     * values from the weighValues of yesterday and today to an output string
      * where v1 - today and v2 - yesterday
      * for example (v1<v2)&&(v1<baselineVal)
      * => " Your relationship is trending down and is not in  a good place "
      * */
    //Comparison function of relationship values of today(v1) and yesterday(v2) and AVG(v3)
    private String printStatusRel(double v1,double v2,double v3){
        String ans, ans1, ans2;
        double BaseScore1 = 10.0; //The Base Score to compare users Scores with will update
        double BaseScore2 = 0.0;  //score based on testing later in development
        if(v3 >= BaseScore1||v3==BaseScore2)
            ans1="Your relationship is in Bad Standing";
        else
            ans1="Your relationship is in Good Standing";

        if(v1==v2||v1==v3)
            ans2="is consistent!";
        else if (v1 < v2||v1 < v3)
            ans2="is getting better!";
        else
            ans2="is getting worse!";
        ans=ans1+" and "+ans2;
        return ans;
    }
    //Retrieve the Values of the Constructor to keep all the Class Variables Private that will
    //be pushed back into the table
    private int incrementNumUpdate(){return this.numUpdate++;}

//End of Class
}
