package com.rhms.notifications;

public interface Notifiable {
    void sendNotification(String recipient, String subject, String message);
}