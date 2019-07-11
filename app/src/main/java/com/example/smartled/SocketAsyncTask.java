package com.example.smartled;

import android.os.AsyncTask;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketAsyncTask extends AsyncTask <String, Void, Void> {
    Socket socket;

    @Override
    protected Void doInBackground(String... strings) {
        String led_switch = strings[0];
        try {
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
        }
        return null;
    }
}
