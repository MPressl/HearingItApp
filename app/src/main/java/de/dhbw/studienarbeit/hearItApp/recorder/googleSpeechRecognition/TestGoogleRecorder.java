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
 * Created by Andi on 19.05.2017.
 */

public class TestGoogleRecorder implements IRecorder{

    private MainActivity parent;
    private Context context;

    private GoogleSpeechConverter converter;
    private boolean isRecording;
    private boolean initialized;

    public TestGoogleRecorder(MainActivity parent, Context context){
        this.context = context;
        this.parent = parent;
        try {
            this.converter = new GoogleSpeechConverter(this);
            initialized = true;
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void startRecording() {
        Log.d(MainActivity.LOG_TAF, "Starting Test Record");
        this.isRecording = true;
        this.parent.getSpeechBtn().setBackgroundResource(R.drawable.mic_start_recording_recording_circle);
        this.parent.getTxtViewRecInfo().setText("Recording...");
        this.parent.getTxtViewRecInfo().setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] byteBuffer = new byte[VoiceRecorder.MIN_BUFFER_SIZE];
                try (
                        BufferedInputStream inputStream = new BufferedInputStream(parent.getResources().openRawResource(R.raw.test_audio))) {
                    int read;
                    while ((read = inputStream.read(byteBuffer)) > 0) {
                        Log.e(MainActivity.LOG_TAF,
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
            Log.d(MainActivity.LOG_TAF, "Stopping Test Record");
        }
        this.parent.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                parent.getSpeechBtn().setBackgroundResource(R.drawable.mic_start_recording_not_recording_circle);
                parent.getTxtViewRecInfo().setText("Press the button!");
                parent.getTxtViewRecInfo().setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                MainActivity.SOUND_ANIMATION_SCALING_VALUE = 0;
            }
        });

        // stops the recording activity
        Log.i(MainActivity.LOG_TAF, "TestRecorder Stopping the record.");
        this.isRecording = false;
        this.converter.setStreamInitialized(false);

        //notify the main activity that recording stopped (if the call came from the conversion client
        if(this.parent.notifyStopRecord()){
            Log.d(MainActivity.LOG_TAF, "Notified main activity about unexpected recorder stop");
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
