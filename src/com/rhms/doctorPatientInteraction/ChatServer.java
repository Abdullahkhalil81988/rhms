package com.rhms.doctorPatientInteraction;

import com.rhms.userManagement.*;
import java.util.*;

public class ChatServer {
    private Map<String, List<String>> chatHistory;
    
    public ChatServer() {
        this.chatHistory = new HashMap<>();
    }
    
    public void sendMessage(String sender, String receiver, String message) {
        String chatId = getChatId(sender, receiver);
        chatHistory.computeIfAbsent(chatId, k -> new ArrayList<>())
                  .add(String.format("[%s] %s: %s", 
                        new Date(), sender, message));
    }
    
    public List<String> getChatHistory(String user1, String user2) {
        return chatHistory.getOrDefault(getChatId(user1, user2), 
                                      new ArrayList<>());
    }
    
    private String getChatId(String user1, String user2) {
        return user1.compareTo(user2) < 0 
               ? user1 + "_" + user2 
               : user2 + "_" + user1;
    }
}