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
    //Creates the media recorder and sets the outfile massAdAudio.3gp
    MediaRecorder mr = new MediaRecorder();
    String outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/massAdAudio.3gp";

    //When the application starts anything in this method will execute first
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_message);
        //Create buttons to handle record audio, play audio and send message
        //Create progress bar for audio recording
        final Button recordAudio = findViewById(R.id.recordAudio);
        final Button playAudio = findViewById(R.id.playAudio);
        final ProgressBar progressBar = findViewById(R.id.progressBar);
        final Button send = findViewById(R.id.sendCustomMessage);
        //Creates onClick listener for send button
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            //When clicked the sendCustomMessage method is called
            public void onClick(View view) {
                sendCustomMessage();
            }
        });


        //Set the play audio button enabled to false
        playAudio.setEnabled(false);

        //Creates onClick listener to record audio
        recordAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked check if app have permission to record audio
                if (ActivityCompat.checkSelfPermission(customMessage.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(customMessage.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            0);

                } else {
                    //Setup the recording
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
                            //If permission is granted prepare the media recorder then start it
                            mr.prepare();
                            mr.start();
                            //Animate the progress bar for 15 seconds for duration of recording
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
                                    //At the end of the progress bar
                                    //Stop recording
                                    //Set play audio button to enabled to allow audio playback
                                    //Set record audio button to disable to stop from overwriting audio recording
                                    super.onAnimationEnd(animation);
                                    mr.stop();
                                    mr.release();
                                    mr = null;
                                    recordAudio.setEnabled(false);
                                    playAudio.setEnabled(true);
                                    //Tell the user the recording process has finished
                                    Toast.makeText(getApplicationContext(), "Recording stopped", Toast.LENGTH_LONG).show();
                                }
                            });
                            //Starts the animation of the progress bar
                            animator.start();
                        }
                    } catch(IllegalStateException ise){
                        ise.printStackTrace();

                    } catch(IOException io){
                        io.printStackTrace();

                    }
                    //Tells the user that recording is in progress
                    Toast.makeText(getApplicationContext(), "Recording started", Toast.LENGTH_LONG).show();

                }
            }
        });


        //Creates onClick listener for play audio button
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //When clicked create a new mediaplayer to play audio
                MediaPlayer mp = new MediaPlayer();
                try{
                    //Set the source of the audio as the output file
                    mp.setDataSource(outputFile);
                    mp.prepare();
                    //Start playing the audio
                    mp.start();
                    //Alert user that audio playback is in progress
                    Toast.makeText(getApplicationContext(), "Playing Recording", Toast.LENGTH_LONG).show();
                    //Enable the record audio button to allow for re-recording of audio
                    recordAudio.setEnabled(true);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });




    }
    //Setups the media recorder
    public void setupRecording(){
        //Sets the audio source
        mr.setAudioSource(MediaRecorder.AudioSource.MIC);
        //Sets the output and encoder formats
        mr.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mr.setAudioEncoder(MediaRecorder.OutputFormat.THREE_GPP);
        //Sets the output file
        mr.setOutputFile(outputFile);
        //Set the max duration of recording to 15 seconds
        mr.setMaxDuration(15000);
    }

    //Method to send the custom message
    public void sendCustomMessage() {
        //Create text editors to hand text input by user
        final EditText messageText = findViewById(R.id.editMessage);
        final EditText titleText = findViewById(R.id.editTitle);
        //Set the alertType integer to 3
        int alertType = 3;
        //Gets the text from the text boxes
        String title = titleText.getText().toString();
        String message = messageText.getText().toString();
        //Checks the title and message textboxes are not null

        if (title != null && !title.isEmpty() && message != null && !message.isEmpty()) {


            //If not null then create HashMap to store message

            HashMap<String, Object> content = new HashMap<>();
            //Store the alertType integer, title, text, device name and device type in HashMap
            content.put("alertType", alertType);
            content.put("title", title);
            content.put("text", message);
            content.put("device_name", Build.MANUFACTURER + " " + Build.MODEL);
            content.put("device_type", Build.DEVICE);

            //Broadcasts the message to devices using sendBroadcastMessage
            com.bridgefy.sdk.client.Message.Builder builder = new com.bridgefy.sdk.client.Message.Builder();
            builder.setContent(content);
            Bridgefy.sendBroadcastMessage(builder.build(), BFEngineProfile.BFConfigProfileLongReach);
            //If textboxes are null then display error to user
        } else {Toast.makeText(getApplicationContext(), "Enter title and message!", Toast.LENGTH_LONG).show();}

    }
}
