package com.example.infero00o.massad;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bridgefy.sdk.client.BFEngineProfile;
import com.bridgefy.sdk.client.Bridgefy;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class customMessage extends AppCompatActivity {
    MediaRecorder mr = new MediaRecorder();
    String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/massAdAudio.3gp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);
        final Button recordAudio = findViewById(R.id.recordAudio);
        final Button playAudio = findViewById(R.id.playAudio);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button send = findViewById(R.id.sendCustomMessage);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendCustomMessage();
            }
        });



        playAudio.setEnabled(false);


        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(customMessage.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(customMessage.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            0);

                } else {
                    setupRecording();



                    try {
                        int permission = ActivityCompat.checkSelfPermission(customMessage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        String[] PERMISSIONS_STORAGE = {
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        if (permission != PackageManager.PERMISSION_GRANTED) {
                            // We don't have permission so prompt the user
                            ActivityCompat.requestPermissions(
                                    customMessage.this,
                                    PERMISSIONS_STORAGE,
                                    1
                            );
                        } else {

                            mr.prepare();
                            mr.start();
                            ValueAnimator animator = ValueAnimator.ofInt(0, progressBar.getMax());
                            animator.setDuration(15000);
                            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    progressBar.setProgress((Integer) valueAnimator.getAnimatedValue());
                                }
                            });
                            animator.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    mr.stop();
                                    mr.release();
                                    mr = null;
                                    recordAudio.setEnabled(false);
                                    playAudio.setEnabled(true);
                                    Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG).show();
                                }
                            });
                            animator.start();
                        }
                        } catch(IllegalStateException ise){
                            ise.printStackTrace();

                        } catch(IOException io){
                            io.printStackTrace();

                        }

                        Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

                }
            }
        });



        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MediaPlayer mp = new MediaPlayer();
                try{
                    mp.setDataSource(outputFile);
                    mp.prepare();
                    mp.start();
                    Toast.makeText(getApplicationContext(), "Playing Recording", Toast.LENGTH_LONG).show();
                    recordAudio.setEnabled(true);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });




    }
    public void setupRecording(){
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setAudioEncoder(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setOutputFile(outputFile);
        mr.setMaxDuration(15000);
    }

    public void sendCustomMessage() {
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        int alertType = 3;
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();

        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {




            HashMap<String, Object> content = new HashMap<>();
            content.put("alertType", alertType);
            content.put("title", title);
            content.put("text", message);
            content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            content.put("device_type", Build.DEVICE);


            com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
            builder.setContent(content);
            Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
        } else {Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();}

    }
}
