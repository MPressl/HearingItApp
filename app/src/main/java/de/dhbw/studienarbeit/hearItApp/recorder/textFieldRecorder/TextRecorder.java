package de.dhbw.studienarbeit.hearItApp.recorder.textFieldRecorder;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.recorder.IRecorder;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * TextRecorder enables a EditText within the MainActivity and
 * takes the entered text as dummy input to display it on the
 * AR device using the selected AR-Printer
 */

public class TextRecorder implements IRecorder {
    MainActivity parent;

    EditText edit_recorder_input;

    TextView label_recorder_input;

    public TextRecorder(MainActivity parent){
        this.parent = parent;

        this.edit_recorder_input = (EditText) this.parent.findViewById(R.id.edit_recorder);
        this.edit_recorder_input.setVisibility(View.VISIBLE);

        this.label_recorder_input = (TextView) this.parent.findViewById(R.id.label_text_recorder);
        this.label_recorder_input.setVisibility(View.VISIBLE);
    }

    @Override
    public void startRecording() {
        this.parent.getSpeechBtn().setText("Printing Text...");
        String result = this.edit_recorder_input.getText().toString();
        this.parent.receiveResult(result);
    }

    @Override
    public void stopRecording() {}

    @Override
    public void shutdown() {
        this.edit_recorder_input.setVisibility(View.INVISIBLE);
        this.label_recorder_input.setVisibility(View.INVISIBLE);
    }
}
