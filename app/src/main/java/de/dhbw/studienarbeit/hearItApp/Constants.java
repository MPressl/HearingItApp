package de.dhbw.studienarbeit.hearItApp;

import java.util.LinkedHashMap;

/**
 * Global Project Constatnts
 */

public class Constants {

    public static final int RESULT_SPEECH = 1;

    /** Factory options for speech recognition **/
    public static final int RECORDER_NATIVE_MIC = 0;
    public static final int RECORDER_ANDROID_VOICE_CLIENT = 1;
    public static final int RECORDER_TEST_GOOGLE = 2;
    public static final int RECORDER_TEXT_FIELD_CLIENT = 999;

    public static final String RECORDER_NATIVE_MIC_TEXT = "Google Cloud Recorder";
    public static final String RECORDER_ANDROID_VOICE_CLIENT_TEXT = "Android Voice Recorder";
    public static final String RECORDER_TEXT_FIELD_CLIENT_TEXT = "Text Field Recorder";
    public static final String RECORDER_TEST_GOOGLE_TEXT = "Test Google Speech Streaming";


    public static final LinkedHashMap<String, Integer> RECORDER_MAP = createRecorderMap();

    /**
     * creates a map out of all recorders in this class ( nameString -> id )
     *
     * @return map of recorders
     */
    private static LinkedHashMap<String, Integer> createRecorderMap()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put(RECORDER_ANDROID_VOICE_CLIENT_TEXT, RECORDER_ANDROID_VOICE_CLIENT);
        map.put(RECORDER_NATIVE_MIC_TEXT, RECORDER_NATIVE_MIC);
        map.put(RECORDER_TEXT_FIELD_CLIENT_TEXT, RECORDER_TEXT_FIELD_CLIENT);
        map.put(RECORDER_TEST_GOOGLE_TEXT, RECORDER_TEST_GOOGLE);
        return map;
    }

    /** Factory options for AR printer variants **/
    public static final int PRINTER_GLASSUP_AR = 0;
    public static final int PRINTER_TEXT_FILED = 1;

    public static final String PRINTER_TEXT_FIELD_TEXT = "Print To Local Text Field";
    public static final String PRINTER_GLASSUP_AR_TEXT = "GlassUp AR Glass Printer";

    public static final LinkedHashMap<String, Integer> PRINTER_MAP = createPrinterMap();

    /**
     * creates a map out of all printers in this class ( nameString -> id )
     *
     * @return map of printers
     */
    private static LinkedHashMap<String, Integer> createPrinterMap()
    {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<String, Integer>();
        map.put(PRINTER_GLASSUP_AR_TEXT, PRINTER_GLASSUP_AR);
        map.put(PRINTER_TEXT_FIELD_TEXT, PRINTER_TEXT_FILED);
        return map;
    }

}
