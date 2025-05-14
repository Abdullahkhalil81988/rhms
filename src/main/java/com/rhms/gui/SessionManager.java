package com.rhms.gui;

import java.util.ArrayList;

import com.rhms.userManagement.*;
import com.rhms.appointmentScheduling.*;
import com.rhms.emergencyAlert.*;
import com.rhms.doctorPatientInteraction.*;
import com.rhms.notifications.*;
import com.rhms.persistence.DataPersistenceManager;

/**
 * Manages application state and user sessions
 */
public class SessionManager {
    private static User currentUser = null;
    private static String userType = "";
    
    // Data repositories
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static ArrayList<Administrator> administrators = new ArrayList<>();
    
    // Services
    private static AppointmentManager appointmentManager = new AppointmentManager();
    private static EmergencyAlert emergencyAlert = new EmergencyAlert();
    private static ChatServer chatServer = ChatServer.getInstance();
    private static SMSNotification smsNotification = new SMSNotification();
    private static EmailNotification emailNotification = new EmailNotification();
    
    // Initialize data, load from files if available
    static {
        initialize();
    }
    
    private static void initializeDemoData() {
        // Add demo admin
        Administrator admin = new Administrator("Admin User", "admin@hospital.com", "admin123", 
                                              "123456789", "Hospital HQ", 1001);
        administrators.add(admin);
        
        // Add demo doctor
        Doctor doctor = new Doctor("Dr. Abdullah", "abdullah@hospital.com", "doctor123", 
                                  "987654321", "Medical Center", 2001, "Cardiology", 10);
        doctors.add(doctor);
        
        // Add demo patient
        Patient patient = new Patient("Ali", "ali@example.com", "patient123", 
                                     "555123456", "123 Main St", 3001);
        patients.add(patient);
        
        System.out.println("Demo accounts created for testing:");
        System.out.println("Admin - Email: admin@hospital.com, Password: admin123");
        System.out.println("Doctor - Email: abdullah@hospital.com, Password: doctor123");
        System.out.println("Patient - Email: ali@example.com, Password: patient123");
    }
    
