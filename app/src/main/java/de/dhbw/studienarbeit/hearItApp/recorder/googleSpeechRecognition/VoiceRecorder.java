package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.ISpeechToTextConverter;

/**
 * Native Voice Recorder
 * Uses the AndroidRecord to record bytes from the microphone and sends the recorded bytes
 * to a ISpeechToTextConverter implementation to convert the speech to text
 */

public class VoiceRecorder implements IRecorder{

    public static final int SAMPLING = 16000;

    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    public static int MIN_BUFFER_SIZE;

    /** parent activity**/
    private MainActivity mainView;

    private AudioRecord androidRecord;

    private boolean isRecording;

    private Thread writeFileThread;

    private ISpeechToTextConverter streamingClient;

    private boolean initialized = false;

    /**
     * Constructor initializing recorder's components such as the AndroidRecord instance
     * and the conversion client
     * @param mainView
     */
    public VoiceRecorder(MainActivity mainView){

        this.mainView = mainView;
        VoiceRecorder.MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(VoiceRecorder.SAMPLING,
                VoiceRecorder.RECORDER_CHANNELS,VoiceRecorder.RECORDER_AUDIO_ENCODING) * 2;

        initializeSpeechConverter();

        this.androidRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                VoiceRecorder.SAMPLING, VoiceRecorder.RECORDER_CHANNELS,
                VoiceRecorder.RECORDER_AUDIO_ENCODING, VoiceRecorder.MIN_BUFFER_SIZE);
    }

    /**
     * Method initializes a new Speech to Text conversion client
     */
    private void initializeSpeechConverter() {
        try{
            streamingClient = new GoogleSpeechConverter(VoiceRecorder.this);
//TODO: initialized varibale needed?
            initialized = true;

        } catch (InterruptedException e) {
            Log.e(MainActivity.LOG_TAG, "Interrupted while initializing " +
                    "the channel to google speech api");
        } catch (GeneralSecurityException e) {
            Log.e(MainActivity.LOG_TAG, e.getMessage());
        } catch (IOException e) {
            if (e.getClass() == FileNotFoundException.class) {
                Log.e(MainActivity.LOG_TAG, "No authentication key for google " +
                        "Cloud found, channel not created");
            } else {
                Log.e(MainActivity.LOG_TAG, e.getMessage());
            }
        }

    }


    @Override
    public void startRecording() {
        if(!initialized){
            Log.e(MainActivity.LOG_TAG, "Recorder not initialized. Cannot start recording");
            return;
        }
        //initialize stream
        this.writeFileThread = new Thread(new Runnable() {
            public void run() {
                Log.i(MainActivity.LOG_TAG, "starting recording write to file thread");
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
        // stops the recording activity
        Log.i(MainActivity.LOG_TAG, "VoiceRecorder Stopping the record.");
        this.isRecording = false;
        if (null != this.androidRecord) {
            this.androidRecord.stop();
            this.writeFileThread = null;
            this.streamingClient.setStreamInitialized(false);
        }
        //notify the main activity that recording stopped (if the call came from the conversion client
        if(this.mainView.notifyStopRecord()){
            Log.d(MainActivity.LOG_TAG, "Notified main activity about unexpected recorder stop");
        }
    }

    /**
     * method reads audio data from selected resource (microphone)
     * and calls the selected conversion client to convert the partial audio data
     * into text
     */
    private void readAudioInput() {

        byte[] byteBuffer = new byte[VoiceRecorder.MIN_BUFFER_SIZE];

        this.androidRecord.startRecording();
        this.isRecording = true;
        int recordingTime = 0;
        while (this.isRecording) {
            long start = System.currentTimeMillis();
            // gets the voice output from microphone to byte format
            short[] shortBuffer = new short[VoiceRecorder.MIN_BUFFER_SIZE / 2];
            int read = this.androidRecord.read(shortBuffer, 0, shortBuffer.length);
            if(read < 0){
               Log.e(MainActivity.LOG_TAG,
                        "Error while reading data from MIC: " + read);
                continue;
            }
            try {
                this.mainView.showSoundAnimation(shortBuffer);
                byteBuffer = this.convertShortToByte(shortBuffer);
                streamingClient.recognizeBytes(byteBuffer, read);
                long stop = System.currentTimeMillis() - start;
                Log.e("measuring " , "NEDED: " + stop);
                //recordingTime += stop;
                //if(recordingTime >= 50000){
                    //Google Speech api has a time limit of 65 seconds of streaming
                    //so generate a new stream after 50 seconds
                //    this.streamingClient = new GoogleSpeechConverter(this);
                //    recordingTime = 0;
             //   }
            } catch (Exception e) {
                Log.e(MainActivity.LOG_TAG, "Recognition error. Stopping. Details: " + e.getMessage());
           }
        }
    }

    /**
     * Splits an array of 16 bit shorts into an array of 8bit bytes
     * Little Endian Style, Low Nibble, High Nibble, Low Nibble, High Nibble
     * @param buffer
     * @return
     */
    private byte[] convertShortToByte(short[] buffer) {
        byte[] byteBuffer = new byte[buffer.length * 2];
        for (int i = 0; i < buffer.length; i++) {
            //take low nibble from short and add it to array
            byteBuffer[i * 2] = (byte) (buffer[i] & 0x00FF);
            //take the high nibble of the short and add it to array
            byteBuffer[(i * 2) + 1] = (byte) (buffer[i] >> 8);
        }
        return byteBuffer;

    }


    public void shutdown(){
        this.androidRecord.release();
        this.androidRecord = null;
        return;
    }

    public MainActivity getMainView() {
        return this.mainView;
    }

    public boolean isInitialize(){ return this.initialized; }

    public boolean isRecording(){ return this.isRecording; }
}
