package de.dhbw.studienarbeit.hearItApp.printer.textFieldPrinter;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.printer.IPrinter;

/**
 * Created by mpressl on 3/13/2017.
 */

public class TextFieldPrinter implements IPrinter {

    private MainActivity mainView;

    private EditText printerField;

    private TextView label;

    public TextFieldPrinter(MainActivity mainView){
        this.mainView = mainView;
        this.printerField = (EditText) mainView.findViewById(R.id.edit_printer);
        this.label = (TextView) mainView.findViewById(R.id.label_text_printer);
        this.printerField.setVisibility(View.VISIBLE);
        this.label.setVisibility(View.VISIBLE);
    }

    @Override
    public void printMessage(String message) {
        String old = this.printerField.getText().toString();
        final String newMessage = old + message;
        this.mainView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printerField.setText(newMessage);
            }
        });
    }

    @Override
    public void shutdown() {

        this.printerField.setVisibility(View.INVISIBLE);
        this.label.setVisibility(View.INVISIBLE);
    }
}
