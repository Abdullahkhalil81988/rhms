package com.rhms.doctorPatientInteraction;

import java.io.Serializable;
import java.util.Date;

import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;

public class Prescription implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String medication;
    private String dosage;
    private String instructions;
    private Date issueDate;
    private Date expiryDate;
    private int doctorID;
    private int patientID;
    
    // Constructor for use with Doctor and Patient objects
    public Prescription(String medication, String dosage, String instructions, 
                       Date issueDate, Date expiryDate, Doctor doctor, Patient patient) {
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.doctorID = doctor.getUserID();
        this.patientID = patient.getUserID();
    }
    
    // Simple constructor for quick prescription creation (used in DoctorDashboardController)
    public Prescription(String medication, String dosage, String instructions) {
        this.medication = medication;
        this.dosage = dosage;
        this.instructions = instructions;
        this.issueDate = new Date();
        this.expiryDate = null;
        this.doctorID = 0;
        this.patientID = 0;
    }
    
    // Default constructor for serialization
    public Prescription() {
        this.medication = "";
        this.dosage = "";
        this.instructions = "";
        this.issueDate = new Date();
        this.expiryDate = null;
        this.doctorID = 0;
        this.patientID = 0;
    }
    
    // Getters
    public String getMedication() {
        return medication;
    }
    
    public String getMedicationName() {
        return medication;
    }
    
    public String getDosage() {
        return dosage;
    }
    
    public String getInstructions() {
        return instructions;
    }
    
    public String getFrequency() {
        return instructions;
    }
    
    public String getSchedule() {
        return instructions;
    }
    
    public Date getIssueDate() {
        return issueDate;
    }
    
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    public int getDoctorID() {
        return doctorID;
    }
    
    public int getPatientID() {
        return patientID;
    }
    
    // Setters
    public void setMedication(String medication) {
        this.medication = medication;
    }
    
    public void setMedicationName(String medication) {
        this.medication = medication;
    }
    
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    
    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    
    public void setSchedule(String instructions) {
        this.instructions = instructions;
    }
    
    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }
    
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
