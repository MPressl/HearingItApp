package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.media.AudioFormat;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.TimeUnit;

/**
 *Created by Martin on 12/15/16.
 */

public class GoogleRecorder implements IRecorder {

    private MainActivity mainView;

/** Recording properties**/
    public static final int ELEMENTS_PER_BUFFER = 1024;

    public static final int BYTES_PER_ELEMENT = 2; // 2 bytes in 16bit format

    public static final int SAMPLING = 16000;

    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    public static int MIN_BUFFER_SIZE;

/** The IRecorder**/
    private VoiceRecorder voiceRecorder;

/** The Google  Speech Streaming Service**/
    private RequestStreamClient streamingThread;

/** Shared queue of Audio Data, recorder adds, streaming thread reads**/
    private BlockingDeque<Byte> audioDataQueue;

    /**
     * Constructor
     */
    public GoogleRecorder(MainActivity mainView){
        this.mainView = mainView;
    }

    @Override
    public void startRecording() {
        this.voiceRecorder = new VoiceRecorder(this);
        this.voiceRecorder.startRecording();
        this.streamingThread = new RequestStreamClient(this);

       new Thread(this.streamingThread,
              "Streaming Thread").start();
    }

    @Override
    public void stopRecording() {
        this.voiceRecorder.stopRecording();
        //for testing play record
       // if(!GoogleRecorder.AUDIO_FILE.exists()){
         //   Log.e(MainActivity.LOG_TAF, "File does not exist.");
           // return;
        //}
        //MediaPlayer mPlayer = new MediaPlayer();
        //try {
          //  Log.i(MainActivity.LOG_TAF, "Playing file " + GoogleRecorder.AUDIO_FILE.getAbsolutePath());
          //  mPlayer.setDataSource(GoogleRecorder.AUDIO_FILE.getAbsolutePath());
          //  mPlayer.prepare();
          //  mPlayer.start();
       // } catch (IOException e) {
        //    Log.e(MainActivity.LOG_TAF, "prepare() of Media Player failed");
       // }

    }

    public MainActivity getMainView(){
        return this.mainView;
    }

    public void addByteToAudioQueue(byte audioByte) {
        this.audioDataQueue.add(audioByte);
    }

    public byte readByteFromQueue() throws InterruptedException {
        return this.audioDataQueue.poll(500, TimeUnit.MILLISECONDS);
    }
}
