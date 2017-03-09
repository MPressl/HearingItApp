package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * Records audio and saves it to audio file
 * GoogleRecorder.AUDIO_FILE_PATH
 *
 * Created by root on 12/29/16.
 */

public class VoiceRecorder {

    private AudioRecord androidRecord;

    private boolean isRecording;

    private Thread writeFileThread;

    private SpeechStreamClient streamingClient;

    private boolean initialized = false;

    public VoiceRecorder(GoogleRecorder man){
        final GoogleRecorder manager = man;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    streamingClient = new SpeechStreamClient(manager);
                    initialized = true;

                } catch (InterruptedException e) {
                    Log.e(MainActivity.LOG_TAF, "Interrupted while initializing " +
                            "the channel to google speech api");
                } catch (GeneralSecurityException e) {
                    Log.e(MainActivity.LOG_TAF, e.getMessage());
                } catch (IOException e) {
                    if (e.getClass() == FileNotFoundException.class) {
                        Log.e(MainActivity.LOG_TAF, "No authentication key for google Cloud found, channel not created");
                    } else {
                        Log.e(MainActivity.LOG_TAF, e.getMessage());
                    }
                }
            }
        }).start();

        this.androidRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                GoogleRecorder.SAMPLING, GoogleRecorder.RECORDER_CHANNELS,
                GoogleRecorder.RECORDER_AUDIO_ENCODING, GoogleRecorder.MIN_BUFFER_SIZE);
    }


    public boolean startRecording() {
        if(!initialized){
            Log.e(MainActivity.LOG_TAF, "Recorder not initialized. Cannot start recording");
            return false;
        }
        Log.i(MainActivity.LOG_TAF, "IRecorder Channels: " + GoogleRecorder.RECORDER_CHANNELS);
        Log.i(MainActivity.LOG_TAF, "Encoding: " + GoogleRecorder.RECORDER_AUDIO_ENCODING);
        Log.i(MainActivity.LOG_TAF, "Sampling: " + GoogleRecorder.SAMPLING);

        Log.i(MainActivity.LOG_TAF, "Elements per buffer: " + GoogleRecorder.ELEMENTS_PER_BUFFER);
        Log.i(MainActivity.LOG_TAF, "Bytes per element: " + GoogleRecorder.BYTES_PER_ELEMENT);
               // GoogleRecorder.BYTES_PER_ELEMENT * GoogleRecorder.ELEMENTS_PER_BUFFER;
        Log.i(MainActivity.LOG_TAF, "MinBufferSize: " + GoogleRecorder.MIN_BUFFER_SIZE);


//FIXME: This is the media recorder solution
//       this.androidRecord = new MediaRecorder();
//        androidRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
//        this.androidRecord.setAudioSamplingRate(GoogleRecorder.SAMPLING);
//        this.androidRecord.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_16BIT);
//       // this.androidRecord.setAudioChannels(GoogleRecorder.RECORDER_CHANNELS);
//       this.androidRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//       this.androidRecord.setOutputFile(GoogleRecorder.AUDIO_FILE.getAbsolutePath());
//        this.androidRecord.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//        try {
//            this.androidRecord.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.androidRecord.start();
//        this.isRecording = true;

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
        return true;
    }

    public void stopRecording() {
        // stops the recording activity
        Log.i(MainActivity.LOG_TAF, "VoiceRecorder Stopping the record.");
        if (null != this.androidRecord) {
            this.isRecording = false;
            this.androidRecord.stop();
            this.androidRecord.release();
            this.writeFileThread = null;
        }
    }
    private void readAudioInput() {

        byte[] buffer = new byte[GoogleRecorder.MIN_BUFFER_SIZE];
             //   GoogleRecorder.ELEMENTS_PER_BUFFER * GoogleRecorder.BYTES_PER_ELEMENT];

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
        this.androidRecord = null;
        return;
    }

}
