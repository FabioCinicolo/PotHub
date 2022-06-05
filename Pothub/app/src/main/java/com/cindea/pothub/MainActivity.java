package com.cindea.pothub;

import static com.cindea.pothub.map.Constants.CLOSE_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.OPEN_CONNECTION_WITH_SERVER;
import static com.cindea.pothub.map.Constants.REPORT_POTHOLE;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import com.cindea.pothub.authentication.views.ResetCredentialsActivity;
import com.cindea.pothub.authentication.views.fragments.SigninFragment;
import com.cindea.pothub.authentication.views.fragments.SignupFragment;
import com.cindea.pothub.cognito.Cognito;
import com.cindea.pothub.entities.Pothole;
import com.cindea.pothub.map.LocationService;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;


public class MainActivity extends AppCompatActivity implements AuthSwitcher {

    Fragment signinFragment = new SigninFragment();
    Fragment signupFragment = new SignupFragment();
    private static Cognito cognito;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cognito = new Cognito(this);


        Thread t =new Thread(new Runnable() {

            private Socket socket;
            private BufferedReader input_stream;
            private PrintWriter output_stream;

            @Override
            public void run() {
                char[] messageByte = new char[100000];
                boolean end = false;
                StringBuilder dataString = new StringBuilder(100000);
                int totalBytesRead = 0;



                try {
                    InetAddress server_address = InetAddress.getByName("20.126.123.213");

                    socket = new Socket(server_address, 12345);

                    input_stream = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
                    output_stream = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);


                    output_stream.write("{\"action\":2,\"latitude\":40,\"longitude\":43,\"range\":1}");
                    output_stream.flush();
                    String x = input_stream.readLine();
                    Log.e("ciao", x);

                } catch (UnknownHostException e) {
                    Log.e("UNKNOWN HOST EXCEPTION", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();
                }


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

        }

        );

        t.start();
        replaceFragment(signinFragment);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void replaceFragment(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.activityMain_framelayout, fragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onSigninPress() {


        replaceFragment(signinFragment);

    }

    @Override
    public void onSignupPress() {

        replaceFragment(signupFragment);

    }

    @Override
    public void onResetPassword() {

        Intent intent = new Intent(MainActivity.this, ResetCredentialsActivity.class);
        startActivity(intent);

    }

    public static Cognito getCognito() {
        return cognito;
    }

}