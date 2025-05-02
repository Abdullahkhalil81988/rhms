package com.rhms.appointmentScheduling;

import java.util.ArrayList;
import java.util.Date;

import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;
import com.rhms.appointmentScheduling.Appointment;

public class AppointmentManager {
    private ArrayList<Appointment> appointments;
    
    public AppointmentManager() {
        this.appointments = new ArrayList<>();
    }
    
    // Get all appointments
    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }
    
    // Request a new appointment
    public Appointment requestAppointment(Date appointmentDate, Doctor doctor, Patient patient) {
        Appointment appointment = new Appointment(appointmentDate, doctor, patient);
        appointments.add(appointment);
        return appointment;
    }
    
    // Approve an appointment
    public void approveAppointment(Appointment appointment) {
        appointment.setStatus("Approved");
    }
    
    // Cancel an appointment
    public void cancelAppointment(Appointment appointment) {
        appointment.setStatus("Cancelled");
    }
    
    // Get appointments for a specific patient
    public ArrayList<Appointment> getPatientAppointments(Patient patient) {
        ArrayList<Appointment> patientAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().equals(patient)) {
                patientAppointments.add(appointment);
            }
        }
        return patientAppointments;
    }
    
    // Get pending appointments for a specific doctor
    public ArrayList<Appointment> getDoctorPendingAppointments(Doctor doctor) {
        ArrayList<Appointment> pendingAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().equals(doctor) && 
                appointment.getStatus().equals("Pending")) {
                pendingAppointments.add(appointment);
            }
        }
        return pendingAppointments;
    }
    
    // Get active appointments for a specific doctor
    public ArrayList<Appointment> getDoctorActiveAppointments(Doctor doctor) {
        ArrayList<Appointment> activeAppointments = new ArrayList<>();
        for (Appointment appointment : appointments) {
            if (appointment.getDoctor().equals(doctor) && 
                !appointment.getStatus().equals("Cancelled")) {
                activeAppointments.add(appointment);
            }
        }
        return activeAppointments;
    }
}
