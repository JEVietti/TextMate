
/* This is an outline of the Algorithm class I think we will need
* As it stands it is very generic and doesn't have pointers in the class
*  variables or parameters of the functions due to not knowing how the data
*  is going to be retrieved from the SMS messenger database*/

        package com.example.textmate;

//Unsure all the imports we will need to retrieve and manipulate data
// add more imports to the list if you know what we will be needing
        import java.lang.Class;
        import java.lang.String;

//This will serve as the bare bones for creating the algorithm
//from the data stored in the SMS database on the phone inherently


//(This is just an idea feel free to edit it as you wish)
// The algorithm weights will be as follows: the delta time of texts recieved and sent
// will have a weight of 2x -> x being number of texts between a day
// Weight of #of characters =>
public class alg{
    public double newDiffTimeSent,NewDiffTimeRec;

    //Constructor I'm thinking we need need this as a way to grab data
    //from the data base to store it into an object for each date we want
    //in order to compute this is just a thought of what it would be like
//Unfinished Constructor
public alg(int numTxt,int txtNumChar,double txtTimeSent,double txtTimeReceived){}

    //Class Variables
    int divisor; //Used to keep track of divisor and multiplier in order to find the averages
    // For instance 100+100+50/3 = 83.3 != ((100+100/2)+50)/2 = 75
     // so to fix this the divisor will be counted and kept track with the number of updates to the values
     // using it to multiply the initial value then add the new val increment divisor to find the new average.
    int valNumCharacters;
     int valueYesterday,valueToday; //Store Value of Yesterday and Today to compare
     int valueMonth; //idea to store the average value over a month span
    //and use it as a way to interpret the progress of the relationship
    String relStatus;  //Prints to the user how the relationship is going with a contact
//Function Definitions and How the weights will be computed
/*  May not need this part because of Built in class functions for models
    //Compute the Average values for the Columns in the Database other than Time Values which are doubles
    public void computeAverageValues(alg obj){
        int res = 0;


    }
    //Find the Average value of Diff Time to push to the database correctly
    public void computeAverageTime(){
        double res = 0.0;

    }

*/
/* For the function valNumTxt -> the value assigned based on the # of texts
  * between a certain time frame i.e. 24 hour cycle
  * The weight of which will be (#texts / time) */
    //Value for the number of texts in a day
     public double valNumTxt(int x,double t){return x/t;}

    /*For the function valSizeTxt -> the value assigned based on
     * the Average size of the texts
     * based on the amount of characters over a certain time period i.e. 24 hour
     * The weight = ((#word total for all texts during day/ # of texts that day)/ time)*/
    //Value for the average size of texts in a day
    double valSizeTxt(int x,int y,double t){ return ((x/y)/t);}

    /* For the Function valTimeTxt -> the value assigned based on
    * the Average time between messages throughout the day,
    * between the messages received from a certain contact, time between messages
    * sent to the contact and the time between a message sent and received
    * from the contact all will be combined into a single value
    * */
    //Value for the time for both received and sent messages
    double valTimeTxt(double tS,double tR,double tSR){
        double ans;
        ans = (tS+2*tR); //Temp = Avg timeSent + 2*timeRec
        ans=ans-tSR;       //Temp = Temp - Avg TimeBetweenSentandRec
        return ans/24; //Temp/hours in a day
    }

    /* The function weighValues is used to combine the values of the previously
    * defined functions: valNumTxt, valTimeTxt, and valSizeTxt
    *  => into a single value score which will be stored into a variable valToday
    *  */
    //Combined value for the functions above
    double weighValues(double v1,double v2,double v3){


        return 0.0;
    }

    /* The Function printStatusRel is used as a comparison function of the
     * values from the weighValues of yesterday and today to an ouput string
      * where v1 - today and v2 - yesterday
      * for example (v1<v2)&&(v1<baselineVal)
      * => " Your relationship is trending down and is not in  a good place "
      * */
    //Comparison function of relationship values of today(v1) and yesterday(v2)
    String printStatusRel(double v1,double v2){
        String ans, ans1, ans2;
        double BaseScore = 2.0; //The Base Score to compare users Scores with will update
                                //score based on testing later in development
        if(v1 < BaseScore)
            ans1="In Bad Standing";
        else
            ans1="In Good Standing";

        if(v1==v2)
            ans2="Relationship Trending Neutral.";
        else if (v1 < v2)
            ans2="Relationship Trending Down.";
        else
            ans2="Relationship Trending Up";
         ans=ans1+" and "+ans2;
        return ans;
           }

//End of Class
}