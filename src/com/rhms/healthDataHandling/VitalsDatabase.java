package com.rhms.healthDataHandling;

import com.rhms.userManagement.Patient;
import java.util.ArrayList;

public class VitalsDatabase {
    private ArrayList<VitalSign> vitals;
    private Patient patient;

    public VitalsDatabase(Patient patient) {
        this.patient = patient;
        this.vitals = new ArrayList<>();
    }

    public void addVitalRecord(VitalSign vitalSign) {
        vitals.add(vitalSign);
        System.out.println("Vital signs recorded successfully for " + patient.getName());
    }

    public ArrayList<VitalSign> getVitals() {
        return vitals;
    }

    public void displayAllVitals() {
        System.out.println("Vital History for Patient: " + patient.getName());
        if (vitals.isEmpty()) {
            System.out.println("No vitals recorded.");
        } else {
            for (VitalSign vital : vitals) {
                vital.displayVitals();
                System.out.println("----------------------");
            }
        }
    }
}
