package com.example.root.HearItApp;

import com.example.root.HearItApp.androidSpeech.AndroidVoiceRecorder;

import com.example.root.HearItApp.googleSpeech.GoogleRecorder;

/**
 * Created by root on 12/27/16.
 */

public class RecorderFactory {

    public static Recorder generate(int type, MainActivity mainView){
        switch (type) {
            case 0:
                return new GoogleRecorder(mainView);
            default:
                return new AndroidVoiceRecorder();
        }
    }
}
