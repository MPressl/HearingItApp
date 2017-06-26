package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.ISpeechToTextConverter;

/**
 *  Created by Martin
 */

public class TestGoogleRecorder implements IRecorder{

    private MainActivity parent;
    private GoogleSpeechConverter converter;
    private boolean isRecording;
    private boolean initialized;

    public TestGoogleRecorder(MainActivity parent){
        this.parent = parent;
        try {
            this.converter = new GoogleSpeechConverter(this);
            initialized = true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void startRecording() {
        Log.d(MainActivity.LOG_TAG, "Starting Test Record");
        this.isRecording = true;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] byteBuffer = new byte[VoiceRecorder.MIN_BUFFER_SIZE];
                try (
                        BufferedInputStream inputStream = new BufferedInputStream(parent.getResources().openRawResource(R.raw.test_audio))) {
                    int read;
                    while ((read = inputStream.read(byteBuffer)) > 0) {
                        Log.e(MainActivity.LOG_TAG,
                                "Reading from File: " + read);
                        converter.recognizeBytes(byteBuffer, read);
                        Thread.sleep(75);
                    }
                    stopRecording();

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();


    }

    @Override
    public void stopRecording() {
        if(this.parent.notifyStopRecord()){
            Log.d(MainActivity.LOG_TAG, "Stopping Test Record");
        }

        // stops the recording activity
        Log.i(MainActivity.LOG_TAG, "TestRecorder Stopping the record.");
        this.isRecording = false;
        this.converter.setStreamInitialized(false);

        //notify the main activity that recording stopped (if the call came from the conversion client
        if(this.parent.notifyStopRecord()){
            Log.d(MainActivity.LOG_TAG, "Notified main activity about unexpected recorder stop");
        }

    }

    @Override
    public void shutdown() {

    }

    @Override
    public MainActivity getMainView() {
        return this.parent;
    }

    public boolean isInitialize(){ return this.initialized; }

    @Override
    public boolean isRecording(){ return this.isRecording; }
}
