package com.rhms.notifications;

public class SMSNotification implements Notifiable {
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        System.out.println("\n=== SMS Notification ===");
        System.out.println("To: " + recipient);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("======================\n");
    }
}