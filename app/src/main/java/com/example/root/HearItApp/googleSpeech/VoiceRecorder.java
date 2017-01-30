package com.example.root.HearItApp.googleSpeech;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

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

    int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
    int BytesPerElement = 2; // 2 bytes in 16bit format

    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    public void startRecording() {

        androidRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                GoogleRecorder.SAMPLING, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, GoogleRecorder.BYTES_PER_BUFFER * GoogleRecorder.BYTES_PER_SAMPLE);
        
        androidRecord.startRecording();
        isRecording = true;

        writeFileThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        writeFileThread.start();
    }

    public void stopRecording() {
        // stops the recording activity
        if (null != androidRecord) {
            isRecording = false;
            androidRecord.stop();
            androidRecord.release();
            androidRecord = null;
            writeFileThread = null;
        }
    }

    //convert short to byte
    private byte[] short2byte(short[] sData) {
        int shortArrsize = sData.length;
        byte[] bytes = new byte[shortArrsize * 2];
        for (int i = 0; i < shortArrsize; i++) {
            bytes[i * 2] = (byte) (sData[i] & 0x00FF);
            bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
            sData[i] = 0;
        }
        return bytes;

    }

    private void writeAudioDataToFile() {
        // Write the output audio in byte
        short sData[] = new short[GoogleRecorder.BYTES_PER_BUFFER];

        FileOutputStream os = null;
        try {
            os = new FileOutputStream(GoogleRecorder.AUDIO_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (isRecording) {
            // gets the voice output from microphone to byte format

            androidRecord.read(sData, 0, GoogleRecorder.BYTES_PER_BUFFER);
            System.out.println("Short wirting to file" + sData.toString());
            try {
                // // writes the data to file from buffer
                // // stores the voice buffer
                byte bData[] = short2byte(sData);
                os.write(bData, 0, GoogleRecorder.BYTES_PER_BUFFER *
                        GoogleRecorder.BYTES_PER_SAMPLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
