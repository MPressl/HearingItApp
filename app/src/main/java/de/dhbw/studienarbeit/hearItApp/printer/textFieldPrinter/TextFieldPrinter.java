package de.dhbw.studienarbeit.hearItApp.printer.textFieldPrinter;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.R;
import de.dhbw.studienarbeit.hearItApp.printer.AbstractPrinter;

/**
 * TextFieldPrinter, prints conversion result from Speech to Text Recorder
 * to a sinmple TextField within the MainActivity
 */

public class TextFieldPrinter extends AbstractPrinter {

    private MainActivity mainView;

    private EditText printerField;

    private TextView label;

    public TextFieldPrinter(MainActivity mainView){
        super();
        this.mainView = mainView;
        this.printerField = (EditText) mainView.findViewById(R.id.edit_printer);
        this.label = (TextView) mainView.findViewById(R.id.label_text_printer);
        this.printerField.setVisibility(View.VISIBLE);
        this.label.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean printMessage(String message) {
        String old = this.printerField.getText().toString();
        final String newMessage = old + message;
        this.mainView.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                printerField.setText(newMessage);
            }
        });
        return true;
    }


    @Override
    public void shutdown() {

        this.printerField.setVisibility(View.INVISIBLE);
        this.label.setVisibility(View.INVISIBLE);
    }
}
