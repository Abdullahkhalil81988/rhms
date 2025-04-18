package com.rhms.userManagement;

import java.util.ArrayList;

public class Doctor extends User {
    private String specialization;
    private int experienceYears;
    private ArrayList<Patient> assignedPatients;

    public Doctor(String name, String email, String password, String phone, String address, int userID, String specialization, int experienceYears) {
        super(name, email, password, phone, address, userID);
        this.specialization = specialization;
        this.experienceYears = experienceYears;
        this.assignedPatients = new ArrayList<>();
    }

    public void addPatient(Patient patient) {
        assignedPatients.add(patient);
    }       //add patient method

    public void provideFeedback(Patient patient, String feedback) {
        patient.getDoctorFeedback().add(feedback);
    }   //feedback method

    public void manageAppointment(String appointmentDetails) {
        System.out.println("Managing appointment: " + appointmentDetails);
    }
}
