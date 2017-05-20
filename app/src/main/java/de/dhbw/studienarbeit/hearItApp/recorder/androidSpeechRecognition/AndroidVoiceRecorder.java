package de.dhbw.studienarbeit.hearItApp.recorder.androidSpeechRecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * AndroidVoiceRecorder using the Android own Speech Recognition service
 * the service works asynchronus. An Intent is called, the speech is recorded
 * converted into text and the result is received by the MainActivity
 */

public class AndroidVoiceRecorder implements IRecorder {

    private MainActivity parent;

    public AndroidVoiceRecorder(MainActivity context){
        this.parent = context;
    }

    /**
     * creates the Android recognition intent
     */
    @Override
    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "de-DE");
        try {
            this.parent.startActivityForResult(intent,Constants.RESULT_SPEECH);
            this.parent.setRecordingModeStyle();
        } catch (ActivityNotFoundException a) {
            this.parent.showToast("Opps! Your device doesn't support Speech to Text");
        }
    }

    @Override
    public void stopRecording() {
        this.parent.setNotRecordingModeStyle();
        return;
    }

    @Override
    public void shutdown() {
        return;
    }

    @Override
    public MainActivity getMainView() {
        return parent;
    }

    @Override
    public boolean isRecording() {
        return false;
    }

}
