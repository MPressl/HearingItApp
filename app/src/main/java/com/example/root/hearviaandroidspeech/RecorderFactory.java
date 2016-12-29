package com.example.root.hearviaandroidspeech;

import com.example.root.hearviaandroidspeech.androidSpeech.AndroidVoiceRecorder;

import GoogleSpeech.CloudStreamClient;

/**
 * Created by root on 12/27/16.
 */

public class RecorderFactory {

    public static Recorder generate(int type, MainActivity mainView){
        switch (type) {
            case 0:
                return new CloudStreamClient(mainView);
            default:
                return new AndroidVoiceRecorder();
        }
    }
}
