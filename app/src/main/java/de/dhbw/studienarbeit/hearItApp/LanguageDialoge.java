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
                                setLanguage(Constants.LANGUAGE_GERMAN);
                                break;
                            case 1:
                                setLanguage(Constants.LANGUAGE_ENGLISH);
                                break;
                            case 2:
                                setLanguage(Constants.LANGUAGE_FRANCE);
                                break;
                            case 3:
                                setLanguage(Constants.LANGUAGE_SPAIN);
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
