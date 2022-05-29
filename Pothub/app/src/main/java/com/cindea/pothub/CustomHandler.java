package com.cindea.pothub;




import static com.cindea.pothub.map.Constants.CLOSE_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.OPEN_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.REPORT_POTHOLE;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cindea.pothub.entities.Pothole;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class CustomHandler extends Handler {

    private Socket socket;
    private BufferedReader input_stream;
    private PrintWriter output_stream;

    @Override
    public void handleMessage(Message msg) {

        switch(msg.what){
            case OPEN_CONNECTION_WITH_SERVER:
                Log.e("HANDLER", "Opening");
                openConnectionWithServer();
                break;
            case REPORT_POTHOLE:
                Log.e("HANDLER", "Reporting");
                reportPotHole();
                Log.e("POTHOLE REPORTED", String.valueOf(((Pothole)msg.obj)));
                break;
            case CLOSE_CONNECTION_WITH_SERVER:
                Log.e("HANDLER", "Closing");
                closeConnectionWithServer();
                break;
            default:
                break;
        }
    }

    public void openConnectionWithServer(){

        try {
            InetAddress server_address = InetAddress.getByName("20.126.123.213");

            socket = new Socket(server_address, 12345);

            input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            output_stream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

        } catch (UnknownHostException e) {
            Log.e("UNKNOWN HOST EXCEPTION", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void reportPotHole(){

    }

    private void closeConnectionWithServer() {
        if (socket != null) {
            if (socket.isConnected()) {
                try {
                    input_stream.close();
                    output_stream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
