package com.example.smartled;

import android.os.AsyncTask;
import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.smartled.MainActivity.*;
import static com.example.smartled.SecondActivity.*;

public class BackgroundWorker extends AsyncTask <String, Void, String> {
    private final static Double WARNING_THRESHOLD = 15.0;
    private final static Double SWITCH_OFF_THRESHOLD = 22.0;
    private final static int HOURS_IN_A_DAY = 24;
    private final static int HOURS_IN_A_WEEK= 168;
    private final static int ONE_HOUR = 1;

    NumberFormat formatter = new DecimalFormat("##.###");

    String url, user, pass;
    String activity_num = "";

    ArrayList<Double> actual_power_1 = new ArrayList<Double>();
    ArrayList<Double> actual_power_2 = new ArrayList<Double>();

    ArrayList<Long> epoch_time_1 = new ArrayList<Long>();
    ArrayList<Long> epoch_time_2 = new ArrayList<Long>();

    Double AveragePower = Double.valueOf(0);
    Double PowerSum = Double.valueOf(0);

    public AsyncResponse delegate = null;

    public BackgroundWorker (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected void onPreExecute()
    {
      //url = "jdbc:mysql://192.168.0.151:3306/power_consumption";
        url = "jdbc:mysql://192.168.0.108:3306/test";
        user = "Team5";
        pass = "Team5";
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        activity_num = params[0];

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            Statement st1 = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("select * from test_plugwise1");
            ResultSet rs2 = st2.executeQuery("select * from test_plugwise2");
            ResultSetMetaData rsmd11 = rs1.getMetaData();
            ResultSetMetaData rsmd12 = rs2.getMetaData();

            while(rs1.next()) {
                actual_power_1.add(rs1.getDouble(2));
                epoch_time_1.add(rs1.getLong(1));
            }

            while(rs2.next()) {
                actual_power_2.add(rs2.getDouble(2));
                epoch_time_2.add(rs2.getLong(1));
            }
        }
        catch (Exception e){
          return new String("Exeption:" + e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        Double last_pow_1 = actual_power_1.get(actual_power_1.size() - 1);
        Double last_pow_2 = actual_power_2.get(actual_power_2.size() - 1);
        Long last_epoch_1 = epoch_time_1.get(epoch_time_1.size() - 1);
        Long last_epoch_2 = epoch_time_2.get(epoch_time_2.size() - 1);

        calculateAveragePower(ONE_HOUR, last_epoch_1, actual_power_1, epoch_time_1);
        Double last_hour_average_power_1 = AveragePower;
        Double last_hour_power_sum_1 = PowerSum;

        calculateAveragePower(HOURS_IN_A_DAY, last_epoch_1, actual_power_1, epoch_time_1);
        Double last_day_average_power_1 = AveragePower;
        Double last_day_power_sum_1 = PowerSum;

        calculateAveragePower(HOURS_IN_A_WEEK, last_epoch_1, actual_power_1, epoch_time_1);
        Double last_week_average_power_1 = AveragePower;
        Double last_week_power_sum_1 = PowerSum;

        calculateAveragePower(ONE_HOUR, last_epoch_2, actual_power_2, epoch_time_2);
        Double last_hour_average_power_2 = AveragePower;
        Double last_hour_power_sum_2 = PowerSum;

        calculateAveragePower(HOURS_IN_A_DAY, last_epoch_2, actual_power_2, epoch_time_2);
        Double last_day_average_power_2 = AveragePower;
        Double last_day_power_sum_2 = PowerSum;

        calculateAveragePower(HOURS_IN_A_WEEK, last_epoch_2, actual_power_2, epoch_time_2);
        Double last_week_average_power_2 = AveragePower;
        Double last_week_power_sum_2 = PowerSum;

        setUIData(last_pow_1, last_pow_2, last_epoch_1, last_epoch_2, last_hour_average_power_1,
                  last_hour_average_power_2, last_day_average_power_1, last_day_average_power_2,
                  last_week_average_power_1, last_week_average_power_2);

        controlLed(last_pow_1, last_pow_2);
    }

    private void calculateAveragePower(int numOfHours, Long last_epoch,
                                       ArrayList<Double> actual_power, ArrayList<Long> epoch_time) {
        Long epoch_diff = Long.valueOf(0);
        Long prev_time_epoch = Long.valueOf(0);
        int db_index_count = 0;
        int totalSeconds = numOfHours * 3600;
        PowerSum = actual_power.get(actual_power.size() - 1);

        for(int i = 2; i < epoch_time.size(); i++) {
            prev_time_epoch = epoch_time.get(epoch_time.size() - i);
            PowerSum += actual_power.get(actual_power.size() - i);
            epoch_diff = last_epoch - prev_time_epoch;
            db_index_count++;
            if(epoch_diff >= totalSeconds) {
                break;
            }
        }
        AveragePower = PowerSum/db_index_count;
    }

    private void setUIData(Double power_1, Double power_2, Long epoch_1, Long epoch_2,
                           Double hr_avg_1, Double hr_avg_2, Double day_avg_1,
                           Double day_avg_2, Double week_avg_1, Double week_avg_2) {
        String format = "dd-MM-yyyy HH:mm:ss";

        String date1 = Epoch2DateString(epoch_1, format);
        String date2 = Epoch2DateString(epoch_2, format);

        String power1 = "Actual Power1: " + power_1 + "W";
        String power2 = "Actual Power2: " + power_2 + "W";
        String act_power1 = "Actual Power: " + power_1 + "W";
        String act_power2 = "Actual Power: " + power_2 + "W";
        String avg_hr_power1 = "Last hour: " + formatter.format(hr_avg_1);
        String avg_hr_power2 = "Last hour: " + formatter.format(hr_avg_2);
        String avg_day_power1 = "Last 24hrs: " + formatter.format(day_avg_1);
        String avg_day_power2 = "Last 24hrs: " + formatter.format(day_avg_2);
        String avg_week_power1 = "Last week: " + formatter.format(week_avg_1);
        String avg_week_power2 = "Last week: " + formatter.format(week_avg_2);

        if(activity_num == "1") {
            plugwiseData1.setText(power1);
            plugwiseData2.setText(power2);
            dateTime1.setText(date1);
            dateTime2.setText(date2);
        } else {
            if(activity_num == "2") {
                actualPower1.setText(act_power1);
                actualPower2.setText(act_power2);
                averageHourPower1.setText(avg_hr_power1);
                averageHourPower2.setText(avg_hr_power2);
                averageDayPower1.setText(avg_day_power1);
                averageDayPower2.setText(avg_day_power2);
                averageWeekPower1.setText(avg_week_power1);
                averageWeekPower2.setText(avg_week_power2);
            }
        }
    }

    private void controlLed(Double power_1, Double power_2) {
        SocketAsyncTask led_control = new SocketAsyncTask();
        if((power_1 > 0) && (power_1 < WARNING_THRESHOLD)) {
            delegate.displayLed("green", 1);
        } else if((power_1 >= WARNING_THRESHOLD) && (power_1 < SWITCH_OFF_THRESHOLD)){
            delegate.displayLed("red", 1);
        } else {
            delegate.displayLed("white", 1);
            if(power_1 > SWITCH_OFF_THRESHOLD) {
                led_control.execute("10");
            }
        }

        if((power_2 > 0) && (power_2 < WARNING_THRESHOLD)) {
            delegate.displayLed("green", 2);
        } else if((power_2 >= WARNING_THRESHOLD) && (power_2 < SWITCH_OFF_THRESHOLD)){
            delegate.displayLed("red", 2);
        } else {
            delegate.displayLed("white", 2);
            if(power_2 > SWITCH_OFF_THRESHOLD) {
                led_control.execute("20");
            }
        }
    }

}
