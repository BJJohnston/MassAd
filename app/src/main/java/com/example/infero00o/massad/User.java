package com.example.infero00o.massad;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class User extends AppCompatActivity {
    public String uuid;
    public String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Bundle extras = getIntent().getExtras();
        uuid = extras.getString("UUID");
        location = extras.getString("location");


        TextView t = findViewById(R.id.userText);
        t.setText("Thank you for logging in!\n" + "Your user ID is: " + uuid + "\n" + "Your located at: " + location + "\n" + "Please keep this application open and wait for any alerts!");
    }
}
