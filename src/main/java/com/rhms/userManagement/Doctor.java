package com.rhms.userManagement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Doctor extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String specialization;
    private int yearsOfExperience;
    private ArrayList<Patient> patients;
    private ArrayList<Map<String, Object>> feedbackReceived;
    
    public Doctor(String name, String email, String password, String phone, String address, 
                 int userID, String specialization, int yearsOfExperience) {
        super(name, email, password, phone, address, userID);
        this.specialization = specialization;
        this.yearsOfExperience = yearsOfExperience;
        this.patients = new ArrayList<>();
        this.feedbackReceived = new ArrayList<>();
    }
    
    public String getSpecialization() {
        return specialization;
    }
    
    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
    
    public ArrayList<Patient> getPatients() {
        return patients;
    }
    
    public void addPatient(Patient patient) {
        if (!patients.contains(patient)) {
            patients.add(patient);
        }
    }
    
    public void removePatient(Patient patient) {
        patients.remove(patient);
    }
    
    /**
     * Records feedback provided by a patient to this doctor
     * @param patient The patient providing the feedback
     * @param feedback The feedback text content
     */
    public void provideFeedback(Patient patient, String feedback) {
        // This method is called when a patient provides feedback about a doctor
        
        // We can record this in a log or database
        System.out.println("Feedback received from patient: " + patient.getName());
        
        // If we want to maintain a collection of feedback in the Doctor class:
        if (this.feedbackReceived == null) {
            this.feedbackReceived = new ArrayList<>();
        }
        
        // Create a map or object to store feedback details
        Map<String, Object> feedbackEntry = new HashMap<>();
        feedbackEntry.put("patientId", patient.getUserID());
        feedbackEntry.put("patientName", patient.getName());
        feedbackEntry.put("feedback", feedback);
        feedbackEntry.put("date", new Date());
        
        // Add to doctor's feedback collection
        this.feedbackReceived.add(feedbackEntry);
        
        // Note: The feedback is already being stored in the patient's medical history
        // in the calling code, so this is primarily for the doctor's records
    }
}
