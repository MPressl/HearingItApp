package de.dhbw.studienarbeit.hearItApp.recorder.androidSpeechRecognition;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.support.v7.app.AppCompatActivity;

import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;
import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * Created by root on 12/14/16.
 */

public class AndroidVoiceRecorder extends AppCompatActivity implements IRecorder {

    private MainActivity parent;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        super.onActivityResult(requestCode, resultCode, data);

        switch(requestCode){

            case MainActivity.RESULT_SPEECH:

                if(resultCode == RESULT_OK && data != null){
                    parent.receiveResult(data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS));

                }else{
                    parent.showToast("couldn't parse speech to text");
                }
                break;
            default:
                parent.showToast("didn't expect that intent result");
                break;
        }
    }

    public void startRecording() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "de");
        try {
            startActivityForResult(intent,MainActivity.RESULT_SPEECH);
            parent.getTxt().setText("");
        } catch (ActivityNotFoundException a) {
            parent.showToast("Opps! Your device doesn't support Speech to Text");
        }
    }

    @Override
    public void stopRecording() {
        return;
    }

}
