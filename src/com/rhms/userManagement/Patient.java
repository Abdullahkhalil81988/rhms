package com.rhms.userManagement;

import java.util.ArrayList;
import com.rhms.emergencyAlert.PanicButton; // Make sure this import exists

public class Patient extends User {
    private ArrayList<String> medicalRecords;
    private ArrayList<String> doctorFeedback;
    private ArrayList<String> appointments;
    private PanicButton panicButton;

    public Patient(String name, String email, String password, String phone, String address, int userID) {
        super(name, email, password, phone, address, userID);
        this.medicalRecords = new ArrayList<>();
        this.doctorFeedback = new ArrayList<>();
        this.appointments = new ArrayList<>();
        this.panicButton = new PanicButton(this); // Initialize panic button
    }

    public void uploadMedicalRecord(String record) {        
        medicalRecords.add(record);
    }

    public ArrayList<String> getDoctorFeedback(){       //returns doctorFeedback list for use in Doctor class
        return doctorFeedback;
    }

    public void viewDoctorFeedback() {
        for (String feedback : doctorFeedback) {        //prints doctorFeedback(s)
            System.out.println(feedback);
        }
    }

    public void scheduleAppointment(String appointment) {
        appointments.add(appointment);
    }

    public void triggerPanicButton(String reason) {
        panicButton.triggerAlert(reason);
    }

    public void enablePanicButton() {
        panicButton.enable();
    }

    public void disablePanicButton() {
        panicButton.disable();
    }

    public PanicButton getPanicButton() {
        return panicButton;
    }
}

