package com.example.infero00o.massad;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
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

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;

import java.util.ArrayList;
import java.util.HashMap;

public class Admin extends AppCompatActivity {
    public connectedPeers Peers;
    private String TAG = "MainActivity";
    public String senderID;
    public int exitCount = 0;
    public ArrayList admin_ids = new ArrayList();
    public int personCount = 0;
    public String uuid;
    public String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");

        final TextView t = findViewById(R.id.userInfoText);
        t.setText("You are admin ID: " + uuid + "\n" + "Your location: " + location);

        final Button fire = findViewById(R.id.fire);
        fire.setVisibility(View.VISIBLE);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire();
            }
        });

        final Button flood = findViewById(R.id.flood);
        flood.setVisibility(View.VISIBLE);
        flood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flood();
            }
        });

        final Button custom = findViewById(R.id.custom);
        custom.setVisibility(View.VISIBLE);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom();
            }
        });

        final Button active = findViewById(R.id.active_shooter);
        active.setVisibility(View.VISIBLE);
        active.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                active();
            }
        });
        final Button connected = findViewById(R.id.connectedButton);
        connected.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){connected();}
        });

    }







    public void fire() {
        int alertType = 1;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }

    public void flood() {
        int alertType = 2;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }

    public void custom() {
        Intent intent = new Intent(getApplicationContext(), customMessage.class);
        startActivity(intent);
    }
    public void connected(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(intent, 0);
    }

    public void active() {
        int alertType = 4;
        HashMap<String, Object> content = new HashMap<>();
        content.put("alertType", alertType);
        content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
        content.put("device_type", Build.DEVICE);

        com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
        builder.setContent(content);
        Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
    }
}
