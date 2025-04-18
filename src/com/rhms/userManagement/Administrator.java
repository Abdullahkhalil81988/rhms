package com.rhms.userManagement;

import java.util.ArrayList;

public class Administrator extends User {
    private ArrayList<Doctor> doctors;
    private ArrayList<Patient> patients;

    public Administrator(String name, String email, String password, String phone, String address, int userID) {
        super(name, email, password, phone, address, userID);
        this.doctors = new ArrayList<>();
        this.patients = new ArrayList<>();
    }

    public void addDoctor(Doctor doctor) {
        doctors.add(doctor);
    }

    public void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
    }

    public void viewSystemLogs() {
        System.out.println("Displaying system logs...");
    }
}
