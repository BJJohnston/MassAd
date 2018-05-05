package com.example.infero00o.massad;

import com.bridgefy.sdk.client.BFEngineProfile;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.bridgefy.sdk.client.Bridgefy;

import java.util.ArrayList;
import java.util.HashMap;

public class Alert extends AppCompatActivity {

String tel;
int alertType;
    public ArrayList admin_ids = new ArrayList();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert);
        Bundle extras = getIntent().getExtras();
         alertType = extras.getInt("ALERT_TYPE");
        tel = extras.getString("TEL");
        admin_ids = extras.getStringArrayList("admin_ids");
        if (alertType == 1){
            TextView t = findViewById(R.id.alert);
            t.setText("Fire");
            TextView m = findViewById(R.id.customMessage);
            m.setText("There is a fire near your location! Please stay calm and head to the nearest exit");
        } else if (alertType == 2){
            TextView t = findViewById(R.id.alert);
            t.setText("Flood");
            TextView m = findViewById(R.id.customMessage);
            m.setText("There is a flood near your location! Please stay calm and head to the nearest exit");
        }else if(alertType == 4){
            TextView t = findViewById(R.id.alert);
            t.setText("Active Shooter");
            TextView m = findViewById(R.id.customMessage);
            m.setText("There is a active shooting near your location! Please stay calm and head to the nearest exit");
        }else if (alertType == 3) {
            String title = extras.getString("TITLE");
            String text = extras.getString("MESSAGE");


            TextView t = findViewById(R.id.alert);
            t.setText(title);

            TextView m = findViewById(R.id.customMessage);
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
                String uuid;
                int alertType = 9;
                for (int i = 0; i < admin_ids.size(); i++) {
                    uuid = admin_ids.get(i).toString();

                    HashMap<String, Object> content = new HashMap<>();
                    content.put("alertType", alertType);



                    com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
                    builder.setContent(content);
                    builder.setReceiverId(uuid);
                    Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);}
                mp.stop();
                mp.release();
                finish();

            }
        });

        final Button alarmONOff = findViewById(R.id.alarmOnOff);
        alarmONOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp.isPlaying()) {
                    alarmONOff.setText("Turn on alarm");
                    mp.pause();
                } else {
                    alarmONOff.setText("Turn off alarm");
                    mp.start();
                }
            }
        });

        final Button callEmergencyServices = findViewById(R.id.emergencyServices);
        callEmergencyServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + tel));
                    mp.pause();
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                                == PackageManager.PERMISSION_GRANTED) {
                            Log.v("TAG", "Permission is granted");
                            startActivity(intent);
                        } else {

                            Log.v("TAG", "Permission is revoked");
                            ActivityCompat.requestPermissions(Alert.this, new String[]{Manifest.permission.CALL_PHONE}, 1);

                        }
                    } else { //permission is automatically granted on sdk<23 upon installation
                        Log.v("TAG", "Permission is granted");

                    }


            }
        });


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


    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.CALL_PHONE)
                    == PackageManager.PERMISSION_GRANTED) {
                Log.v("TAG","Permission is granted");
                return true;
            } else {

                Log.v("TAG","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("TAG","Permission is granted");
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {

            case 1: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }








}
