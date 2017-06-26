package de.dhbw.studienarbeit.hearItApp.recorder;

import de.dhbw.studienarbeit.hearItApp.MainActivity;

/**
 * Interface for Recorders
 *
 *  Created by Martin Created by Martin Created by Martin Created by Martin Created by Martin Created by Martin
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

    MainActivity getMainView();

    boolean isRecording();
}
