package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;

/**
 * Records audio and saves it to audio file
 * GoogleRecorder.AUDIO_FILE_PATH
 *
 * Created by root on 12/29/16.
 */

public class VoiceRecorder implements IRecorder{

    /** Recording properties**/
    public static final int ELEMENTS_PER_BUFFER = 1024;

    public static final int BYTES_PER_ELEMENT = 2; // 2 bytes in 16bit format

    public static final int SAMPLING = 16000;

    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    public static int MIN_BUFFER_SIZE;

    /** parent activity**/
    private MainActivity mainView;

    private AudioRecord androidRecord;

    private boolean isRecording;

    private Thread writeFileThread;

    private SpeechStreamClient streamingClient;

    private boolean initialized = false;

    public VoiceRecorder(MainActivity mainView){

        this.mainView = mainView;
        VoiceRecorder.MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(VoiceRecorder.SAMPLING,
                VoiceRecorder.RECORDER_CHANNELS,VoiceRecorder.RECORDER_AUDIO_ENCODING) * 2;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    streamingClient = new SpeechStreamClient(VoiceRecorder.this);
                    initialized = true;

                } catch (InterruptedException e) {
                    Log.e(MainActivity.LOG_TAF, "Interrupted while initializing " +
                            "the channel to google speech api");
                } catch (GeneralSecurityException e) {
                    Log.e(MainActivity.LOG_TAF, e.getMessage());
                } catch (IOException e) {
                    if (e.getClass() == FileNotFoundException.class) {
                        Log.e(MainActivity.LOG_TAF, "No authentication key for google " +
                                "Cloud found, channel not created");
                    } else {
                        Log.e(MainActivity.LOG_TAF, e.getMessage());
                    }
                }
            }
        }).start();

        this.androidRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                VoiceRecorder.SAMPLING, VoiceRecorder.RECORDER_CHANNELS,
                VoiceRecorder.RECORDER_AUDIO_ENCODING, VoiceRecorder.MIN_BUFFER_SIZE);
    }


    @Override
    public void startRecording() {
        if(!initialized){
            Log.e(MainActivity.LOG_TAF, "Recorder not initialized. Cannot start recording");
            return;
        }
        this.mainView.getSpeechBtn().setText("Recording... Please Speak Now.");

        //initialize stream
        this.writeFileThread = new Thread(new Runnable() {
            public void run() {
                Log.i(MainActivity.LOG_TAF, "starting recording write to file thread");
                try {
                    readAudioInput();
                }catch( Exception e){
                    Log.e("EROOOR", e.getMessage());
                }
            }
        }, "AudioRecorder Thread");
       this.writeFileThread.start();
    }

    @Override
    public void stopRecording() {
        if(!this.isRecording){
            return;
        }
        this.mainView.getSpeechBtn().setText("Start Speech Recording");
        // stops the recording activity
        Log.i(MainActivity.LOG_TAF, "VoiceRecorder Stopping the record.");
        if (null != this.androidRecord) {
            this.isRecording = false;
            this.androidRecord.stop();
            this.writeFileThread = null;
            this.streamingClient.setStreamInitialized(false);
        }
    }
    private void readAudioInput() {

        byte[] buffer = new byte[VoiceRecorder.MIN_BUFFER_SIZE];

        this.androidRecord.startRecording();
        this.isRecording = true;
        while (this.isRecording) {
            long start = System.currentTimeMillis();
            // gets the voice output from microphone to byte format
            int read = this.androidRecord.read(buffer, 0, buffer.length);
            if(read < 0){
               Log.e(MainActivity.LOG_TAF,
                        "Error while reading data from MIC: " + read);
                continue;
            }
            try {
                streamingClient.recognizeBytes(buffer, read);
                long stop = System.currentTimeMillis() - start;
                Log.e("measuring " , "NEDED: " + stop);
            } catch (Exception e) {
                Log.e(MainActivity.LOG_TAF, "Recognition error. Stopping. Details: " + e.getMessage());
            }
        }
    }

    public void shutdown(){
        this.androidRecord.release();
        this.androidRecord = null;
        return;
    }

    public MainActivity getMainView() {
        return this.mainView;
    }
}
