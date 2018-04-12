
package com.example.infero00o.massad;

import com.bridgefy.sdk.client.BFEnergyProfile;
import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Config;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;
import com.bridgefy.sdk.client.MessageListener;
import com.bridgefy.sdk.client.RegistrationListener;
import com.bridgefy.sdk.client.Session;
import com.bridgefy.sdk.client.StateListener;
import com.example.infero00o.massad.R;
import com.example.infero00o.massad.*;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    public ArrayList admin_ids = new ArrayList();
    public String uuid;
    public String senderID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (admin_ids.isEmpty()) {
            qrScanner();
        }

        BluetoothAdapter btA = BluetoothAdapter.getDefaultAdapter();
        if (!btA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }


        Bridgefy.initialize(getApplicationContext(), "c567d3e3-cb18-4f5f-af10-ef5cf018aa3a", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                uuid = bridgefyClient.getUserUuid();
                startBridgefy();
                Toast.makeText(getApplicationContext(), "Bridgefy Start", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
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
            int alertType = (int) message.getContent().get("alertType");
            senderID = message.getSenderId();
            //{"id":"0d5f5dd6-7ef8-4596-b5c5-cee8550d741b"}

            if (admin_ids.contains(senderID)) {

                if (alertType != 3) {
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
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
            Log.e("Main", "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
            }
        }
    };

    public void qrScanner(){
        Intent intent = new Intent(getApplicationContext(), QRScanner.class);
        startActivityForResult(intent, 1);
    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1){
            if (resultCode == RESULT_OK){
                admin_ids = data.getStringArrayListExtra("admin_ids");
                if (admin_ids.contains(uuid)){
                    Intent intent = new Intent(getApplicationContext(), Admin.class);
                    intent.putExtra("UUID", uuid);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), User.class);
                    intent.putExtra("UUID", uuid);
                    startActivity(intent);

                }


            }
        }
    }

}