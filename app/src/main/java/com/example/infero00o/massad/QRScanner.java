package com.example.infero00o.massad;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bridgefy.sdk.client.RegistrationListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import com.bridgefy.sdk.client.Bridgefy;
import com.bridgefy.sdk.client.BridgefyClient;
import com.bridgefy.sdk.client.Device;
import com.bridgefy.sdk.client.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

public class QRScanner extends AppCompatActivity{

    private IntentIntegrator qrScan;
    public ArrayList admin_ids = new ArrayList();
    public String location;
    public String tel;
    public String uuid;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscanner);

        BluetoothAdapter btA = BluetoothAdapter.getDefaultAdapter();
        if (!btA.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, 0);
        }


        Button buttonScan = findViewById(R.id.buttonScan);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               scan();
            }
        });



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    JSONArray objArray = obj.getJSONArray("id");
                    location = obj.getString("location");
                    tel = obj.getString("tel");
                    int length = objArray.length();

                    for (int i =0; i<length; i++){
                         admin_ids.add(objArray.getString(i));
                    }

                    if (admin_ids.contains(uuid)){
                        Intent intent = new Intent(getApplicationContext(), Admin.class);
                        intent.putExtra("UUID", uuid);
                        intent.putExtra("location", location);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getApplicationContext(), User.class);
                        intent.putExtra("UUID", uuid);
                        intent.putExtra("location", location);
                        intent.putExtra("tel", tel);
                        intent.putStringArrayListExtra("admin_ids", admin_ids);
                        startActivity(intent);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void scan(){
        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }
}
