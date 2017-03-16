package de.dhbw.studienarbeit.hearItApp.printer;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter.GlassUpPrinter;
import de.dhbw.studienarbeit.hearItApp.printer.textFieldPrinter.TextFieldPrinter;

/**
 * Factory class for AR device printers
 */

public class PrinterFactory {

    /**
     * method returns a new AR device printer depending on the given mode
     * @param printerMode
     * @param mainActivity
     * @return
     */
    public static AbstractPrinter generate(int printerMode, MainActivity mainActivity) {

        switch(printerMode){
            case Constants.PRINTER_GLASSUP_AR:
                return new GlassUpPrinter(mainActivity);
            default:
                return new TextFieldPrinter(mainActivity);
        }
    }
}
