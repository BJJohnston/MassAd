package com.example.infero00o.massad;

import android.os.Build;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;

import java.util.ArrayList;
import java.util.HashMap;

public class sendAdminMessage extends AppCompatActivity {
    public String uuid;
    public ArrayList admin_ids = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_admin_message);

        Bundle extras = getIntent().getExtras();
        admin_ids = extras.getStringArrayList("admin_ids");

        final Button send = findViewById(R.id.sendCustomMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendDirectMessage();
            }
        });
    }

    public void sendDirectMessage() {
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        int alertType = 5;
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();

        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {

        for (int i = 0; i < admin_ids.size(); i++) {
            uuid = admin_ids.get(i).toString();

            HashMap<String, Object> content = new HashMap<>();
            content.put("alertType", alertType);
            content.put("title", title);
            content.put("text", message);


            com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
            builder.setContent(content);
            builder.setReceiverId(uuid);
            Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
        }
        } else {Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();}

    }
}
