package com.rhms.doctorPatientInteraction;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single chat message in the doctor-patient communication system
 */
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int senderId;        // User ID of message sender
    private int receiverId;      // User ID of message recipient
    private String content;      // Message content
    private Date timestamp;      // Time when message was sent
    private boolean isRead;      // Message read status
    
    public ChatMessage(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.timestamp = new Date();
        this.isRead = false;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public String getContent() {
        return content;
    }
    
    public Date getTimestamp() {
        return timestamp;
    }
    
    public boolean isRead() {
        return isRead;
    }
    
    public void markAsRead() {
        this.isRead = true;
    }
    
    @Override
    public String toString() {
        return String.format("[%tF %tT] %d: %s", timestamp, timestamp, senderId, content);
    }
}