package com.example.smartled;

import android.os.AsyncTask;
import android.util.Log;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.util.Properties;

public class PowerRead extends AsyncTask<String, Void, Void> {
    Socket socket;

    @Override
    protected Void doInBackground(String... strings) {
        int option = Integer.valueOf(strings[0]);
        /*try {
            InetAddress inetAddress = InetAddress.getByName(MainActivity.wifiModuleIp);
            socket = new java.net.Socket(inetAddress, MainActivity.wifiModulePort);
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeBytes(led_switch);
            dataOutputStream.close();
            socket.close();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try {
            String g=executeRemoteCommand("pi", "smart@team5", "192.168.0.114", 22, option);
            //Log.d("DDD",g);
            MainActivity.plugwiseData1.setText(option+g);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String executeRemoteCommand(
            String username,
            String password,
            String hostname,
            int port,
            int option) throws Exception {
        String command=null;
        switch (option)
        {
            case 1:command="mosquitto_sub -t plugwise2py/state/power/000D6F0005690BAB -C 1";
                break;
            case 2:command="mosquitto_sub -t plugwise2py/state/power/000D6F0003562BE1 -C 1";
                break;
        }
        String g = null;
        try {
            JSch jsch = new JSch();
            Session session = jsch.getSession(username, hostname, 22);
            session.setPassword(password);
            Log.d("SSH", "session done");

            // Avoid asking for key confirmation
            Properties prop = new Properties();
            prop.put("StrictHostKeyChecking", "no");
            session.setConfig(prop);
            session.connect();
            Log.d("SSH", "session conn");

            // SSH Channel
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Log.d("SSH", "BAOS done");
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            Log.d("SSH", "chsnnel done");
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();

            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) break;
                    g = new String(tmp, 0, i);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) continue;
                    System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                } catch (Exception ee) {
                }
            }

            channel.disconnect();
            session.disconnect();
        }
        catch(Exception e){
            System.out.println(e);
        }
        return g;

    }



}
