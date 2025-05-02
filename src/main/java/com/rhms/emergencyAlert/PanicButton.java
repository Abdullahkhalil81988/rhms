package com.rhms.emergencyAlert;

import java.io.Serializable;
import java.util.Date;

import com.rhms.userManagement.Patient;

/**
 * Manages emergency panic button functionality for patients
 */
public class PanicButton implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private boolean active;
    private Date activationTime;
    private Date lastTriggered;
    private transient NotificationService notificationService;
    private transient Patient patient;
    
    public PanicButton() {
        this.active = false;
        this.activationTime = null;
        this.lastTriggered = null;
        this.notificationService = new NotificationService();
    }
    
    public PanicButton(Patient patient) {
        this();
        this.patient = patient;
    }
    
    // Method called from PatientDashboardController
    public void enable() {
        active = true;
        activationTime = new Date();
        System.out.println("Panic button enabled for patient");
    }
    
    // Method called from PatientDashboardController
    public void disable() {
        active = false;
        activationTime = null;
        System.out.println("Panic button disabled for patient");
    }
    
    public void triggerAlert(String reason) {
        if (!active) {
            System.out.println("Cannot trigger alert: Panic button is not enabled");
            return;
        }
        
        lastTriggered = new Date();
        String alertMessage = "EMERGENCY ALERT: " + reason;
        
        if (notificationService == null) {
            notificationService = new NotificationService();
        }
        
        // Simple console notification for demo purposes
        System.out.println("\n===== EMERGENCY ALERT =====");
        System.out.println("Time: " + lastTriggered);
        System.out.println("Alert: " + alertMessage);
        System.out.println("===========================\n");
        
        if (patient != null) {
            notificationService.sendEmergencyAlert(alertMessage, patient);
        }
    }
    
    public boolean isActive() {
        return active;
    }
    
    public Date getActivationTime() {
        return activationTime;
    }
    
    public Date getLastTriggered() {
        return lastTriggered;
    }
}