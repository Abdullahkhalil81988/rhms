package com.rhms.emergencyAlert;

import com.rhms.healthDataHandling.VitalSign;
import com.rhms.userManagement.Patient;

public class EmergencyAlert {
    private static final double MAX_HEART_RATE = 120.0;
    private static final double MIN_HEART_RATE = 40.0;
    private static final double MIN_OXYGEN_LEVEL = 90.0;
    private static final double MAX_BLOOD_PRESSURE = 140.0;
    private static final double MAX_TEMPERATURE = 39.0;

    private NotificationService notificationService;

    public EmergencyAlert() {
        this.notificationService = new NotificationService();
    }

    public void checkVitals(Patient patient, VitalSign vitals) {
        StringBuilder alertMessage = new StringBuilder();
        boolean isEmergency = false;

        if (vitals.getHeartRate() > MAX_HEART_RATE || vitals.getHeartRate() < MIN_HEART_RATE) {
            alertMessage.append("CRITICAL: Abnormal heart rate detected: ").append(vitals.getHeartRate()).append(" bpm\n");
            isEmergency = true;
        }

        if (vitals.getOxygenLevel() < MIN_OXYGEN_LEVEL) {
            alertMessage.append("CRITICAL: Low oxygen level detected: ").append(vitals.getOxygenLevel()).append("%\n");
            isEmergency = true;
        }

        if (vitals.getBloodPressure() > MAX_BLOOD_PRESSURE) {
            alertMessage.append("CRITICAL: High blood pressure detected: ").append(vitals.getBloodPressure()).append(" mmHg\n");
            isEmergency = true;
        }

        if (vitals.getTemperature() > MAX_TEMPERATURE) {
            alertMessage.append("CRITICAL: High temperature detected: ").append(vitals.getTemperature()).append("°C\n");
            isEmergency = true;
        }

        if (isEmergency) {
            String alert = String.format("Emergency Alert for patient %s:\n%s", 
                patient.getName(), alertMessage.toString());
            notificationService.sendEmergencyAlert(alert, patient);
        }
    }
}