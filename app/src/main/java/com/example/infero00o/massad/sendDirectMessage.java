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

import java.util.HashMap;

public class sendDirectMessage extends AppCompatActivity {
    //Stores UUID of message sender
    String uuid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);
        //Creates a bundle and retrieved the data from Admin class
        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        //Creates button to hand send button
        final Button send = findViewById(R.id.sendCustomMessage);
        //Creates onClick listener for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked the sendDirectMessage method is called
            public void onClick(View view) {
                sendDirectMessage();
            }
        });
    }
    //Method to send direct message
    public void sendDirectMessage() {
        //Edittexts to handle data in textboxes
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        //Set alertType integer to 5
        int alertType = 5;
        //Gets the text from the texboxes
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();
        //Check textboxes are not null
        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {



            //Creates a Hashmap to store alertType, title and message content
            HashMap<String, Object> content = new HashMap<>();
            content.put("alertType", alertType);
            content.put("title", title);
            content.put("text", message);


            //Sends the message directly to the selected UUID using sendMessage
            com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
            builder.setContent(content);
            builder.setReceiverId(uuid);
            Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
            //Finish the activity to prevent overlapping
            finish();
            //Display error to user if the textboxes are null
        } else {Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();}

    }
}
