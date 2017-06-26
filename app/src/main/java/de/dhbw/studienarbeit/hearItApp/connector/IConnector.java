package de.dhbw.studienarbeit.hearItApp.connector;

/**
 * Interface for a AR Printer
 * to implement a new printer use AbstractConnector instead of this interface itself
 *
 *  Created by Martin
 */

public interface IConnector {

    /**
     * method adds a String to the queue of messages to be printer
     * @param message
     */
    void addToMessageBuffer(String message);

    /**
     * method strts a thread which waits until a new message appears in the queue
     * and prints it
     */
    void startPrinting();

    void stopPrinting();

    /**
     * method closes the connection to the AR device
     */
    void shutdown();
}
