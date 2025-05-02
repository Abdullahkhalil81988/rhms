package com.rhms.healthDataHandling;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import com.rhms.userManagement.Patient;

/**
 * Manages and stores vital sign records for a specific patient
 * Provides functionality to add, retrieve, and display vital sign history
 */
public class VitalsDatabase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Patient associated with these vital records
    private Patient patient;
    // Collection to store patient's vital sign records
    private ArrayList<VitalSign> vitals;
    
    /**
     * Creates a new vitals database for a specific patient
     * @param patient The patient whose vitals will be tracked
     */
    public VitalsDatabase(Patient patient) {
        this.patient = patient;
        this.vitals = new ArrayList<>();
    }
    
    /**
     * Adds a new vital sign record to the patient's history
     * @param vitalSign The vital sign measurements to be recorded
     */
    public void addVitalRecord(VitalSign vitalSign) {
        if (vitals == null) {
            vitals = new ArrayList<>();
        }
        vitals.add(vitalSign);
    }
    
    /**
     * Retrieves all vital sign records for the patient
     * @return ArrayList containing all recorded vital signs
     */
    public ArrayList<VitalSign> getVitals() {
        if (vitals == null) {
            vitals = new ArrayList<>();
        }
        return vitals;
    }
    
    /**
     * Displays complete vital sign history for the patient
     * Shows a message if no vitals are recorded
     */
    public void displayAllVitals() {
        if (vitals == null || vitals.isEmpty()) {
            System.out.println("No vital records available.");
            return;
        }
        
        System.out.println("Vital History for Patient: " + patient.getName());
        int count = 1;
        for (VitalSign vital : vitals) {
            System.out.println("Record #" + count++);
            System.out.println("Timestamp: " + vital.getTimestamp());
            System.out.println("Heart Rate: " + vital.getHeartRate() + " bpm");
            System.out.println("Oxygen Level: " + vital.getOxygenLevel() + "%");
            System.out.println("Blood Pressure: " + vital.getBloodPressure() + " mmHg");
            System.out.println("Temperature: " + vital.getTemperature() + "°C");
            System.out.println("-----------------------------------------");
        }
    }
    
    /**
     * Gets the patient associated with this vitals database
     * @return Patient object
     */
    public Patient getPatient() {
        return patient;
    }
}
