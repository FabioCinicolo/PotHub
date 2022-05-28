package com.cindea.pothub;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class AsyncTaskLoaderProva extends AsyncTaskLoader<String> {

    private Socket socket;
    private BufferedReader input_stream;
    private PrintWriter output_stream;

    private String ret;

    public AsyncTaskLoaderProva(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public String loadInBackground() {

        try {
            InetAddress server_address = InetAddress.getByName("20.126.123.213");

            socket = new Socket(server_address, 12345);

            input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            output_stream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

            disConnectWithServer(); // disconnect server

        } catch (UnknownHostException e) {
            Log.e("UNKNOWN HOST EXCEPTION", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
        ret = "ciao";

        return ret;
    }

    @Override
    protected void onStartLoading() {

        super.onStartLoading();
        if(ret!=null)
            deliverResult(ret); //Se il risultato è gia presente allora non faccio di nuovo il caricamento, uso il valore cachato. Utile quando ad esempio si ruota il cellulare, si evita di far ripartire sempre loadInBackground()
        else
            forceLoad(); //Fa partire loadInBackground
    }


    private void disConnectWithServer() {
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

    public String receiveDataFromServer() {
        try {
            String message = "";
            int charsRead = 0;
            char[] buffer = new char[10];

            while ((charsRead = input_stream.read(buffer)) != -1) {
                message += new String(buffer).substring(0, charsRead);
                Log.e("ciao",message);
            }
            return message;
        } catch (IOException e) {
            return "Error receiving response:  " + e.getMessage();
        }
    }


}
