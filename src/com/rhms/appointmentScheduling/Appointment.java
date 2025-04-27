package com.rhms.appointmentScheduling;

//importing packages
import com.rhms.userManagement.Patient;
import com.rhms.userManagement.Doctor;
import java.util.Date;

public class Appointment {
    private Date appointmentDate;
    private Doctor doctor;
    private Patient patient;
    private String status; // "Pending", "Approved", "Cancelled"
    private String notes;
    private int appointmentID;
    
    // Static counter for generating appointment IDs
    private static int nextAppointmentID = 1000;
    
    /**
     * Constructor for creating a new appointment
     * @param appointmentDate Date and time of the appointment
     * @param doctor Doctor assigned to this appointment
     * @param patient Patient for this appointment
     */
    public Appointment(Date appointmentDate, Doctor doctor, Patient patient) {
        this.appointmentDate = appointmentDate;
        this.doctor = doctor;
        this.patient = patient;
        this.status = "Pending"; // Default status is pending
        this.notes = "";
        this.appointmentID = nextAppointmentID++;
    }
    
    /**
     * Constructor with all fields
     * @param appointmentDate Date and time of the appointment
     * @param doctor Doctor assigned to this appointment
     * @param patient Patient for this appointment
     * @param status Current status of the appointment
     * @param notes Additional notes for the appointment
     * @param appointmentID Unique identifier for the appointment
     */
    public Appointment(Date appointmentDate, Doctor doctor, Patient patient, 
                       String status, String notes, int appointmentID) {
        this.appointmentDate = appointmentDate;
        this.doctor = doctor;
        this.patient = patient;
        this.status = status;
        this.notes = notes;
        this.appointmentID = appointmentID;
    }
    
    // Getter for appointment date
    public Date getAppointmentDate() {
        return appointmentDate;
    }
    
    // Setter for appointment date
    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }
    
    // Getter for doctor
    public Doctor getDoctor() {
        return doctor;
    }
    
    // Setter for doctor
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    // Getter for patient
    public Patient getPatient() {
        return patient;
    }
    
    // Setter for patient
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    // Getter for status
    public String getStatus() {
        return status;
    }
    
    // Setter for status
    public void setStatus(String status) {
        this.status = status;
    }
    
    // Getter for notes
    public String getNotes() {
        return notes;
    }
    
    // Setter for notes
    public void setNotes(String notes) {
        this.notes = notes;
    }
    
    // Getter for appointmentID
    public int getAppointmentID() {
        return appointmentID;
    }
    
    // Override toString method for better display
    @Override
    public String toString() {
        return "Appointment #" + appointmentID + 
               " - Date: " + appointmentDate + 
               ", Doctor: Dr. " + doctor.getName() + 
               ", Patient: " + patient.getName() + 
               ", Status: " + status;
    }
}
