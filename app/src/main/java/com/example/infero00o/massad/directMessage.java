package com.example.infero00o.massad;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class directMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_direct_message);
        //Creates bundle to retrieve data from Admin class
        Bundle extras = getIntent().getExtras();
        String title = extras.getString("TITLE");
        String text = extras.getString("MESSAGE");
        String uuid = extras.getString("senderID");

        //Creates textview to handle title
        TextView t = findViewById(R.id.alert);
        t.setText(title);
        //Creates textview to handle message
        TextView m = findViewById(R.id.customMessage);
        m.setText(text);
        //Creates button to handle reply button
        Button reply = findViewById(R.id.buttonReply);
        //Creates onClick listener for reply button
        reply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked create new intent to start sendDirectMessage class activity
                Intent intent = new Intent(getApplicationContext(), sendDirectMessage.class);
                //Send the UUID of the last message recieved to the activity
                intent.putExtra("UUID", uuid);
                //start the acitvity
                startActivity(intent);
                //finish this activity to prevent overlapping
                finish();
            }
        });
        //Notify the user of new message
        notificationMessage();
    }
    //Creates the notification
    public void notificationMessage(){
        //Gets the message title
        TextView t = findViewById(R.id.alert);
        String textTitle = t.getText().toString();
        //Creates new intent to display notification on directMessage class
        Intent intent = new Intent(getApplicationContext(), directMessage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        //Displays the notification popup and on the home screen
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK|PowerManager.ACQUIRE_CAUSES_WAKEUP |PowerManager.ON_AFTER_RELEASE, "Alert");
        wl.acquire(3000);
        wl.release();
        //Plays the default ringtone when a message is recieved
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        //Checks if the API of the device is greater or equal than API 26
        //Else run older version of notification
        //Displays the notification with icons
        if (Build.VERSION.SDK_INT >= 26){
            NotificationChannel mChannel = new NotificationChannel("alert", "alert", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("Alert notification");
            mNotificationManager.createNotificationChannel(mChannel);

            Notification n = new Notification.Builder(this, "alert")
                    .setContentTitle(textTitle)
                    .setContentText("New Message!")
                    .setBadgeIconType(R.mipmap.ic_launcher)
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setSound(alarmSound)
                    .build();


            mNotificationManager.notify(001, n);


        }else {
            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentTitle(textTitle)
                    .setContentText("New Message!")
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(alarmSound)
                    .setContentIntent(pendingIntent);



            mNotificationManager.notify(001, mBuilder.build());

        }

    }
    //Check permission to use phone if device API is greater than 23
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
    //If permission is granted or denied display message accordingly
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


        }
    }

}
