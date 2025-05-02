package com.rhms.userManagement;

import java.io.Serializable;
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
        return medicalHistory;
    }
    
    public PanicButton getPanicButton() {
        return panicButton;
    }
    
    public void setPanicButton(PanicButton panicButton) {
        this.panicButton = panicButton;
    }
    
    // Add or modify method
    public VitalsDatabase getVitalsDatabase() {
        VitalsDatabase db = new VitalsDatabase(this);
        // Add all vital signs to the database
        for (VitalSign vs : this.vitalSigns) {
            db.addVitalRecord(vs);
        }
        return db;
    }

    /**
     * Sets the vitals database for this patient
     */
    public void setVitalsDatabase(VitalsDatabase vitalsDatabase) {
        this.vitalsDatabase = vitalsDatabase;
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

    /**
     * Alternative method name for setting medical history
     */
    public void setMedicalHistory(MedicalHistory medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
}

