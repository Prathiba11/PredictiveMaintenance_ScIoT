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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BackgroundWorker extends AsyncTask <String, Void, String> {
    private final static Double WARNING_THRESHOLD = 11.0;
    private final static Double SWITCH_OFF_THRESHOLD = 20.0;
    String json_url, url, user, pass;

    public AsyncResponse delegate = null;

    public BackgroundWorker (AsyncResponse asyncResponse) {
        delegate = asyncResponse;
    }

    @Override
    protected void onPreExecute()
    {
      //json_url = "http://192.168.0.111/plugwise.php";
        url = "jdbc:mysql//192.168.0.151:3306/power_consumption";
        user = "root";
        pass = "12345";
    }

    @Override
    protected String doInBackground(String... params) {
        String result = "";
        try {
            /*URL url = new URL(json_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                break;
            }
            bufferedReader.close();
            inputStream.close();;
            httpURLConnection.disconnect();
            result = stringBuilder.toString();*/
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(url, user, pass);

            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("select * from plugwise1");
            ResultSetMetaData rsnd = rs.getMetaData();

            while(rs.next()) {
                ;
            }
        }
        catch (Exception e){
          return new String("Exeption:" + e.getMessage());
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
        Handler handler1 = new Handler();
        String line = null;
        String json1 = null;
        String json2 = null;
        Pattern pattern = Pattern.compile(";");
        Matcher matcher = pattern.matcher(result);
        if (matcher.find()) {
            json1 = result.substring(0, matcher.start());
            json2 = result.substring(matcher.end());
        }
        //Toast.makeText(context.getApplicationContext(), result,Toast.LENGTH_SHORT).show();
        try{
            //JSONArray jsonArray = new JSONArray(result);
            JSONObject jsonResult1 = new JSONObject(json1);
            JSONObject jsonResult2 = new JSONObject(json2);
            int success1 = jsonResult1.getInt("success1");
            int success2 = jsonResult2.getInt("success2");
            if(success1 == 1) {
                //Toast.makeText(context.getApplicationContext(), "LED data present",Toast.LENGTH_SHORT).show();
                JSONArray ledData = jsonResult1.getJSONArray("plugwise1");
                //for(int i=0; i < ledData.length(); i++) {
                JSONObject led = ledData.getJSONObject(ledData.length()- 1);
                Double act_pow = (Double) led.getDouble("power");
                //String date = ledData.("date_time");

                line = "Actual Power1: " + act_pow + "\n";
                //MainActivity.plugwiseData.setText(line);


                if(act_pow < WARNING_THRESHOLD) {
                    //SocketAsyncTask led_off = new SocketAsyncTask();
                    //led_off.execute("0");
                    delegate.displayLed("green", 1);
                } else if((act_pow >= WARNING_THRESHOLD) && (act_pow < SWITCH_OFF_THRESHOLD)){
                    delegate.displayLed("red", 1);
                } else {
                    delegate.displayLed("white", 1);
                }
                //}
            }
            else {
                //Toast.makeText(context.getApplicationContext(), "No LED data present",Toast.LENGTH_SHORT).show();
            }
            if(success2 == 1) {
                //Toast.makeText(context.getApplicationContext(), "LED data present",Toast.LENGTH_SHORT).show();
                JSONArray ledData = jsonResult2.getJSONArray("plugwise2");
                //for(int i=0; i < ledData.length(); i++) {
                JSONObject led = ledData.getJSONObject(ledData.length()- 1);
                Double act_pow = (Double) led.getDouble("power");
                //String date = ledData.("date_time");
                line += "Actual Power2: " + act_pow;
                MainActivity.plugwiseData.setText(line);
                if(act_pow < WARNING_THRESHOLD) {
                    /*SocketAsyncTask led_off = new SocketAsyncTask();
                    led_off.execute("0");*/
                    delegate.displayLed("green", 2);
                } else if((act_pow >= WARNING_THRESHOLD) && (act_pow < SWITCH_OFF_THRESHOLD)){
                    delegate.displayLed("red", 2);
                } else {
                    delegate.displayLed("white", 2);
                }
                //}
            }
            else {
                //Toast.makeText(context.getApplicationContext(), "No LED data present",Toast.LENGTH_SHORT).show();
            }
        }
        catch (JSONException e){

            //Toast.makeText(context.getApplicationContext(), e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


}
