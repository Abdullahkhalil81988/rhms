package com.rhms.doctorPatientInteraction;

public class Prescription {
    private String medicationName;
    private String dosage;
    private String schedule;

    public Prescription(String medicationName, String dosage, String schedule) {
        this.medicationName = medicationName;
        this.dosage = dosage;
        this.schedule = schedule;
    }

    public String getMedicationName() {
        return medicationName;
    }

    public String getDosage() {
        return dosage;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setMedicationName(String medicationName) {
        this.medicationName = medicationName;
    }

    public void setDosage(String dosage) {
        this.dosage = dosage;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public void displayPrescription() {
        System.out.println("Medication: " + medicationName);
        System.out.println("Dosage: " + dosage);
        System.out.println("Schedule: " + schedule);
    }
}
