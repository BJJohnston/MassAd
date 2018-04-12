package com.example.infero00o.massad;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;

import java.util.HashMap;

public class customMessage extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);

        final Button send = findViewById(R.id.sendCustomMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCustomMessage();
            }
        });
    }

    public void sendCustomMessage() {
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        int alertType = 3;
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();

        if (title != null && !title.isEmpty() || message != null && !message.isEmpty()) {




            HashMap<String, Object> content = new HashMap<>();
            content.put("alertType", alertType);
            content.put("title", title);
            content.put("text", message);
            content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            content.put("device_type", Build.DEVICE);


            com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
            builder.setContent(content);
            Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
        } Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();

    }
}
