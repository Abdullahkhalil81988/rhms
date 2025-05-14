package com.rhms.doctorPatientInteraction;

/**
 * Interface for listening to new chat messages
 */
public interface ChatMessageListener {
    /**
     * Called when a new message is received
     * @param message The new message
     */
    void onNewMessage(ChatMessage message);
}