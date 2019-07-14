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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackgroundWorker extends AsyncTask <String, Void, String> {
    private final static Double WARNING_THRESHOLD = 11.0;
    private final static Double SWITCH_OFF_THRESHOLD = 20.0;
    String url, user, pass;

    ArrayList<Double> actual_power_1 = new ArrayList<Double>();
    ArrayList<Double> actual_power_2 = new ArrayList<Double>();

    ArrayList<Long> epoch_time_1 = new ArrayList<Long>();
    ArrayList<Long> epoch_time_2 = new ArrayList<Long>();

    public AsyncResponse delegate = null;

    public BackgroundWorker (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected void onPreExecute()
    {
      //url = "jdbc:mysql://192.168.0.151:3306/power_consumption";
        url = "jdbc:mysql://192.168.0.151:3306/power_consumption";
        user = "Team5";
        pass = "Team5";
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            Statement st1 = con.createStatement();
            Statement st2 = con.createStatement();
            ResultSet rs1 = st1.executeQuery("select * from plugwise1");
            ResultSet rs2 = st2.executeQuery("select * from plugwise2");
            ResultSetMetaData rsmd11 = rs1.getMetaData();
            ResultSetMetaData rsmd12 = rs2.getMetaData();

            while(rs1.next()) {
                actual_power_1.add(rs1.getDouble(3));
                epoch_time_1.add(rs1.getLong(2));
            }

            while(rs2.next()) {
                actual_power_2.add(rs2.getDouble(3));
                epoch_time_2.add(rs2.getLong(2));
            }
        }
        catch (Exception e){
          return new String("Exeption:" + e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        Double last_pow1 = actual_power_1.get(actual_power_1.size() - 1);
        Double last_pow2 = actual_power_2.get(actual_power_2.size() - 1);

        Long last_epoch_1 = epoch_time_1.get(epoch_time_1.size() - 1);
        Long last_epoch_2 = epoch_time_2.get(epoch_time_2.size() - 1);

        String format = "dd-MM-yyyy HH:mm:ss";

        String date1 = MainActivity.Epoch2DateString(last_epoch_1, format);
        String date2 = MainActivity.Epoch2DateString(last_epoch_2, format);

        String power1 = "Actual Power1: " + actual_power_1.get(actual_power_1.size() - 1);
        String power2 = "Actual Power2: " + actual_power_2.get(actual_power_2.size() - 1);

        MainActivity.plugwiseData1.setText(power1);
        MainActivity.plugwiseData2.setText(power2);
        MainActivity.dateTime1.setText(date1);
        MainActivity.dateTime2.setText(date2);

        if(last_pow1 < WARNING_THRESHOLD) {
            delegate.displayLed("green", 1);
        } else if((last_pow1 >= WARNING_THRESHOLD) && (last_pow1 < SWITCH_OFF_THRESHOLD)){
            delegate.displayLed("red", 1);
        } else {
            delegate.displayLed("white", 1);
        }

        if(last_pow2 < WARNING_THRESHOLD) {
            delegate.displayLed("green", 2);
        } else if((last_pow2 >= WARNING_THRESHOLD) && (last_pow2 < SWITCH_OFF_THRESHOLD)){
            delegate.displayLed("red", 2);
        } else {
            delegate.displayLed("white", 2);
        }

    }


}
