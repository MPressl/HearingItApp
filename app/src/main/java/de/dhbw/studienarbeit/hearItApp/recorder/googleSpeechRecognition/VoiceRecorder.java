package de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition;

import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

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

    private GoogleRecorder manager;

    public VoiceRecorder(GoogleRecorder manager){
        this.manager = manager;
    }


    public void startRecording() {

        Log.i(MainActivity.LOG_TAF, "IRecorder Channels: " + GoogleRecorder.RECORDER_CHANNELS);
        Log.i(MainActivity.LOG_TAF, "Encoding: " + GoogleRecorder.RECORDER_AUDIO_ENCODING);
        Log.i(MainActivity.LOG_TAF, "Sampling: " + GoogleRecorder.SAMPLING);

        Log.i(MainActivity.LOG_TAF, "Elements per buffer: " + GoogleRecorder.ELEMENTS_PER_BUFFER);
        Log.i(MainActivity.LOG_TAF, "Bytes per element: " + GoogleRecorder.BYTES_PER_ELEMENT);

        GoogleRecorder.MIN_BUFFER_SIZE =
                GoogleRecorder.BYTES_PER_ELEMENT * GoogleRecorder.ELEMENTS_PER_BUFFER;

        Log.i(MainActivity.LOG_TAF, "MinBufferSize: " + GoogleRecorder.MIN_BUFFER_SIZE);

        this.androidRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                GoogleRecorder.SAMPLING, GoogleRecorder.RECORDER_CHANNELS,
                GoogleRecorder.RECORDER_AUDIO_ENCODING, GoogleRecorder.MIN_BUFFER_SIZE);

//FIXME: This is the media recorder solution
       // this.androidRecord = new MediaRecorder();
        //androidRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
//        this.androidRecord.setAudioChannels(RECORDER_CHANNELS);
        //this.androidRecord.setAudioEncodingBitRate(AudioFormat.ENCODING_PCM_16BIT);
       // this.androidRecord.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        this.androidRecord.setOutputFile(GoogleRecorder.AUDIO_FILE.getAbsolutePath());
//        this.androidRecord.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);

//        try {
//            this.androidRecord.prepare();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.androidRecord.start();
//        this.isRecording = true;

        this.writeFileThread = new Thread(new Runnable() {
            public void run() {
                Log.i(MainActivity.LOG_TAF, "starting recording write to file thread");
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
       this.writeFileThread.start();
    }

    public void stopRecording() {
        // stops the recording activity
        Log.i(MainActivity.LOG_TAF, "VoiceRecorder Stopping the record.");
        if (null != this.androidRecord) {
            this.isRecording = false;
            this.androidRecord.stop();
            this.androidRecord.release();
            this.androidRecord = null;
            this.writeFileThread = null;
        }
    }
    private void writeAudioDataToFile() {

        byte[] buffer = new byte[
                GoogleRecorder.ELEMENTS_PER_BUFFER * GoogleRecorder.BYTES_PER_ELEMENT];

 //       FileOutputStream os = null;
 //       try {
 //           os = new FileOutputStream(GoogleRecorder.AUDIO_FILE);
 //           Log.i("HearItApp", "File is: " + GoogleRecorder.AUDIO_FILE.getAbsolutePath());
 //       } catch (FileNotFoundException e) {
 //           e.printStackTrace();
//        }
        this.androidRecord.startRecording();
        this.isRecording = true;
        int offset = 0;
        while (this.isRecording) {
            // gets the voice output from microphone to byte format
            this.androidRecord.read(buffer, offset, buffer.length);

            for(int i = 0; i< buffer.length; ++i ) {
                Log.i(MainActivity.LOG_TAF, "Byte wirting to queue" + buffer[i]);
                this.manager.addByteToAudioQueue(buffer[i]);
            }
        }
    }

}
