package de.dhbw.studienarbeit.hearItApp.recorder;

/**
 * Interface for Speech to Text Conversion Clients
 */

public interface ISpeechToTextConverter {

    /**
     * takes an array of recorded bytes and its size, converts the bytes to text
     * and calls the method receiveResult(String result) of the class MainActivity
     * to display the result
     *
     * @param buffer
     * @param size
     */
    void recognizeBytes(byte[] buffer, int size);

    /**
     * sets the initialization state of the converter
     *
     * @param streamInitialized
     */
    void setStreamInitialized(boolean streamInitialized);
}
