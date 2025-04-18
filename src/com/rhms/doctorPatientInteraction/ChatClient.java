package com.rhms.doctorPatientInteraction;

import com.rhms.userManagement.*;

public class ChatClient {
    private User user;
    private ChatServer chatServer;
    
    public ChatClient(User user, ChatServer chatServer) {
        this.user = user;
        this.chatServer = chatServer;
    }
    
    public void sendMessage(String receiverName, String message) {
        chatServer.sendMessage(user.getName(), receiverName, message);
    }
    
    public void displayChat(String otherUser) {
        System.out.println("\nChat history with " + otherUser + ":");
        for (String message : chatServer.getChatHistory(user.getName(), otherUser)) {
            System.out.println(message);
        }
    }
}