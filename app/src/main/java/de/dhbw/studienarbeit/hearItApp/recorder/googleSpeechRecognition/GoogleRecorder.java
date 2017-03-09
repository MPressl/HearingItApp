package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *Created by Martin on 12/15/16.
 */

public class GoogleRecorder implements IRecorder {

    public static File AUDIO_FILE;
    private MainActivity mainView;

/** Recording properties**/
    public static final int ELEMENTS_PER_BUFFER = 1024;

    public static final int BYTES_PER_ELEMENT = 2; // 2 bytes in 16bit format

    public static final int SAMPLING = 16000;

    public static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;

    public static int MIN_BUFFER_SIZE;

    private boolean isRecording;

/** The IRecorder**/
    private VoiceRecorder voiceRecorder;

    /**
     * Constructor
     */
    public GoogleRecorder(MainActivity mainView){
        this.mainView = mainView;
        GoogleRecorder.MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(GoogleRecorder.SAMPLING,
                GoogleRecorder.RECORDER_CHANNELS,GoogleRecorder.RECORDER_AUDIO_ENCODING) * 2;
        this.voiceRecorder = new VoiceRecorder(this);
    }

    @Override
    public void startRecording() {
        this.isRecording = true;
        this.voiceRecorder.startRecording();
    }

    @Override
    public void stopRecording() {
        if(this.isRecording == false){
            return;
        }
        this.isRecording = false;
        this.voiceRecorder.stopRecording();
    }

    @Override
    public void shutdown() {
        this.stopRecording();
        this.voiceRecorder.shutdown();
        return;
    }

    public MainActivity getMainView(){
        return this.mainView;
    }
}
