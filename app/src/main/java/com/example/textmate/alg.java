
/* This is an outline of the Algorithm class I think we will need
* As it stands it is very generic and doesn't have pointers in the class
*  variables or parameters of the functions due to not knowing how the data
*  is going to be retrieved from the SMS messenger database*/

        package com.example.textmate;

//Unsure all the imports we will need to retrieve and manipulate data
// add more imports to the list if you know what we will be needing
        import android.util.Log;

        import java.lang.Class;
        import java.lang.String;
        import java.lang.Math;
        import java.text.DecimalFormat;

        import com.example.textmate.MainActivity;
        import com.example.textmate.sqlitehelper.DatabaseHelper;
//This will serve as the bare bones for creating the algorithm
//from the data stored in the SMS database on the phone inherently


//(This is just an idea feel free to edit it as you wish)
// The algorithm weights will be as follows: the delta time of texts received and sent
// will have a weight of 2x -> x being number of texts between a day
// Weight of #of characters =>
public class alg{

    //Constructor I'm thinking we need need this as a way to grab data
    //from the data base to store it into an object for each date we want
    //in order to compute this is just a thought of what it would be like

    //Class Variables
    private int numUpdate; //Used to keep track of divisor and multiplier in order to find the averages
    // For instance 100+100+50/3 = 83.3 != ((100+100/2)+50)/2 = 75
     // so to fix this the divisor will be counted and kept track with the number of updates to the values
     // using it to multiply the initial value then add the new val increment divisor to find the new average.
    private double valNum,valSize,valTime; //Store Value of Yesterday and Today to compare
    private double newValueYesterday, oldValueAvg, newValueAvg, valueToday,currScore;              //idea to store the average value
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

        this.valSize = valSizeTimeTxt(this.WordPerMessage,this.avgTimeSent,this.avgTimeRec) ;
        this.valNum = valSizeNumTxt(this.msgCount, this.wordCount);
        this.valTime = valTimeTxt(this.avgTimeSent, this.avgTimeRec);
        this.valueToday = this.weighValues(this.valNum, this.valSize,this.valTime);
        this.newValueAvg = this.NewAvgScore(this.oldValueAvg,this.valueToday,this.numUpdate);
        this.relStatus = printStatusRel(this.valueToday,this.newValueYesterday,this.newValueAvg);
        upDBScores.updateScoresOfThreadTable(ID,this.valueToday,this.newValueYesterday,this.newValueAvg,(this.numUpdate),this.relStatus);
    }
//Function Definitions and How the weights will be computed
/*  May not need this part because of Built in class functions for models

*/
/* For the function valNumTxt -> the value assigned based on the # of texts
  * between a certain time frame i.e. 24 hour cycle
  * The weight of which will be (#texts / time) */
    //Value for the number of texts in a day
     public double valSizeNumTxt(int MC,int WC){return(2*WC)+MC/100; }

    /*For the function valSizeTxt -> the value assigned based on
     * the Average size of the texts
     * based on the amount of characters over a certain time period i.e. 24 hour
     * The weight = ((#word total for all texts during day/ # of texts that day)/ time)*/
    //Value for the average size of texts in a day
    public double valSizeTimeTxt(double WPM,double TS,double TR){
        double TSR = Math.abs(TS-TR);
        return (TSR/WPM);
    }

    /* For the Function valTimeTxt -> the value assigned based on
    * the Average time between messages throughout the day,
    * between the messages received from a certain contact, time between messages
    * sent to the contact and the time between a message sent and received
    * from the contact all will be combined into a single value
    * */
    //Value for the time for both received and sent messages
    public double valTimeTxt(double tS,double tR) {
        double ans, tSR;

            tSR = Math.abs(tS - tR);
            ans = (tS + tR); //Temp = Avg timeSent + 2*timeRec
            ans = ans - tSR;       //Temp = Temp - Avg TimeBetweenSentAndReceived
            return ans / 24; //Temp/hours in a day
    }

    /* The function weighValues is used to combine the values of the previously
    * defined functions: valNumTxt, valSizeTxt, and valTime
    *  => into a single value score which will be stored into a variable valToday
    *  */
    //Combined value for the functions above
    public double weighValues(double v1,double v2,double v3){
        double ans = ((2*v1)+(3*v2)+(2*v3))/3;
        ans=ans/100;
        ans = Double.parseDouble(new DecimalFormat("#.##").format(ans));
        return ans;
    }

    //Calculates the newAverage Score for
    public double NewAvgScore(double originalAvg, double newScore, int num){
        double temp1, temp2,ans;
        temp1 = originalAvg * this.numUpdate;
        temp2 = temp1 + newScore;
        incrementNumUpdate();
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
    public String printStatusRel(double v1,double v2,double v3){
        String ans, ans1, ans2;
        double BaseScore1 = 50.0; //The Base Score to compare users Scores with will update
        double BaseScore2 = 15.0;  //score based on testing later in development
        if(v3 >= BaseScore1||v3<BaseScore2)
            ans1="Your relationship is in Bad Standing";
        else
            ans1="Your relationship is in Good Standing";

        if(v1==v2||v1==v3)
            ans2="is consistent";
        else if (v1 < v2||v1 < v3)
            ans2="is getting better!";
        else
            ans2="is getting worse!";
         ans=ans1+" and "+ans2;
        return ans;
    }
    //Retrieve the Values of the Constructor to keep all the Class Variables Private that will
    //be pushed back into the table
    public int incrementNumUpdate(){return this.numUpdate++;}

//End of Class
}