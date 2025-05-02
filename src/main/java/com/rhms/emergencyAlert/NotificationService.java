package com.rhms.emergencyAlert;

import java.io.Serializable;
import com.rhms.notifications.SMSNotification;
import com.rhms.notifications.EmailNotification;
import com.rhms.userManagement.Patient;
import com.rhms.userManagement.Doctor;
import com.rhms.gui.SessionManager;

/**
 * Service for sending emergency notifications
 */
public class NotificationService implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private transient SMSNotification smsService;
    private transient EmailNotification emailService;
    
    public NotificationService() {
        this.smsService = new SMSNotification();
        this.emailService = new EmailNotification();
    }
    
    /**
     * Sends emergency alert to all relevant parties
     */
    public void sendEmergencyAlert(String alertMessage, Patient patient) {
        // Ensure services are initialized
        if (smsService == null) {
            smsService = new SMSNotification();
        }
        
        if (emailService == null) {
            emailService = new EmailNotification();
        }
        
        // Send SMS to patient
        smsService.sendNotification(
            patient.getPhone(),
            "EMERGENCY ALERT",
            alertMessage
        );
        
        // Notify all doctors (in a real system, would only notify assigned doctors)
        for (Doctor doctor : SessionManager.getDoctors()) {
            emailService.sendNotification(
                doctor.getEmail(),
                "EMERGENCY ALERT for " + patient.getName(),
                alertMessage + "\nPatient: " + patient.getName() + 
                "\nPhone: " + patient.getPhone() +
                "\nLocation: " + patient.getAddress()
            );
            
            // Send SMS for immediate attention
            smsService.sendNotification(
                doctor.getPhone(),
                "EMERGENCY",
                "Patient " + patient.getName() + " needs immediate attention"
            );
        }
        
        // Log the emergency
        System.out.println("Emergency alert sent for patient: " + patient.getName());
        System.out.println("Alert message: " + alertMessage);
    }
}