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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackgroundWorker extends AsyncTask <String, Void, String> {
    private final static Double WARNING_THRESHOLD = 11.0;
    private final static Double SWITCH_OFF_THRESHOLD = 20.0;
    String url, user, pass;

    ArrayList<Double> actual_power_1 = new ArrayList<Double>();
    ArrayList<String> actual_power_2 = new ArrayList<String>();

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

            Statement st = con.createStatement();
            ResultSet rs1 = st.executeQuery("select * from plugwise1");
            //ResultSet rs2 = st.executeQuery("select * from plugwise2");
            ResultSetMetaData rsmd1 = rs1.getMetaData();

            while(rs1.next()) {
                actual_power_1.add(rs1.getDouble(3));
            }

            /*while(rs2.next()) {
                actual_power_2.add(rs1.getString(3));
            }*/
        }
        catch (Exception e){
          return new String("Exeption:" + e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {

        String line = "Actual Power1: " + actual_power_1.get(actual_power_1.size() - 1) + "\n";
        MainActivity.plugwiseData.setText(line);
        Double last_val = actual_power_1.get(actual_power_1.size() - 1);

        if(last_val < WARNING_THRESHOLD) {
            //SocketAsyncTask led_off = new SocketAsyncTask();
            //led_off.execute("0");
            delegate.displayLed("green", 1);
        } else if((last_val >= WARNING_THRESHOLD) && (last_val < SWITCH_OFF_THRESHOLD)){
            delegate.displayLed("red", 1);
        } else {
            delegate.displayLed("white", 1);
        }

    }


}
