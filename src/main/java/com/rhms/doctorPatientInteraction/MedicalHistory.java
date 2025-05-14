package com.rhms.doctorPatientInteraction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import com.rhms.healthDataHandling.VitalsDatabase;

public class MedicalHistory implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ArrayList<MedicalRecord> records;
    private ArrayList<Feedback> feedbackList;
    private ArrayList<String> conditions;
    private ArrayList<String> allergies;
    private VitalsDatabase vitalsDatabase;
    
    public MedicalHistory() {
        this.records = new ArrayList<>();
        this.feedbackList = new ArrayList<>();
        this.conditions = new ArrayList<>();
        this.allergies = new ArrayList<>();
    }
    
    public void addRecord(String condition, String treatment, Date date) {
        records.add(new MedicalRecord(condition, treatment, date));
    }
    
    public ArrayList<MedicalRecord> getRecords() {
        return records;
    }
    
    public void addFeedback(Feedback feedback) {
        if (feedbackList == null) {
            feedbackList = new ArrayList<>();
        }
        feedbackList.add(feedback);
    }
    
    // Alias for addFeedback to match code in DoctorDashboardController
    public void addConsultation(Feedback feedback) {
        addFeedback(feedback);
    }
    
    public ArrayList<Feedback> getFeedbackList() {
        if (feedbackList == null) {
            feedbackList = new ArrayList<>();
        }
        return feedbackList;
    }
    
    // Alias for getFeedbackList to match code in DoctorDashboardController
    public ArrayList<Feedback> getPastConsultations() {
        return getFeedbackList();
    }
    
    /**
     * Gets the list of consultations (alias for getPastConsultations)
     * @return List of feedback records
     */
    public ArrayList<Feedback> getConsultations() {
        return getPastConsultations();
    }
    
    public void addCondition(String condition) {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        conditions.add(condition);
    }
    
    public ArrayList<String> getConditions() {
        if (conditions == null) {
            conditions = new ArrayList<>();
        }
        return conditions;
    }
    
    public void addAllergy(String allergy) {
        if (allergies == null) {
            allergies = new ArrayList<>();
        }
        allergies.add(allergy);
    }
    
    public ArrayList<String> getAllergies() {
        if (allergies == null) {
            allergies = new ArrayList<>();
        }
        return allergies;
    }
    
    // Inner class for medical records
    public static class MedicalRecord implements Serializable {
        private static final long serialVersionUID = 1L;
        
        private String condition;
        private String treatment;
        private Date date;
        
        public MedicalRecord(String condition, String treatment, Date date) {
            this.condition = condition;
            this.treatment = treatment;
            this.date = date;
        }
        
        public String getCondition() {
            return condition;
        }
        
        public String getTreatment() {
            return treatment;
        }
        
        public Date getDate() {
            return date;
        }
    }
    
    public void processConsultations(MedicalHistory medHistory) {
        if (medHistory.getConsultations() != null) {
            for (Feedback feedback : medHistory.getConsultations()) {
                // Add the feedback to this medical history
                this.addFeedback(feedback);
            }
        }
    }
    
    /**
     * Displays the full medical history to the console
     */
    public void displayMedicalHistory() {
        if (feedbackList == null || feedbackList.isEmpty()) {
            System.out.println("No medical history records available.");
            return;
        }
        
        int count = 1;
        for (Feedback feedback : feedbackList) {
            System.out.println("Record #" + count++);
            
            if (feedback.getDoctor() != null) {
                System.out.println("Doctor: Dr. " + feedback.getDoctor().getName());
            } else {
                System.out.println("Doctor: [Not specified]");
            }
            
            System.out.println("Date: " + feedback.getConsultationDate());
            System.out.println("Comments: " + feedback.getComments());
            
            Prescription prescription = feedback.getPrescription();
            if (prescription != null) {
                System.out.println("Medication: " + prescription.getMedication());
                System.out.println("Dosage: " + prescription.getDosage());
                System.out.println("Schedule: " + prescription.getSchedule());
            } else {
                System.out.println("No medications prescribed.");
            }
            System.out.println("-----------------------------------------");
        }
    }
    
    /**
     * Set the vitals database 
     */
    public void setVitalsDatabase(VitalsDatabase vitalsDatabase) {
        this.vitalsDatabase = vitalsDatabase;
    }
    
    /**
     * Get the vitals database
     */
    public VitalsDatabase getVitalsDatabase() {
        return vitalsDatabase;
    }
}
