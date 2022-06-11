package com.cindea.pothub;

import static com.cindea.pothub.map.Constants.CLOSE_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.GET_POTHOLES_BY_RANGE;
import static com.cindea.pothub.map.Constants.GET_USER_POTHOLES_BY_DAYS;
import static com.cindea.pothub.map.Constants.OPEN_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.REPORT_POTHOLE;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.home.contracts.LeftHomeContract;
import com.cindea.pothub.home.contracts.RightHomeContract;
import com.cindea.pothub.home.models.LeftHomeModel;
import com.cindea.pothub.home.models.RightHomeModel;
import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class CustomHandler extends Handler {

    private Socket socket;
    private BufferedReader input_stream;
    private PrintWriter output_stream;
    private Boolean socket_ready = new Boolean(false);

    @Override
    public void handleMessage(Message msg) {

        switch(msg.what){
            case OPEN_CONNECTION_WITH_SERVER:
                Log.e("HANDLER", "Opening");
                openConnectionWithServer();
                break;
            case REPORT_POTHOLE: {
                Log.e("HANDLER", "Reporting");
                try {
                    reportPotHole((Pothole)msg.obj);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            }
            case GET_USER_POTHOLES_BY_DAYS: {
                Log.e("HANDLER", "Getting User Potholes By Days");
                getUserPotholesByDays(((LeftHomeModel.CustomMessage)msg.obj).username, ((LeftHomeModel.CustomMessage)msg.obj).date, ((LeftHomeModel.CustomMessage)msg.obj).listener);
                break;
            }
            case CLOSE_CONNECTION_WITH_SERVER:
                Log.e("HANDLER", "Closing");
                closeConnectionWithServer();
                break;
            case GET_POTHOLES_BY_RANGE: {
                Log.e("HANDLER", "Getting Potholes by range");
                getPotholesByRange(((RightHomeModel.CustomMessage)msg.obj).latitude, ((RightHomeModel.CustomMessage)msg.obj).longitude, ((RightHomeModel.CustomMessage)msg.obj).meters, ((RightHomeModel.CustomMessage)msg.obj).listener);
                break;
            }
            default:
                break;
        }
    }

    public boolean openConnectionWithServer(){

        try {
            InetAddress server_address = InetAddress.getByName("20.126.123.213");

            socket = new Socket(server_address, 12345);

            input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            output_stream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            socket_ready = true;
            synchronized (socket_ready) {
                socket_ready.notify();
            }

        } catch (UnknownHostException e) {
            return false;
        } catch (IOException e) {
            return false;
        }

        Log.e("HANDLER","Connection with server opened");
        return true;

    }

    public void reportPotHole(Pothole pothole) throws InterruptedException {
        String json;
        Gson gson = new Gson();
        pothole.setAction(REPORT_POTHOLE);
        json = gson.toJson(pothole, Pothole.class);
        synchronized (socket_ready) {
            while(!socket_ready)
                socket_ready.wait();
        }
        output_stream.write(json);
        output_stream.flush();
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
        Log.e("HANDLER","Connection with server closed");
    }

    public void getUserPotholesByDays(String username, String date, LeftHomeContract.Model.OnFinishListener listener){

        if(openConnectionWithServer()) {

            Gson gson;
            output_stream.write("{\"action\":2,\"user\":\""+username+"\",\"date\":"+"\""+date+"\"}");
            output_stream.flush();
            try {
                String json = input_stream.readLine();
                gson = new Gson();

                List<Pothole> potholes = new ArrayList<>();
                Pothole[] potholes_arr = gson.fromJson(json, Pothole[].class);
                for(int i = 0; i < potholes_arr.length; i++)
                    potholes.add(potholes_arr[i]);
                //p è null
                listener.onPotholesLoaded(potholes);
            } catch (IOException e) {
                listener.onError(e.getMessage());
            }
            closeConnectionWithServer();

            return;

        }


    }

    public void getPotholesByRange(double latitude, double longitude, double range, RightHomeContract.Model.OnFinishListener listener){

        if(openConnectionWithServer()) {

            Gson gson;
            output_stream.write("{\"action\":3,\"latitude\":"+latitude+",\"longitude\":"+longitude+ ",\"range\":" +range+"}");
            output_stream.flush();
            try {
                String json = input_stream.readLine();
                gson = new Gson();
                List<Pothole> potholes = new ArrayList<>();
                Pothole[] potholes_arr = gson.fromJson(json, Pothole[].class);
                if(potholes_arr != null) {

                    for(int i = 0; i < potholes_arr.length; i++)
                        potholes.add(potholes_arr[i]);
                    //p è null
                    listener.onPotholesLoaded(potholes);

                }else {

                    listener.onError(null);

                }

            } catch (IOException e) {
                listener.onError(null);
            }
            closeConnectionWithServer();

        }

    }


}

