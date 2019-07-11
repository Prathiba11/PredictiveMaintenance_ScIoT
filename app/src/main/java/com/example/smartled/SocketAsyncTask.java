package com.example.smartled;

import android.os.AsyncTask;

import java.io.ByteArrayOutputStream;
import java.net.Socket;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class SocketAsyncTask extends AsyncTask <String, Void, Void> {
    Socket socket;

    @Override
    protected Void doInBackground(String... strings) {
        String led_switch = strings[0];
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
            executeRemoteCommand("root", "smart@team5", "192.", 22);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String executeRemoteCommand(String username,String password,String hostname,int port) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(username, hostname, port);
        session.setPassword(password);

        // Avoid asking for key confirmation
        Properties prop = new Properties();
        prop.put("StrictHostKeyChecking", "no");
        session.setConfig(prop);

        session.connect();

        // SSH Channel
        ChannelExec channelssh = (ChannelExec)
                session.openChannel("exec");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        channelssh.setOutputStream(baos);

        // Execute command
        channelssh.setCommand("lsusb > /home/pi/test.txt");
        channelssh.connect();
        channelssh.disconnect();

        return baos.toString();
    }
}
