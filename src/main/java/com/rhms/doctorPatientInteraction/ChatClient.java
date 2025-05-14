package com.rhms.doctorPatientInteraction;

import java.util.ArrayList;
import java.util.List;

import com.rhms.userManagement.*;

/**
 * Handles real-time chat functionality between doctors and patients in the system
 */
public class ChatClient implements ChatMessageListener {
    // Reference to the user (doctor/patient) who owns this chat client
    private User user;
    // Connection to the central chat server that manages all messages
    private ChatServer chatServer;
    // Listeners for new messages
    private List<ChatMessageListener> messageListeners;
    
    /**
     * Creates a new chat client for a specific user
     */
    public ChatClient(User user) {
        this.user = user;
        this.chatServer = ChatServer.getInstance();
        this.messageListeners = new ArrayList<>();
        
        // Register for message notifications
        chatServer.addMessageListener(user.getUserID(), this);
    }
    
    /**
     * Routes a message from current user to specified recipient through server
     */
    public void sendMessage(int receiverId, String message) {
        chatServer.sendMessage(user.getUserID(), receiverId, message);
    }
    
    /**
     * Gets all messages exchanged with another user
     */
    public List<ChatMessage> getChatHistory(int otherUserId) {
        return chatServer.getChatHistory(user.getUserID(), otherUserId);
    }
    
    /**
     * Gets all unread messages from another user
     */
    public List<ChatMessage> getUnreadMessages(int otherUserId) {
        return chatServer.getUnreadMessages(user.getUserID(), otherUserId);
    }
    
    /**
     * Mark all messages from another user as read
     */
    public void markMessagesAsRead(int otherUserId) {
        chatServer.markMessagesAsRead(user.getUserID(), otherUserId);
    }
    
    /**
     * Add a listener for new messages
     */
    public void addMessageListener(ChatMessageListener listener) {
        messageListeners.add(listener);
    }
    
    /**
     * Remove a message listener
     */
    public void removeMessageListener(ChatMessageListener listener) {
        messageListeners.remove(listener);
    }
    
    /**
     * Called when a new message is received
     */
    @Override
    public void onNewMessage(ChatMessage message) {
        // Only process messages where this user is sender or receiver
        if (message.getSenderId() == user.getUserID() || 
            message.getReceiverId() == user.getUserID()) {
            
            // Notify all registered listeners
            for (ChatMessageListener listener : messageListeners) {
                listener.onNewMessage(message);
            }
        }
    }
    
    /**
     * Clean up resources when client is no longer needed
     */
    public void close() {
        chatServer.removeMessageListener(user.getUserID(), this);
    }
    
    /**
     * Display chat messages in the console (for testing)
     */
    public void displayChat(int otherUserId) {
        System.out.println("\nChat history with user " + otherUserId + ":");
        List<ChatMessage> messages = getChatHistory(otherUserId);
        
        for (ChatMessage message : messages) {
            String sender = message.getSenderId() == user.getUserID() ? "Me" : "Other";
            System.out.printf("[%tT] %s: %s\n", 
                message.getTimestamp(), 
                sender, 
                message.getContent());
        }
    }
}