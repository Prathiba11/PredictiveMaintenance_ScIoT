package com.example.smartled;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class SecondActivity extends AppCompatActivity {

    public static Button first_activity;
    public static TextView actualPower1, actualPower2, averageHourPower1, averageHourPower2,
                           averageDayPower1, averageDayPower2, averageWeekPower1, averageWeekPower2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        first_activity = (Button) findViewById(R.id.btn_activity);
        actualPower1 = (TextView) findViewById(R.id.tv_actpow1);
        actualPower2 = (TextView) findViewById(R.id.tv_actpow2);
        averageHourPower1 = (TextView) findViewById(R.id.tv_avgpowhr1);
        averageHourPower2 = (TextView) findViewById(R.id.tv_avgpowhr2);
        averageDayPower1 = (TextView) findViewById(R.id.tv_avgpowday1);
        averageDayPower2 = (TextView) findViewById(R.id.tv_avgpowday2);
        averageWeekPower1 = (TextView) findViewById(R.id.tv_avgpowwk1);
        averageWeekPower2 = (TextView) findViewById(R.id.tv_avgpowwk2);

        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        BackgroundWorker backgroundWorker2 = new BackgroundWorker(new AsyncResponse() {
                            @Override
                            public void displayLed(String ledColour, int ledNum) {
                            }
                        });
                        backgroundWorker2.execute("2");
                    }
                });
            }
        };
        timer.schedule(task, 0, 5000);

        first_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
