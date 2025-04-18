package com.rhms.emergencyAlert;

import com.rhms.userManagement.Patient;
import java.time.LocalDateTime;

public class PanicButton {
    private NotificationService notificationService;
    private Patient patient;
    private boolean isActive;
    private LocalDateTime lastTriggered;

    public PanicButton(Patient patient) {
        this.patient = patient;
        this.notificationService = new NotificationService();
        this.isActive = true;
        this.lastTriggered = null;
    }

    public void triggerAlert(String reason) {
        if (!isActive) {
            System.out.println("Panic button is currently disabled.");
            return;
        }

        lastTriggered = LocalDateTime.now();
        String alertMessage = String.format(
            "EMERGENCY: Manual alert triggered by patient %s\n" +
            "Reason: %s\n" +
            "Time: %s\n" +
            "Location: %s",
            patient.getName(),
            reason,
            lastTriggered.toString(),
            patient.getAddress()
        );

        notificationService.sendEmergencyAlert(alertMessage, patient);
    }

    public void enable() {
        if (isActive) {
            System.out.println("Panic button is already enabled for patient: " + patient.getName());
            return;
        }
        isActive = true;
        System.out.println("Panic button enabled for patient: " + patient.getName());
    }

    public void disable() {
        if (!isActive) {
            System.out.println("Panic button is already disabled for patient: " + patient.getName());
            return;
        }
        isActive = false;
        System.out.println("Panic button disabled for patient: " + patient.getName());
    }

    public LocalDateTime getLastTriggered() {
        return lastTriggered;
    }

    public boolean isActive() {
        return isActive;
    }

    public String getStatus() {
        return isActive ? "enabled" : "disabled";
    }
}