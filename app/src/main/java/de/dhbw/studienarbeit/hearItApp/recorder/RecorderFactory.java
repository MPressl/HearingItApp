package de.dhbw.studienarbeit.hearItApp.recorder;

import android.content.Context;
import android.util.Log;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.MainActivity;

import de.dhbw.studienarbeit.hearItApp.recorder.androidSpeechRecognition.AndroidVoiceRecorder;

import de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition.TestGoogleRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.googleSpeechRecognition.VoiceRecorder;
import de.dhbw.studienarbeit.hearItApp.recorder.textFieldRecorder.TextRecorder;

/**
 * Factory class for recorders
 *
 *  Created by Martin
 */

public class RecorderFactory {

    /**
     * method takes the id of a recorder and creates the recorder with the respective id
     * the ids are stored within a map in the class Constants
     *
     * @param type
     * @param mainView
     * @return Recorder
     */
    public static IRecorder generate(int type, MainActivity mainView){
        switch (type) {

            case Constants.RECORDER_TEST_GOOGLE:
                TestGoogleRecorder recorder_test = new TestGoogleRecorder(mainView);
                if(!recorder_test.isInitialize()){

                    // if selected recorder cannot be initialized find the index of the
                    // text field recorder and select it
                    Log.e(MainActivity.LOG_TAG, "Could not initialize the native recorder with " +
                            "the selected streaming client.");
                    return null;
                }

                return recorder_test;

            case Constants.RECORDER_NATIVE_MIC:

                VoiceRecorder recorder = new VoiceRecorder(mainView);

                if(!recorder.isInitialize()){

                    // if selected recorder cannot be initialized find the index of the
                    // text field recorder and select it
                    Log.e(MainActivity.LOG_TAG, "Could not initialize the native recorder with " +
                            "the selected streaming client.");
                    return null;
                }

                return recorder;

            case Constants.RECORDER_ANDROID_VOICE_CLIENT:
                return new AndroidVoiceRecorder(mainView);

            default:
                return new TextRecorder(mainView);
        }
    }
}
