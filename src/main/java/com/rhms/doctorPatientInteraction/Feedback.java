package com.rhms.doctorPatientInteraction;

import java.io.Serializable;
import java.util.Date;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;

public class Feedback implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Doctor doctor;
    private Patient patient;
    private String comments;
    private Date consultationDate;
    private Prescription prescription;
    
    public Feedback(Doctor doctor, Patient patient, String comments, Prescription prescription) {
        this.doctor = doctor;
        this.patient = patient;
        this.comments = comments;
        this.prescription = prescription;
        this.consultationDate = new Date(); // Current date/time
    }
    
    /**
     * Get the doctor who provided the feedback
     * @return The doctor
     */
    public Doctor getDoctor() {
        return doctor;
    }
    
    /**
     * Get the patient who received the feedback
     * @return The patient
     */
    public Patient getPatient() {
        return patient;
    }
    
    /**
     * Get the feedback comments
     * @return The comments text
     */
    public String getComments() {
        return comments;
    }
    
    /**
     * Get the date of the consultation
     * @return The consultation date
     */
    public Date getConsultationDate() {
        return consultationDate;
    }
    
    /**
     * Alias for getConsultationDate to match code in MedicalHistory
     * @return The consultation date
     */
    public Date getDate() {
        return getConsultationDate();
    }
    
    /**
     * Get the prescription provided during the consultation
     * @return The prescription
     */
    public Prescription getPrescription() {
        return prescription;
    }
}
