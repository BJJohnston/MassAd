package com.example.infero00o.massad;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEnergyProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.Config;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class User extends AppCompatActivity {
    public ArrayList admin_ids = new ArrayList();
    public String tel;
    public String location;
    public String uuid;
    public String senderID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");
        admin_ids = extras.getStringArrayList("admin_ids");

        startBridgefy();

        Button messageAdmin = findViewById(R.id.messageAdmin);
        messageAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), sendAdminMessage.class);

                intent.putStringArrayListExtra("admin_ids", admin_ids);
                startActivity(intent);
            }
        });

        TextView t = findViewById(R.id.userText);
        t.setText("Thank you for logging in!\n" + "Your user ID is: " + uuid + "\n" + "Your located at: " + location + "\n" + "Please keep this application open and wait for any alerts!");
    }

    public void startBridgefy() {
        Config.Builder builder = new Config.Builder();
        builder.setEnergyProfile(BFEnergyProfile.HIGH_PERFORMANCE);
        builder.setEncryption(true);
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();
    }

    private MessageListener messageListener = new MessageListener() {
        @Override

        public void onMessageReceived(Message message) {
            if (message.getContent().containsKey("alertType")){
                double alertType = (double) message.getContent().get("alertType");

                if (alertType == 5){


                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    String uuid = message.getSenderId();
                    Intent intent = new Intent(getApplicationContext(), directMessage.class);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("senderID", uuid);
                    startActivity(intent);


                } else  if (alertType == 6){
                    Bridgefy.stop();
                    finish();
                    moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);


                }}
        }




        public void onBroadcastMessageReceived(Message message) {
            int alertType = (int) message.getContent().get("alertType");
            senderID = message.getSenderId();
            //{"id":"0d5f5dd6-7ef8-4596-b5c5-cee8550d741b"}


            if (admin_ids.contains(senderID)) {
                if (alertType != 3) {
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TEL", tel);
                    intent.putStringArrayListExtra("admin_ids", admin_ids);
                    startActivity(intent);
                }
                else{
                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");

                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("TEL", tel);
                    intent.putStringArrayListExtra("admin_ids", admin_ids);
                    startActivity(intent);
                }

            }
        }

    };
    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {


            HashMap<String, Object> map = new HashMap<>();
            map.put("ID", uuid);
            map.put("Make", Build.MANUFACTURER);
            map.put("Model", Build.MODEL);
            if (admin_ids.contains(uuid)){
                map.put("Admin", 1);} else {map.put("Admin", 0);}
            map.put("Status", 1);
            device.sendMessage(map);
        }
        @Override
        public void onDeviceLost(Device device) {
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e("Main", "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(User.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };
}
