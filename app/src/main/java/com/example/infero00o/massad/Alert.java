package com.example.infero00o.massad;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.example.infero00o.massad.MainActivity.*;
import android.R.*;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.util.HashMap;

public class Alert extends AppCompatActivity {
String senderId;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Bundle extras = getIntent().getExtras();
        int alertType = extras.getInt("ALERT_TYPE");
        if (alertType == 1){
            TextView t = findViewById(R.id.alert);
            t.setText("Fire");
        } else if (alertType == 2){
            TextView t = findViewById(R.id.alert);
            t.setText("Flood");
        } else if (alertType == 3) {
            String title = extras.getString("TITLE");
            String text = extras.getString("MESSAGE");

            TextView t = findViewById(R.id.alert);
            t.setText(title);

            TextView m = findViewById(R.id.customMessage);
            m.setVisibility(View.VISIBLE);
            m.setText(text);
        }

        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        v.vibrate(2500);

        MediaPlayer mp = MediaPlayer.create(this, R.raw.alert);
        mp.start();

        final Button exitBuilding = findViewById(R.id.exitBuilding);
        exitBuilding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mp.stop();
                mp.release();
                exitBuilding();
                finish();

            }
        });


    }

    public void exitBuilding(){

        MainActivity main = new MainActivity();
        String exit = "true";
        HashMap<String, Object> content = new HashMap<>();
        content.put("exit", exit);


        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content).setReceiverId(senderId);
        Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }







}
