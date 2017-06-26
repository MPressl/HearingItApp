package de.dhbw.studienarbeit.hearItApp.connector;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.connector.glassUpARConnector.GlassUpConnector;
import de.dhbw.studienarbeit.hearItApp.connector.textFieldConnector.TextFieldConnector;

/**
 * Factory class for AR device printers
 *
 *  Created by Martin
 */

public class ConnectorFactory {

    /**
     * method returns a new AR device printer depending on the given mode
     * @param printerMode
     * @param mainActivity
     * @return
     */
    public static AbstractConnector generate(int printerMode, MainActivity mainActivity) {

        switch(printerMode){
            case Constants.PRINTER_GLASSUP_AR:
                return new GlassUpConnector(mainActivity);
            default:
                return new TextFieldConnector(mainActivity);
        }
    }
}
