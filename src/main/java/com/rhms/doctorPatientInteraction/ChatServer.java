package com.rhms.doctorPatientInteraction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.io.Serializable;
import com.rhms.userManagement.*;

/**
 * Central server that manages all chat messages between doctors and patients
 */
public class ChatServer implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Stores chat messages using a unique chat ID for each conversation pair
    private Map<String, List<ChatMessage>> chatHistory;
    
    // Observer pattern - store listeners for new messages by user ID
    // Note: transient means this won't be serialized
    private transient Map<Integer, List<ChatMessageListener>> messageListeners;
    
    // Singleton instance
    private static ChatServer instance;
    
    // Get singleton instance
    public static synchronized ChatServer getInstance() {
        if (instance == null) {
            instance = new ChatServer();
        }
        return instance;
    }
    
    // Private constructor for singleton pattern
    private ChatServer() {
        this.chatHistory = new ConcurrentHashMap<>();
        this.messageListeners = new ConcurrentHashMap<>();
    }
    
    /**
     * Sends a message from one user to another
     */
    public void sendMessage(int senderId, int receiverId, String message) {
        ChatMessage chatMessage = new ChatMessage(senderId, receiverId, message);
        String chatId = getChatId(senderId, receiverId);
        
        // Add message to history
        chatHistory.computeIfAbsent(chatId, k -> Collections.synchronizedList(new ArrayList<>()))
                  .add(chatMessage);
        
        // Notify listeners for both sender and receiver
        notifyMessageListeners(senderId, chatMessage);
        notifyMessageListeners(receiverId, chatMessage);
    }
    
    /**
     * Retrieves all messages exchanged between two users
     */
    public List<ChatMessage> getChatHistory(int user1Id, int user2Id) {
        return new ArrayList<>(chatHistory.getOrDefault(getChatId(user1Id, user2Id), 
                                      new ArrayList<>()));
    }
    
    /**
     * Retrieves unread messages for a specific user from another user
     */
    public List<ChatMessage> getUnreadMessages(int userId, int otherId) {
        List<ChatMessage> allMessages = getChatHistory(userId, otherId);
        List<ChatMessage> unread = new ArrayList<>();
        
        for (ChatMessage message : allMessages) {
            if (message.getReceiverId() == userId && !message.isRead()) {
                unread.add(message);
            }
        }
        
        return unread;
    }
    
    /**
     * Mark all messages from sender to receiver as read
     */
    public void markMessagesAsRead(int receiverId, int senderId) {
        List<ChatMessage> messages = getChatHistory(receiverId, senderId);
        
        for (ChatMessage message : messages) {
            if (message.getReceiverId() == receiverId && !message.isRead()) {
                message.markAsRead();
            }
        }
    }
    
    /**
     * Register a listener for new messages
     */
    public void addMessageListener(int userId, ChatMessageListener listener) {
        // Initialize messageListeners if it's null (after deserialization)
        if (messageListeners == null) {
            messageListeners = new ConcurrentHashMap<>();
        }
        
        messageListeners.computeIfAbsent(userId, k -> new ArrayList<>()).add(listener);
    }
    
    /**
     * Remove a message listener
     */
    public void removeMessageListener(int userId, ChatMessageListener listener) {
        if (messageListeners != null && messageListeners.containsKey(userId)) {
            messageListeners.get(userId).remove(listener);
        }
    }
    
    /**
     * Notify all listeners for a user about a new message
     */
    private void notifyMessageListeners(int userId, ChatMessage message) {
        if (messageListeners != null && messageListeners.containsKey(userId)) {
            for (ChatMessageListener listener : messageListeners.get(userId)) {
                listener.onNewMessage(message);
            }
        }
    }
    
    /**
     * Creates unique identifier for chat between two users, order-independent
     */
    private String getChatId(int user1Id, int user2Id) {
        // Ensures same ID regardless of parameter order
        return user1Id < user2Id 
               ? user1Id + "_" + user2Id 
               : user2Id + "_" + user1Id;
    }
    
    /**
     * Called when object is read from serialized form
     * Initializes transient fields
     */
    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
        messageListeners = new ConcurrentHashMap<>();
    }
}