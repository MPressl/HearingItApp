package de.dhbw.studienarbeit.hearItApp.recorder.androidSpeechRecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * AndroidVoiceRecorder using the Android own Speech Recognition service
 * the service works asynchronus. An Intent is called, the speech is recorded
 * converted into text and the result is received by the MainActivity
 */

public class AndroidVoiceRecorder implements IRecorder {

    private MainActivity context;

    public AndroidVoiceRecorder(MainActivity context){
        this.context = context;
    }

    /**
     * creates the Android recognition intent
     */
    @Override
    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "de-DE");
        try {
            this.context.startActivityForResult(intent,Constants.RESULT_SPEECH);
        } catch (ActivityNotFoundException a) {
            this.context.showToast("Opps! Your device doesn't support Speech to Text");
        }
    }

    @Override
    public void stopRecording() {
        return;
    }

    @Override
    public void shutdown() {
        return;
    }

}
