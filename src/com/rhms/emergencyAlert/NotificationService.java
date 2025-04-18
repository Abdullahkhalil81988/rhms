package com.rhms.emergencyAlert;

import com.rhms.userManagement.Patient;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationService {
    private static final String EMAIL_SERVER = "smtp.hospital.com";
    private static final String SMS_GATEWAY = "sms.hospital.com";

    public void sendEmergencyAlert(String message, Patient patient) {
        sendEmail(message, patient);
        sendSMS(message, patient);
        logAlert(message);
    }

    private void sendEmail(String message, Patient patient) {
        // Simulated email sending
        System.out.println("Sending email alert to: " + patient.getEmail());
        System.out.println("Message: " + message);
    }

    private void sendSMS(String message, Patient patient) {
        // Simulated SMS sending
        System.out.println("Sending SMS alert to: " + patient.getName());
        System.out.println("Message: " + message);
    }

    private void logAlert(String message) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Alert logged at " + now.format(formatter));
        System.out.println("Alert details: " + message);
    }
}