package com.rycrosoft.Sayback;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created with IntelliJ IDEA.
 * User: rmoriarty
 * Date: 2/1/14
 * Time: 4:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResponseActivity extends Activity {

    private static final int RECORDER_SAMPLERATE = 8000;

    Button playButton;
    Button recordButton;
    Button playForwardButton;
    Button playBackwardButton;
    Button giveUpButton;

    Recorder recorder;

    short[] forwardPrompt;
    short[] backwardPrompt;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.response);

        Intent intent = getIntent();

        recorder = new Recorder();

        playButton = (Button) findViewById(R.id.buttonPlay);
        recordButton = (Button) findViewById(R.id.buttonRecord);
        playForwardButton = (Button) findViewById(R.id.buttonPlayForward);
        playBackwardButton = (Button) findViewById(R.id.buttonPlayBackward);
        giveUpButton = (Button) findViewById(R.id.response_buttonGiveUp);

        forwardPrompt = intent.getShortArrayExtra("forward");
        backwardPrompt = intent.getShortArrayExtra("backward");

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(backwardPrompt);
            }
        });


        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(recorder.record())
                    recordButton.setText("STOP RECORDING");
                else
                    recordButton.setText("START RECORDING");
            }
        });

        playForwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.play(true);
                recordButton.setText("START RECORDING");
            }
        });

        playBackwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recorder.play(false);
            }
        });

        giveUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(forwardPrompt);
            }
        });
    }

    public void play(short[] sound) {
// Get the file we want to playback.

// Create a new AudioTrack object using the same parameters as the AudioRecord
// object used to create the file.
        int bufferSize = AudioTrack.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                RECORDER_SAMPLERATE,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufferSize,
                AudioTrack.MODE_STREAM);
// Start playback

        audioTrack.play();

// Write the promptLoader buffer to the AudioTrack object
        audioTrack.write(sound, 0, sound.length);

        audioTrack.stop();
        audioTrack.release();
    }

}
