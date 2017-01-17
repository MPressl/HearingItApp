package com.example.root.HearItApp.googleSpeech;

import android.os.Environment;

import com.example.root.HearItApp.MainActivity;
import com.example.root.HearItApp.Recorder;

import java.io.File;

/**
 *Created by Martin on 12/15/16.
 */

public class GoogleRecorder implements Recorder {

    private MainActivity mainView;

    public static File AUDIO_FILE;


    public static final int SAMPLING = 16000;

    public static final int BYTES_PER_BUFFER = 3200; //buffer size in bytes

    public static final int BYTES_PER_SAMPLE = 2; //bytes per sample for LINEAR16

    private VoiceRecorder voiceRecorder;

    /**
     * Constructor
     */
    public GoogleRecorder(MainActivity mainView){

        File externalRoot = Environment.getExternalStorageDirectory();
        File tempDir = new File(externalRoot, ".myAppTemp");
        AUDIO_FILE = new File(tempDir.getPath() + "recording.mp3");

        this.mainView = mainView;
        this.voiceRecorder = new VoiceRecorder();

    }

    @Override
    public void startRecording() {

        voiceRecorder.startRecording();


//        new Thread(new GoogleSpeechStreamer(this),
  //              "Streaming Thread").start();
    }

    @Override
    public void stopRecording() {
        this.voiceRecorder.stopRecording();
    }

    public MainActivity getMainView(){
        return mainView;
    }

}
