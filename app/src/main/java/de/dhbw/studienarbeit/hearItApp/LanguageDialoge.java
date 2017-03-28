package de.dhbw.studienarbeit.hearItApp;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;

/**
 * Created by Andi on 28.03.2017.
 */

public class LanguageDialoge extends DialogFragment {

    MainActivity parent;

    public Dialog onCreateDialog(Bundle savedInstanceState, MainActivity parent) {

        this.parent = parent;
        AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle(R.string.pick_language)
                .setItems(R.array.language_options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch(which) {
                            case 0:
                                setLanguage(1); //German
                                break;
                            case 1:
                                setLanguage(2);  //English
                                break;
                            case 2:
                                setLanguage(3);  //France
                                break;
                            case 3:
                                setLanguage(4);  //Spain
                                break;
                            default: break;
                        }
                        // The 'which' argument contains the index position
                        // of the selected item
                    }
                });
        return builder.create();
    }

    public void setLanguage(int languageId){
        parent.setLanguageId(languageId);
    }
}
