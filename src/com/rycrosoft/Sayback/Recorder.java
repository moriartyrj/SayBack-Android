package com.rycrosoft.Sayback;

import android.media.*;
import android.view.View;
import android.widget.Button;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: rmoriarty
 * Date: 2/2/14
 * Time: 5:31 PM
 * To change this template use File | Settings | File Templates.
 */
public class Recorder {
    private static final int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private short[] promptLoader;

    private short[] backwardPlayer;
    private short[] forwardPlayer;
    private static final int loaderSize = 500000;

    int bufferSize;
    int playerSize;



    public Recorder() {
        promptLoader = new short[loaderSize];
        playerSize = 0;

        bufferSize =  AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);

    }

    public boolean record() {
        if (!isRecording) {
            recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                    RECORDER_AUDIO_ENCODING, bufferSize);

            recorder.startRecording();
            isRecording = true;
            recordingThread = new Thread(new Runnable() {
                public void run() {
                    writeAudioDataToFile();
                }
            }, "AudioRecorder Thread");
            recordingThread.start();

        } else {
            // stops the recording activity
            if (null != recorder) {
                isRecording = false;
                recorder.stop();
                recorder.release();
                recorder = null;
                recordingThread = null;
            }
        }

        return isRecording;
    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        short[] buffer = new short[bufferSize];


        int i = 0;

        while (isRecording && i < loaderSize) {
            // gets the voice output from microphone to byte format
            try {
                int bufferReadResult = recorder.read(buffer, 0, bufferSize);
                for (int j = 0; j < bufferReadResult; j++) {
                    if(i < loaderSize) {
                        promptLoader[i] = buffer[j];
                        i++;
                    } else {
                        break;
                    }
                }
//                    dos.writeShort(buffer[j]);
//
//            } catch (IOException e) {
//                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
        playerSize = i;
        backwardPlayer = new short[playerSize];
        forwardPlayer = new short[playerSize];

        for(int j = 0; j < playerSize; j++) {
            backwardPlayer[playerSize - j - 1] = promptLoader[j];
            forwardPlayer[j] = promptLoader[j];
        }

    }

    public void play(boolean forward) {
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
        if(forward) {
            audioTrack.write(forwardPlayer, 0, playerSize);
        } else {
            audioTrack.write(backwardPlayer, 0, playerSize);
        }

        audioTrack.stop();
        audioTrack.release();

    }

    public void readFromFile() {
        String filepath = "/sdcard/voice8K16bitmono.pcm";
        File file = new File(filepath);
// Get the length of the audio stored in the file (16 bit so 2 bytes per short)
// and create a short array to store the recorded audio.
        int musicLength = (int)(file.length()/2);
        short[] oldmusic = new short[musicLength];


        try {
// Create a DataInputStream to read the audio data back from the saved file.
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);

// Read the file into the promptLoader array.
            int i = 0;
            while (dis.available() > 0) {
//                promptLoader[musicLength-1-i] = dis.readShort();
                promptLoader[i] = dis.readShort();
                i++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

// Close the input streams.
        //dis.close();

    }

    public void writeToFile() {
        String filePath = "/sdcard/voice8K16bitmono.pcm";
        File file = new File(filePath);
        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        DataOutputStream dos = null;

        try {
            // Create a DataOuputStream to write the audio data into the saved file.
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            dos = new DataOutputStream(bos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
//            int bufferReadResult = recorder.read(buffer, 0, bufferSize);
//            for (int j = 0; j < bufferReadResult; j++) {
//                if(i < loaderSize) {
//                    promptLoader[loaderSize - 1 - i] = buffer[j];
//                    i++;
//                } else {
//                    break;
//                }
//            }
//                    dos.writeShort(buffer[j]);
//
//            } catch (IOException e) {
//                e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        try {
            dos.close();
            bos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public short[] getForwardPlayer() {
        return forwardPlayer;
    }

    public short[] getBackwardPlayer() {
        return backwardPlayer;
    }

}
