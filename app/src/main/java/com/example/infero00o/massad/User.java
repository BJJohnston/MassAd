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
    //Variable to be used within User
    //Creates a new array list to store the admin ids passed by the QRScanner
    public ArrayList admin_ids = new ArrayList();
    //Stores the telephone number passed by the QRScanner
    public String tel;
    //Stores the location passed by the QRScanner
    public String location;
    //Stores the UUID of the device passed by the QRScanner
    public String uuid;
    //Stores the senderID of any messages that are recieved
    public String senderID;


    @Override
    //When the application starts anything in this method will execute first
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        //Creates a bundle and retrieves the data sent by the QRScanner
        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");
        admin_ids = extras.getStringArrayList("admin_ids");

        //Calls the method below to start Bridgefy messageListener and
        //stateListener
        startBridgefy();

        //Creates a button to handle message admin button
        Button messageAdmin = findViewById(R.id.messageAdmin);
        //Creates onClick listener for the message admin button
        messageAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked create new intent to start sendAdminMessage class activity
                Intent intent = new Intent(view.getContext(), sendAdminMessage.class);
                //Send the admin ids to the sendAdminMessage class
                intent.putStringArrayListExtra("admin_ids", admin_ids);
                //Start the activity
                startActivity(intent);
            }
        });
        //Creates a textview to hand user info
        TextView t = findViewById(R.id.userText);
        //Displays the users UUID and location
        t.setText("Thank you for logging in!\n" + "Your user ID is: " + uuid + "\n" + "Your located at: " + location + "\n" + "Please keep this application open and wait for any alerts!");
    }

    //Starts the Bridgefy messageListener and stateListener
    //Creates a new configuration for Bridgefy to use
    //Sets the Bluetooth performance mode to High
    //Sets encryption as true to use RSA message encryption
    //Displays a start message to signal Bridgefy has been started
    public void startBridgefy() {
        Config.Builder builder = new Config.Builder();
        builder.setEnergyProfile(BFEnergyProfile.HIGH_PERFORMANCE);
        builder.setEncryption(true);
        Bridgefy.start(messageListener, stateListener);
        Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_LONG).show();
    }

    //Creates the messageListener which listens for incoming messages
    private MessageListener messageListener = new MessageListener() {
        @Override

        public void onMessageReceived(Message message) {
            //If the message conatins a alerType integer value
            if (message.getContent().containsKey("alertType")) {
                //Get the integer value from the message
                double alertType = (double) message.getContent().get("alertType");
                //If the alertType integer is 5 == custom message
                if (alertType == 5){


                    //Get the title of the message
                    String title = (String) message.getContent().get("title");
                    //Get the message text
                    String text = (String) message.getContent().get("text");
                    //Get the sender UUID
                    String uuid = message.getSenderId();
                    //Create a new intent to start the directMessage class
                    Intent intent = new Intent(getApplicationContext(), directMessage.class);
                    //Send the collected variables to this class
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("senderID", uuid);
                    //Start the directMessage class activity
                    startActivity(intent);
                    //If the alertType integer is 6 == remove device from network
                } else  if (alertType == 6){
                    //Stop Bridgefy messageListener and stateListener
                    Bridgefy.stop();
                    //Finish the running activity
                    finish();
                    //Move the activity out of view to the user
                    moveTaskToBack(true);
                    //Kill any massAd running processes
                    android.os.Process.killProcess(android.os.Process.myPid());
                    //Exit the application
                    System.exit(1);


                }}
        }




        //When an emergency broadcast message is recieved
        public void onBroadcastMessageReceived(Message message) {
            //Ge the alertType integer from the message
            int alertType = (int) message.getContent().get("alertType");
            //Get the sender UUID from the message
            senderID = message.getSenderId();


            //If the admin_ids arraylist contains the sender UUID
            //The sender therefore has admin status
            if (admin_ids.contains(senderID)) {
                //If the alert type does not equal 3 == not a custom message
                if (alertType != 3) {
                    //Create a new intent to start the Alert class
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    //Send the SENDER ID, ALERT TYPE AND TEL(retrieved from QR Code) to Alert class
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TEL", tel);
                    //Start the Alert class activity
                    startActivity(intent);
                }
                //If the alertType == 3 == custom message
                else {
                    //Get the title and text of the message
                    String title = (String) message.getContent().get("title");
                    String text = (String) message.getContent().get("text");
                    //Create an intent to start the Alert class
                    Intent intent = new Intent(getApplicationContext(), Alert.class);
                    //Send the SENDER ID, ALERT TYPE, TITLE, MESSAGE AND TEL(retrieved from QR Code) to Alert class
                    intent.putExtra("SENDER_ID", senderID);
                    intent.putExtra("ALERT_TYPE", alertType);
                    intent.putExtra("TITLE", title);
                    intent.putExtra("MESSAGE", text);
                    intent.putExtra("TEL", tel);
                    //Start the Alert class activity
                    startActivity(intent);
                }

            }
        }

    };
    //Creates the stateListener to listener for devices in range
    private StateListener stateListener = new StateListener() {
        //When a device is connected to the network
        @Override
        public void onDeviceConnected(final Device device, Session session) {

            //Creat a HashMap and insert device UUID, MAKE, MODEL
            //ADMIN status if the admin_ids contains this devices UUID
            //Set the connected status to 1
            //Send the message to the connected device
            HashMap < String, Object > map = new HashMap < > ();
            map.put("ID", uuid);
            map.put("Make", Build.MANUFACTURER);
            map.put("Model", Build.MODEL);
            if (admin_ids.contains(uuid)) {
                map.put("Admin", 1);
            } else {
                map.put("Admin", 0);
            }
            map.put("Status", 1);
            device.sendMessage(map);
        }

        @Override
        public void onDeviceLost(Device device) {
        }
        //If the stateListener fails to start log the error
        //If the error is insufficient permissions ask the user for permission
        @Override
        public void onStartError(String message, int errorCode) {
            Log.e("Main", "onStartError: " + message);

            if (errorCode == StateListener.INSUFFICIENT_PERMISSIONS) {
                ActivityCompat.requestPermissions(User.this,
                        new String[] {
                                Manifest.permission.ACCESS_FINE_LOCATION
                        }, 0);
            }
        }
    };
}
