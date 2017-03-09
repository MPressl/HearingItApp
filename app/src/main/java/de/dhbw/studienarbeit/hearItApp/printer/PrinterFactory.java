package de.dhbw.studienarbeit.hearItApp.printer;

import de.dhbw.studienarbeit.hearItApp.Constants;
import de.dhbw.studienarbeit.hearItApp.MainActivity;
import de.dhbw.studienarbeit.hearItApp.printer.glassUpARPrinter.GlassUpPrinter;

/**
 * Created by mpressl on 3/3/2017.
 */

public class PrinterFactory {


    public static IPrinter generate(int printerMode, MainActivity mainActivity) {

        switch(printerMode){
            case Constants.PRINTER_GLASSUP_AR:
                return new GlassUpPrinter(mainActivity);
            default:
                return null;
        }
    }
}
