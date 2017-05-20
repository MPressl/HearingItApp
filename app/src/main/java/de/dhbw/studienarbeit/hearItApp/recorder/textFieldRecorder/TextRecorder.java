package de.dhbw.studienarbeit.hearItApp.recorder.textFieldRecorder;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
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
    private Context context;

    EditText edit_recorder_input;

    TextView label_recorder_input;

    public TextRecorder(MainActivity parent, Context context){
        this.parent = parent;
        this.context = context;

        this.edit_recorder_input = (EditText) this.parent.findViewById(R.id.edit_recorder);
        this.edit_recorder_input.setVisibility(View.VISIBLE);

        ViewGroup.LayoutParams params = edit_recorder_input.getLayoutParams();
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        edit_recorder_input.setLayoutParams(params);

        this.label_recorder_input = (TextView) this.parent.findViewById(R.id.label_text_recorder);
        this.label_recorder_input.setVisibility(View.VISIBLE);
    }

    @Override
    public void startRecording() {
        //this.parent.getSpeechBtn().setText("Printing Text...");
        String result = this.edit_recorder_input.getText().toString();
        this.parent.receiveResult(result);

        this.parent.setRecordingModeStyle();
    }

    @Override
    public void stopRecording() {
        this.parent.setNotRecordingModeStyle();

    }

    @Override
    public void shutdown() {
        this.edit_recorder_input.setVisibility(View.INVISIBLE);


        ViewGroup.LayoutParams params = edit_recorder_input.getLayoutParams();
        params.height = 0;
        edit_recorder_input.setLayoutParams(params);

        this.label_recorder_input.setVisibility(View.INVISIBLE);
    }

    @Override
    public MainActivity getMainView() {
        return this.parent;
    }

    @Override
    public boolean isRecording() {
        return false;
    }
}
