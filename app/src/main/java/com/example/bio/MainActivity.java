package com.example.bio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.material.button.MaterialButton;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private TextView connectionMsgTextView;
    private MaterialButton continueButton;
    private Handler handler = new Handler();
    private Runnable runnable;
    private ProgressBar connectionStatusProgBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectionMsgTextView = findViewById(R.id.connectionMsgTextView);
        continueButton = findViewById(R.id.continueButton);
        connectionStatusProgBar = findViewById(R.id.connectionStatusProgBar);

        try {
            DataReceiver.startUDPServer();
        }
        catch (Exception e){
            e.printStackTrace();
        }

        runnable = new Runnable() {
            @Override
            public void run() {
                /* do what you need to do */
                if(DataBuffer.MSG_QUEUE.isEmpty()){
                    //do nothing
                }
                else{
                    String str = DataBuffer.MSG_QUEUE.remove();
                    if(str.equals(Constants.CONNECTION)){

                        try {
                            DataReceiver.sendPacket(Constants.CONN_OK);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }
                        continueButton.setVisibility(View.VISIBLE);
                        connectionMsgTextView.setTextColor(Color.GREEN);
                        connectionMsgTextView.setText("Device Connected!");
                        connectionStatusProgBar.setVisibility(View.INVISIBLE);

                        handler.removeCallbacks(runnable);
                    }
                }

                /* and here comes the "trick" */
                handler.postDelayed(this, 1000);
            }
        };

        handler.postDelayed(runnable, 100);



    }

    public void continueSelected(View view){
        startActivity(new Intent(this, HomeActivity.class));
    }
}
