package com.rycrosoft.Sayback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PromptActivity extends Activity {

    Button recordButton;
    Button playForwardButton;
    Button playBackwardButton;
    Button doneButton;

    Recorder recorder;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt);

        recorder = new Recorder();

        recordButton = (Button) findViewById(R.id.buttonRecord);
        playForwardButton = (Button) findViewById(R.id.buttonPlayForward);
        playBackwardButton = (Button) findViewById(R.id.buttonPlayBackward);
        doneButton = (Button) findViewById(R.id.prompt_buttonDone);

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

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent responseIntent = new Intent(v.getContext(),ResponseActivity.class);

                responseIntent.putExtra("forward",recorder.getForwardPlayer());
                responseIntent.putExtra("backward",recorder.getBackwardPlayer());
                startActivity(responseIntent);
            }
        });
    }
}
