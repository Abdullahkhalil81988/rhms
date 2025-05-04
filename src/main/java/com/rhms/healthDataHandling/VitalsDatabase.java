package com.rhms.healthDataHandling;

import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import com.rhms.userManagement.Patient;

/**
 * Manages and stores vital sign records for a specific patient
 * Provides functionality to add, retrieve, and display vital sign history
 */
public class VitalsDatabase implements Serializable {
    private static final long serialVersionUID = 1L;
    
    // Ensure patient reference doesn't cause serialization issues
    private transient Patient patient; // Make patient transient
    private int patientId; // Add patientId to maintain relationship
    
    // Collection to store patient's vital sign records
    private ArrayList<VitalSign> vitals;
    // Timestamp for the last update
    private Date lastUpdated;
    
    /**
     * Creates a new vitals database for a specific patient
     * @param patient The patient whose vitals will be tracked
     */
    public VitalsDatabase(Patient patient) {
        this.patient = patient;
        this.patientId = patient.getUserID(); // Store the ID separately
        this.vitals = new ArrayList<>();
        this.lastUpdated = null;
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
        lastUpdated = new Date(); // Update timestamp
        
        System.out.println("Added vital to database: " + 
                          vitalSign.getHeartRate() + ", " + 
                          vitalSign.getOxygenLevel() + ", " + 
                          vitalSign.getBloodPressure() + ", " + 
                          vitalSign.getTemperature());
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
    
    /**
     * Sets the patient associated with this vitals database
     * @param patient The patient to be associated
     */
    public void setPatient(Patient patient) {
        this.patient = patient;
        this.patientId = patient.getUserID();
    }
    
    // Ensure proper serialization 
    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        // Write patientId instead of patient reference
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        // Patient reference will be restored by Patient.readObject
    }
}
