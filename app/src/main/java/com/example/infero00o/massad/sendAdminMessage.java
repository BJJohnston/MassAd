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
    //Variables to be used in sendAdminMessage class
    //Stores the UUID to send a message to
    public String uuid;
    //Stores the admin ids retrieved from User class
    public ArrayList admin_ids = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_admin_message);
        //Create bundle to retrieve data
        Bundle extras = getIntent().getExtras();
        admin_ids = extras.getStringArrayList("admin_ids");
        //Create button to handle sendCustomMessage
        final Button send = findViewById(R.id.sendCustomMessage);
        //Create onClick listener for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked call the sendDirectMessage method
            public void onClick(View view) {
                sendDirectMessage();
            }
        });
    }
    //Method to send direct message
    public void sendDirectMessage() {
        //Creates Edittexts to handle message content
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        //Sets the alertType integer to 5
        int alertType = 5;
        //Gets the text from the text boxes
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();
//Checks the title and message textboxes are not null
        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {
            //If not null then create HashMap to store message
            //Loop through admin ids arraylist retrieve an id and then sending a message to that admin
            //Until end of list
            for (int i = 0; i < admin_ids.size(); i++) {
                uuid = admin_ids.get(i).toString();

                HashMap<String, Object> content = new HashMap<>();
                //Store the alertType integer, title, and text in HashMap
                content.put("alertType", alertType);
                content.put("title", title);
                content.put("text", message);

                //Sends the message to devices using sendMessage
                com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
                builder.setContent(content);
                builder.setReceiverId(uuid);
                Bridgefy.sendMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
            }//If textboxes are null then display error to user
        } else {Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();}

    }
}
