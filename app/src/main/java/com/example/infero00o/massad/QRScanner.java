package com.example.infero00o.massad;

import android.bluetooth.BluetoothAdapter;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;

import android.widget.Toast;

import com.bridgefy.sdk.client.RegistrationListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

public class QRScanner extends AppCompatActivity {
    //Variables to be used throughout the application
    private IntentIntegrator qrScan;
    //Stores the admin ids from the QR Code
    public ArrayList admin_ids = new ArrayList();
    //Stores the location from the QR Code
    public String location;
    //Stores the telephone number from the QR Code
    public String tel;
    //Stores the UUID generated in Bridgefy.initialize
    public String uuid;

    //When the application starts anything in this method will execute first
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        //Get the default bluetooth adapter
        BluetoothAdapter btA = BluetoothAdapter.getDefaultAdapter();
        //If bluetooth is not enabled ask the user for mission to turn on bluetooth
        if (!btA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }

        //Find the scan button from activity_qrscanner.xml
        //Set a onClick listener to the button which calls the scan method
        Button buttonScan = findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scan();
            }
        });


        //Initialises bridgefy
        //Check if API key is correct, else throws an error message
        //Calls the uuid generator method and assigns it to uuid variable
        //If successful Bridgefy Start message is shown to the user
        Bridgefy.initialize(getApplicationContext(), "c567d3e3-cb18-4f5f-af10-ef5cf018aa3a", new RegistrationListener() {
            @Override
            public void onRegistrationSuccessful(BridgefyClient bridgefyClient) {
                uuid = bridgefyClient.getUserUuid();

                Toast.makeText(getApplicationContext(), "Bridgefy Start", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRegistrationFailed(int errorCode, String message) {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }


        });

    }
    //Deals with the result returned from the QR scanner
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //If QR code is empty
            if (result.getContents() == null) {
                //Display error message
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //If QR code contains data
                try {
                    //Get data and convert to JSON
                    JSONObject obj = new JSONObject(result.getContents());
                    //Get array of admin ids and assign then to a JSONArray
                    JSONArray objArray = obj.getJSONArray("id");
                    //Retrieve the location
                    location = obj.getString("location");
                    //Retrieve the telephone number
                    tel = obj.getString("tel");
                    //Get the length of the JSONArray
                    int length = objArray.length();
                    //Until the end of JSONArray is reached
                    for (int i = 0; i < length; i++) {
                        //Copy admin ids to admin_ids arraylist
                        admin_ids.add(objArray.getString(i));
                    }
                    //If the admin ids arraylist contains the uuid generated in Bridgefy.initialise
                    //Start Admin.class and send the uuid, location, telephone number and admin_ids
                    if (admin_ids.contains(uuid)) {
                        Intent intent = new Intent(getApplicationContext(), Admin.class);
                        intent.putExtra("UUID", uuid);
                        intent.putExtra("tel", tel);
                        intent.putExtra("location", location);
                        intent.putStringArrayListExtra("admin_ids", admin_ids);
                        startActivity(intent);
                    } else {
                        //If the admin ids arraylist doesnt contain the uuid then start the User.class
                        //SSend the uuid, location, telephone number and admin ids
                        Intent intent = new Intent(getApplicationContext(), User.class);
                        intent.putExtra("UUID", uuid);
                        intent.putExtra("location", location);
                        intent.putExtra("tel", tel);
                        intent.putStringArrayListExtra("admin_ids", admin_ids);
                        startActivity(intent);

                    }
                    //If result returned is null or doesnt match a JSONObject print the stack trace to the console
                    //Display what is scanned to the user
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //Create the new intent for the QR Scanner
    //Calls the initiate scan method to start scanning the QR Code
    public void scan() {
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }
}