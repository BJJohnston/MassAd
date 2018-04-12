package com.example.infero00o.massad;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.example.infero00o.massad.MainActivity.*;
import android.R.*;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
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
        }else if(alertType == 4){
            TextView t = findViewById(R.id.alert);
            t.setText("Active Shooter");
        }else if (alertType == 3) {
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
        notification();

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

    public void notification(){
        TextView t = findViewById(R.id.alert);
        String textTitle = t.getText().toString();

        Intent intent = new Intent(getApplicationContext(), Alert.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "Alert");
        wl.acquire(3000);
        wl.release();

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26){
            NotificationChannel mChannel = new NotificationChannel("alert", "alert", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("Alert notification");
            mNotificationManager.createNotificationChannel(mChannel);

            Notification n = new Notification.Builder(this, "alert")
                    .setContentTitle(textTitle)
                    .setContentText("Emergency in progess!")
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .build();


                mNotificationManager.notify(001, n);


        }else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText("Emergency in progess!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentIntent(pendingIntent);


            mNotificationManager.notify(001, mBuilder.build());

        }

    }







}