    public static void saveData() {
        try {
            // Fix relationships before saving
            fixObjectRelationships();
            
            // Save all data using the DataPersistenceManager
            boolean saved = DataPersistenceManager.saveAllData(
                patients, 
                doctors, 
                administrators, 
                appointmentManager.getAppointments(),
                chatServer
            );
            
            if (saved) {
                System.out.println("All data saved successfully.");
            } else {
                System.err.println("Failed to save data.");
            }
        } catch (Exception e) {
            System.err.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Fix object relationships before saving
     * This ensures that references between objects are consistent
     */
    private static void fixObjectRelationships() {
        // Fix patient-doctor relationships
        for (Patient patient : patients) {
            if (patient.getMedicalHistory() != null) {
                // Ensure all feedback is properly connected to the right objects
                for (Feedback feedback : patient.getMedicalHistory().getPastConsultations()) {
                    // Ensure doctor and patient references are up to date
                    if (feedback.getDoctor() != null) {
                        // Find the doctor in our current list to ensure it's the same instance
                        for (Doctor doctor : doctors) {
                            if (doctor.getUserID() == feedback.getDoctor().getUserID()) {
                                feedback.setDoctor(doctor);
                                break;
                            }
                        }
                    }
                    
                    // Update patient reference to ensure consistency
                    feedback.setPatient(patient);
                }
            }
        }
        
        // Fix doctor-patient relationships
        for (Doctor doctor : doctors) {
            if (doctor.getPatients() != null) {
                ArrayList<Patient> updatedPatients = new ArrayList<>();
                for (Patient p : doctor.getPatients()) {
                    // Find the patient in our current list
                    for (Patient current : patients) {
                        if (current.getUserID() == p.getUserID()) {
                            updatedPatients.add(current);
                            break;
                        }
                    }
                }
                // Replace with updated list if we found any matches
                if (!updatedPatients.isEmpty()) {
                    doctor.setPatients(updatedPatients);
                }
            }
        }
        
        // Fix appointment relationships
        for (Appointment appointment : appointmentManager.getAppointments()) {
            // Update doctor reference
            if (appointment.getDoctor() != null) {
                for (Doctor doctor : doctors) {
                    if (doctor.getUserID() == appointment.getDoctor().getUserID()) {
                        appointment.setDoctor(doctor);
                        break;
                    }
                }
            }
            
            // Update patient reference
            if (appointment.getPatient() != null) {
                for (Patient patient : patients) {
                    if (patient.getUserID() == appointment.getPatient().getUserID()) {
                        appointment.setPatient(patient);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Initialize the system
     * Should be called when application starts
     */
    public static void initialize() {
        // Load data if available
        boolean dataLoaded = DataPersistenceManager.loadAllData(
            patients, doctors, administrators, appointmentManager.getAppointments());
        
        if (!dataLoaded) {
            // If no data was loaded, initialize demo data
            initializeDemoData();
        }
        
        // Initialize chat system
        initializeChatSystem();
    }
    
    // Check login credentials
    public static User authenticateUser(String email, String password) {
        // Try to authenticate as administrator
        for (Administrator admin : administrators) {
            if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
                currentUser = admin;
                userType = "Administrator";
                return admin;
            }
        }
        
        // Try to authenticate as doctor
        for (Doctor doctor : doctors) {
            if (doctor.getEmail().equals(email) && doctor.getPassword().equals(password)) {
                currentUser = doctor;
                userType = "Doctor";
                return doctor;
            }
        }
        
        // Try to authenticate as patient
        for (Patient patient : patients) {
            if (patient.getEmail().equals(email) && patient.getPassword().equals(password)) {
                currentUser = patient;
                userType = "Patient";
                return patient;
            }
        }
        
        return null; // Authentication failed
    }
    
    /**
     * Login as a patient
     * @param email Patient's email
     * @param password Patient's password
     * @return true if login successful, false otherwise
     */
    public static boolean loginPatient(String email, String password) {
        for (Patient patient : patients) {
            if (patient.getEmail().equals(email) && patient.getPassword().equals(password)) {
                currentUser = patient;
                userType = "Patient";
                return true;
            }
        }
        return false;
    }

    /**
     * Login as a doctor
     * @param email Doctor's email
     * @param password Doctor's password
     * @return true if login successful, false otherwise
     */
    public static boolean loginDoctor(String email, String password) {
        for (Doctor doctor : doctors) {
            if (doctor.getEmail().equals(email) && doctor.getPassword().equals(password)) {
                currentUser = doctor;
                userType = "Doctor";
                return true;
            }
        }
        return false;
    }

    /**
     * Login as an administrator
     * @param email Administrator's email
     * @param password Administrator's password
     * @return true if login successful, false otherwise
     */
    public static boolean loginAdministrator(String email, String password) {
        for (Administrator admin : administrators) {
            if (admin.getEmail().equals(email) && admin.getPassword().equals(password)) {
                currentUser = admin;
                userType = "Administrator";
                return true;
            }
        }
        return false;
    }
    
    // Getters for all services and repositories
    public static User getCurrentUser() {
        return currentUser;
    }
    
    public static String getUserType() {
        return userType;
    }
    
    public static ArrayList<Patient> getPatients() {
        return patients;
    }
    
    public static ArrayList<Doctor> getDoctors() {
        return doctors;
    }
    
    public static ArrayList<Administrator> getAdministrators() {
        return administrators;
    }
    
    public static AppointmentManager getAppointmentManager() {
        return appointmentManager;
    }
    
    public static EmergencyAlert getEmergencyAlert() {
        return emergencyAlert;
    }
    
    public static ChatServer getChatServer() {
        return chatServer;
    }
    
    public static SMSNotification getSmsNotification() {
        return smsNotification;
    }
    
    public static EmailNotification getEmailNotification() {
        return emailNotification;
    }
    
    /**
     * Initialize chat system
     */
    public static void initializeChatSystem() {
        // Ensure the chat server is initialized
        ChatServer chatServer = getChatServer();
        
        // Register all users with the chat server
        // (This could be moved to user login process)
        for (Doctor doctor : doctors) {
            // Any initialization needed for doctor chat
        }
        
        for (Patient patient : patients) {
            // Any initialization needed for patient chat
        }
    }
    
    // Log out the current user
    public static void logout() {
        // Save data before logging out
        saveData();
        
        currentUser = null;
        userType = "";
    }
    
    // Register a new patient in the system
    public static void registerPatient(Patient patient) {
        patients.add(patient);
        // Save data after registration
        saveData();
    }
    
    // Register a new doctor in the system
    public static void registerDoctor(Doctor doctor) {
        doctors.add(doctor);
        // Save data after registration
        saveData();
    }
    
    // Register a new administrator in the system
    public static void registerAdministrator(Administrator admin) {
        administrators.add(admin);
        // Save data after registration
        saveData();
    }
    
    public static void addPatient(Patient patient) {
        patients.add(patient);
        saveData();
    }

    public static void removePatient(Patient patient) {
        patients.remove(patient);
        saveData();
    }

    public static void addDoctor(Doctor doctor) {
        doctors.add(doctor);
        // Use Administrator's existing method if current user is Administrator
        if (currentUser instanceof Administrator) {
            ((Administrator) currentUser).addDoctor(doctor);
        }
        saveData();
    }

    public static void removeDoctor(Doctor doctor) {
        doctors.remove(doctor);
        // Use Administrator's existing method if current user is Administrator
        if (currentUser instanceof Administrator) {
            ((Administrator) currentUser).removeDoctor(doctor);
        }
        saveData();
    }

    public static void addAdministrator(Administrator admin) {
        administrators.add(admin);
        saveData();
    }
}