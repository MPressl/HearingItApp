package de.dhbw.studienarbeit.hearItApp.recorder.textFieldRecorder;

import android.view.View;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by mpressl on 2/28/2017.
 */

public class TextRecorder implements IRecorder {
    MainActivity parent;

    public TextRecorder(MainActivity parent){
        this.parent = parent;
    }

    @Override
    public void startRecording() {
        this.parent.getSpeechBtn().setText("Recording.. enter in textfield");
        this.parent.getTextField().setVisibility(View.VISIBLE);

    }

    @Override
    public void stopRecording() {
        this.parent.getSpeechBtn().setText("Start Speech recording");
        this.parent.receiveResult((ArrayList<String>) Arrays.asList(
        new String[] { this.parent.getTextField().getText().toString()}));
    }
}
