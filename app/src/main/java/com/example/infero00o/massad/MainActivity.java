
package com.example.infero00o.massad;

import com.bridgefy.sdk.client.BFEnergyProfile;
import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Config;
import com.example.infero00o.massad.R;
import com.example.infero00o.massad.*;

import android.app.PendingIntent;
import android.widget.TextView;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


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
import java.util.List;

public class MainActivity extends AppCompatActivity {


    public connectedPeers Peers;
    private String TAG = "MainActivity";
    public String senderID;
    public int exitCount = 0;
    public String admin_id = "0d5f5dd6-7ef8-4596-b5c5-cee8550d741b";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BluetoothAdapter btA = BluetoothAdapter.getDefaultAdapter();
        if (!btA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }


        Bridgefy.initialize(getApplicationContext(), "c567d3e3-cb18-4f5f-af10-ef5cf018aa3a", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                startBridgefy();
                Toast.makeText(getApplicationContext(), "Bridgefy Start", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });


        final Button fire = findViewById(R.id.fire);
        fire.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fire();
            }
        });

        final Button flood = findViewById(R.id.flood);
        flood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flood();
            }
        });

        final Button custom = findViewById(R.id.custom);
        custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                custom();
            }
        });

    }

    public void startBridgefy() {
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();
    }

    private MessageListener messageListener = new MessageListener() {
        @Override

        public void onMessageReceived(Message message) {





        }

        public void onBroadcastMessageReceived(Message message) {
       /*Peer peer = new Peer(message.getSenderId(), (String) message.getContent().get("device_name"));
       peer.setConnected(true);
       Peers.addChild(peer);
       String incomingMessage = (String) message.getContent().get("text");
       TextView textView = findViewById(R.id.textView);
       textView.setText(incomingMessage);*/

            int alertType = (int) message.getContent().get("alertType");
            senderID = (String) message.getSenderId();

            if (senderID.equals(admin_id)) {

                if (alertType != 3) {
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    startActivity(intent);
                } else {
                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    startActivity(intent);
                }

            }
        }

    };
    private StateListener stateListener = new StateListener() {
        @Override
        public void onDeviceConnected(final Device device, Session session) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            map.put("device_type", Build.DEVICE);
            device.sendMessage(map);
        }

        @Override
        public void onStartError(String message, int errorCode) {
            Log.e(TAG, "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };


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

}