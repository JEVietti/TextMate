package com.example.textmate;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;


public class contactInfo extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);

        TextView textView = (TextView) findViewById(R.id.contactName);
        TextView scoreTextView = (TextView) findViewById(R.id.myImageViewText);
        TextView timeView = (TextView) findViewById(R.id.lastTextTime);
        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String avgscore = intent.getStringExtra("avgscore");
        String time = intent.getStringExtra("time");
        ArrayList<Integer> myTimeList = intent.getIntegerArrayListExtra("timeList2");
        double avgScore = Double.parseDouble(intent.getStringExtra("avgscore"));
        double todayScore = Double.parseDouble(intent.getStringExtra("todayScore")) ;
        double yesScore = Double.parseDouble(intent.getStringExtra("yesScore"));

        textView.setText(name);
        scoreTextView.setText(avgscore);
        timeView.setText(time);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        /*LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, .5),
                new DataPoint(2, 2),
                new DataPoint(3, .5),
                new DataPoint(4, 1.5),
                new DataPoint(5, 2),
                new DataPoint(6, 5),
                new DataPoint(7, 3.8),
                new DataPoint(8, 4),
                new DataPoint(9, 2.2),
                new DataPoint(10, 3),
                new DataPoint(11, .5),
                new DataPoint(12, 5.2),
                new DataPoint(13, .5),
                new DataPoint(14, 1.5),
                new DataPoint(15, 2),
                new DataPoint(16, 5),
                new DataPoint(17, 3.8),
                new DataPoint(18, 5.4),
                new DataPoint(19, 2)
        });*/
        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, avgScore),
                new DataPoint(1, yesScore),
                new DataPoint(2, todayScore)
        });
       /* LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, myTimeList.get(0))
                //new DataPoint(1, myTimeList.get(1)),
                //new DataPoint(2, myTimeList.get(2)),
                //new DataPoint(3, myTimeList.get(3)),
                //new DataPoint(4, myTimeList.get(4))
        });*/
        graph.addSeries(series);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
    //    getMenuInflater().inflate(R.menu.menu_contact_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
     //   if (id == R.id.action_settings) {
            //return true;
     //   }

       return super.onOptionsItemSelected(item);
    }
}
