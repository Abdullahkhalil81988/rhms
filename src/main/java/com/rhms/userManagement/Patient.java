package com.rhms.userManagement;

import java.io.Serializable;
import java.io.IOException;
import java.util.ArrayList;

import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.emergencyAlert.PanicButton;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;

public class Patient extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private ArrayList<VitalSign> vitalSigns;
    private ArrayList<Prescription> prescriptions;
    private MedicalHistory medicalHistory;
    private PanicButton panicButton;
    private VitalsDatabase vitalsDatabase;
    
    public Patient(String name, String email, String password, String phone, String address, int userID) {
        super(name, email, password, phone, address, userID);
        this.vitalSigns = new ArrayList<>();
        this.prescriptions = new ArrayList<>();
        this.medicalHistory = new MedicalHistory();
        this.panicButton = new PanicButton(this);
    }
    
    public ArrayList<VitalSign> getVitalSigns() {
        return vitalSigns;
    }
    
    public void addVitalSign(VitalSign vitalSign) {
        vitalSigns.add(vitalSign);
    }
    
    public ArrayList<Prescription> getPrescriptions() {
        return prescriptions;
    }
    
    public void addPrescription(Prescription prescription) {
        prescriptions.add(prescription);
    }
    
    public MedicalHistory getMedicalHistory() {
        // Initialize if needed
        if (medicalHistory == null) {
            medicalHistory = new MedicalHistory();
        }
        return medicalHistory;
    }
    
    public void setMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public PanicButton getPanicButton() {
        return panicButton;
    }
    
    public void setPanicButton(PanicButton panicButton) {
        this.panicButton = panicButton;
    }
    
    public VitalsDatabase getVitalsDatabase() {
        return vitalsDatabase;
    }

    public void setVitalsDatabase(VitalsDatabase vitalsDatabase) {
        this.vitalsDatabase = vitalsDatabase;
        
        // Ensure the database has a reference back to this patient
        if (vitalsDatabase != null) {
            vitalsDatabase.setPatient(this);
        }
    }

    /**
     * Trigger the panic button with a specific reason
     */
    public void triggerPanicButton(String reason) {
        if (this.panicButton == null) {
            this.panicButton = new PanicButton(this);
        }
        this.panicButton.triggerAlert(reason);
    }

    /**
     * Disable the panic button
     */
    public void disablePanicButton() {
        if (this.panicButton == null) {
            this.panicButton = new PanicButton(this);
        }
        this.panicButton.disable();
    }

    /**
     * Enable the panic button
     */
    public void enablePanicButton() {
        if (this.panicButton == null) {
            this.panicButton = new PanicButton(this);
        }
        this.panicButton.enable();
    }

    /**
     * Initialize medical history for the patient
     */
    public void initializeMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // After loading, make sure vitals database has proper reference back to patient
        if (vitalsDatabase != null) {
            vitalsDatabase.setPatient(this);
        }
    }
}

