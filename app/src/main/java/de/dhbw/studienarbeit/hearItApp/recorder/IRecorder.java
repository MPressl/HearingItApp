package de.dhbw.studienarbeit.hearItApp.recorder;

/**
 * Interface for Recorders
 */

public interface IRecorder {

    /**
     * method starts the recording of audio data in an own thread
     */
    void startRecording();

    /**
     * stops the audio record
     */
    void stopRecording();

    /**
     * stops the audio record if still running and frees all resources
     */
    void shutdown();
}
