package com.example.smartled;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    public static TextView plugwiseData;
    public static Button led_on1, led_on2, led_off1, led_off2, fetch_plugwise;
    public static ImageView led_state1, led_state2;
    Socket myAppSocket = null;
    public static String wifiModuleIp = "";
    public static int wifiModulePort = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        plugwiseData = (TextView) findViewById(R.id.tv_data);
        led_on1 = (Button) findViewById(R.id.btn_on1);
        led_off1 = (Button) findViewById(R.id.btn_off1);
        led_on2 = (Button) findViewById(R.id.btn_on2);
        led_off2 = (Button) findViewById(R.id.btn_off2);
        fetch_plugwise = (Button) findViewById(R.id.btn_fetch);
        led_state1 = (ImageView) findViewById(R.id.img_led1);
        led_state2 = (ImageView) findViewById(R.id.img_led2);

        int imageResource1 = getResources().getIdentifier("@drawable/led_white", null, getApplicationContext().getPackageName());
        led_state1.setImageResource(imageResource1);
        int imageResource2 = getResources().getIdentifier("@drawable/led_white", null, getApplicationContext().getPackageName());
        led_state2.setImageResource(imageResource2);

        fetch_plugwise.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
            final Handler handler = new Handler();
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        public void run() {
                            //new BackgroundWorker().execute();
                            BackgroundWorker backgroundWorker = new BackgroundWorker(new AsyncResponse() {
                                @Override
                                public void displayLed(String ledColour, int ledNum) {
                                    int imageResource = 0;
                                    if(ledColour == "white"){
                                        imageResource = getResources().getIdentifier("@drawable/led_white", null, getApplicationContext().getPackageName());
                                    } else if(ledColour == "green"){
                                        imageResource = getResources().getIdentifier("@drawable/led_green", null, getApplicationContext().getPackageName());
                                    } else {
                                        imageResource = getResources().getIdentifier("@drawable/led_red", null, getApplicationContext().getPackageName());
                                    }
                                    if(ledNum == 1) {
                                        led_state1.setImageResource(imageResource);
                                    } else {
                                        led_state2.setImageResource(imageResource);
                                    }
                                }
                            });
                            backgroundWorker.execute();
                        }
                    });
                }
            };
            timer.schedule(task, 0, 5000);
            }
        });

        led_on1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
              SocketAsyncTask led_on = new SocketAsyncTask();
              led_on.execute("1");
              int imageResource = getResources().getIdentifier("@drawable/led_green", null, getApplicationContext().getPackageName());
              led_state1.setImageResource(imageResource);
            }
        });

        led_off1.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
            SocketAsyncTask led_off = new SocketAsyncTask();
            led_off.execute("0");
            int imageResource = getResources().getIdentifier("@drawable/led_white", null, getApplicationContext().getPackageName());
            led_state1.setImageResource(imageResource);
            }
        });

        led_on2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                SocketAsyncTask led_on = new SocketAsyncTask();
                led_on.execute("1");
                int imageResource = getResources().getIdentifier("@drawable/led_green", null, getApplicationContext().getPackageName());
                led_state2.setImageResource(imageResource);
            }
        });

        led_off2.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SocketAsyncTask led_off = new SocketAsyncTask();
                led_off.execute("0");
                int imageResource = getResources().getIdentifier("@drawable/led_white", null, getApplicationContext().getPackageName());
                led_state2.setImageResource(imageResource);
            }
        });
    }
}
