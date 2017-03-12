package de.dhbw.studienarbeit.hearItApp.recorder;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.MainActivity;

import de.dhbw.studienarbeit.hearItApp.recorder.androidSpeechRecognition.AndroidVoiceRecorder;

import de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition.GoogleRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.textFieldRecorder.TextRecorder;

/**
 * Created by root on 12/27/16.
 */

public class RecorderFactory {

    public static IRecorder generate(int type, MainActivity mainView){
        switch (type) {

            case Constants.RECORDER_GOOGLE_CLOUD_CLIENT:
                return new GoogleRecorder(mainView);

            case Constants.RECORDER_ANDROID_VOICE_CLIENT:
                return new AndroidVoiceRecorder(mainView);

            default:
                return new TextRecorder(mainView);
        }
    }
}
