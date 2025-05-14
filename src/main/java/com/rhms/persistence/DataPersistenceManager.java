package com.rhms.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rhms.appointmentScheduling.Appointment;
import com.rhms.doctorPatientInteraction.ChatServer;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.emergencyAlert.PanicButton;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.userManagement.Administrator;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;
import com.rhms.userManagement.User;

/**
 * Manages data persistence for the application
 * Handles saving and loading data to/from files
 */
public class DataPersistenceManager {
    
    private static String DATA_DIRECTORY = "data";
    
    /**
     * Ensure data directory exists
     */
    private static void ensureDataDirectoryExists() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (!created) {
                System.err.println("ERROR: Could not create data directory at " + directory.getAbsolutePath());
                
                // Try alternative location if the primary location fails
                try {
                    File userDir = new File(System.getProperty("user.home"), "rhms_data");
                    if (userDir.exists() || userDir.mkdirs()) {
                        System.out.println("Using alternative data directory: " + userDir.getAbsolutePath());
                        DATA_DIRECTORY = userDir.getAbsolutePath();
                    }
                } catch (Exception e) {
                    System.err.println("Could not create alternative data directory: " + e.getMessage());
                }
            } else {
                System.out.println("Created data directory at " + directory.getAbsolutePath());
            }
        }
        
        // Test write permissions
        try {
            File testFile = new File(directory, "test.tmp");
            if (testFile.createNewFile()) {
                testFile.delete();
            } else {
                System.err.println("WARNING: No write permission in data directory!");
            }
        } catch (IOException e) {
            System.err.println("ERROR: Cannot write to data directory: " + e.getMessage());
        }
    }
    
    /**
     * Save users to a file
     * @param users List of users to save
     * @param fileName Base file name
     * @throws IOException If there's an error writing to file
     */
    public static <T extends User> void saveUsers(ArrayList<T> users, String fileName) throws IOException {
        ensureDataDirectoryExists();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIRECTORY + File.separator + fileName + ".ser"))) {
            oos.writeObject(users);
            System.out.println(fileName + " data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving " + fileName + " data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load users from a file
     * @param fileName Base file name
     * @return List of users, or null if file doesn't exist
     * @throws IOException If there's an error reading the file
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public static <T extends User> ArrayList<T> loadUsers(String fileName) throws IOException, ClassNotFoundException {
        File file = new File(DATA_DIRECTORY + File.separator + fileName + ".ser");
        if (!file.exists()) {
            System.out.println("No " + fileName + " data file found.");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            ArrayList<T> users = (ArrayList<T>) ois.readObject();
            System.out.println(fileName + " data loaded successfully.");
            return users;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading " + fileName + " data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Save the ChatServer to a file
     * @param chatServer ChatServer instance to save
     * @throws IOException If there's an error writing to file
     */
    public static void saveChatServer(ChatServer chatServer) throws IOException {
        ensureDataDirectoryExists();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIRECTORY + File.separator + "chat_data.ser"))) {
            oos.writeObject(chatServer);
            System.out.println("Chat data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving chat data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load the ChatServer from a file
     * @return ChatServer instance, or null if file doesn't exist
     * @throws IOException If there's an error reading the file
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    public static ChatServer loadChatServer() throws IOException, ClassNotFoundException {
        File file = new File(DATA_DIRECTORY + File.separator + "chat_data.ser");
        if (!file.exists()) {
            System.out.println("No chat data file found. Using a new instance.");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            ChatServer chatServer = (ChatServer) ois.readObject();
            System.out.println("Chat data loaded successfully.");
            return chatServer;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading chat data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Save appointments to a file
     * @param appointments List of appointments to save
     * @throws IOException If there's an error writing to file
     */
    public static void saveAppointments(ArrayList<Appointment> appointments) throws IOException {
        ensureDataDirectoryExists();
        
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(DATA_DIRECTORY + File.separator + "appointments.ser"))) {
            oos.writeObject(appointments);
            System.out.println("Appointment data saved successfully.");
        } catch (IOException e) {
            System.err.println("Error saving appointment data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load appointments from a file
     * @return List of appointments, or null if file doesn't exist
     * @throws IOException If there's an error reading the file
     * @throws ClassNotFoundException If the class of a serialized object cannot be found
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<Appointment> loadAppointments() throws IOException, ClassNotFoundException {
        File file = new File(DATA_DIRECTORY + File.separator + "appointments.ser");
        if (!file.exists()) {
            System.out.println("No appointment data file found.");
            return null;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(file))) {
            ArrayList<Appointment> appointments = (ArrayList<Appointment>) ois.readObject();
            System.out.println("Appointment data loaded successfully.");
            return appointments;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading appointment data: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Load all data from files
     * @param patients List to store loaded patients
     * @param doctors List to store loaded doctors
     * @param administrators List to store loaded administrators
     * @param appointments List to store loaded appointments
     * @return true if data was loaded successfully, false otherwise
     */
    public static boolean loadAllData(
            ArrayList<Patient> patients,
            ArrayList<Doctor> doctors,
            ArrayList<Administrator> administrators,
            ArrayList<Appointment> appointments) {
        
        boolean anyDataLoaded = false;
        
        try {
            // Load patients
            ArrayList<Patient> loadedPatients = loadUsers("patients");
            if (loadedPatients != null) {
                patients.clear();
                patients.addAll(loadedPatients);
                anyDataLoaded = true;
            }
            
            // Load doctors
            ArrayList<Doctor> loadedDoctors = loadUsers("doctors");
            if (loadedDoctors != null) {
                doctors.clear();
                doctors.addAll(loadedDoctors);
                anyDataLoaded = true;
            }
            
            // Load administrators
            ArrayList<Administrator> loadedAdmins = loadUsers("administrators");
            if (loadedAdmins != null) {
                administrators.clear();
                administrators.addAll(loadedAdmins);
                anyDataLoaded = true;
            }
            
            // Load appointments
            ArrayList<Appointment> loadedAppointments = loadAppointments();
            if (loadedAppointments != null) {
                appointments.clear();
                appointments.addAll(loadedAppointments);
                anyDataLoaded = true;
            }
            
            // Load chat server
            ChatServer loadedChatServer = loadChatServer();
            if (loadedChatServer != null) {
                // Set the loaded chat server as the singleton instance
                // This typically requires a method in ChatServer to set the instance
                anyDataLoaded = true;
            }
            
            // Restore relationships between objects
            restoreObjectRelationships(patients, doctors, administrators, appointments);
            
            return anyDataLoaded;
            
        } catch (Exception e) {
            System.err.println("Error during data loading: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Restore object relationships after loading from files
     * This ensures that references between objects are properly set
     */
    private static void restoreObjectRelationships(
            ArrayList<Patient> patients,
            ArrayList<Doctor> doctors,
            ArrayList<Administrator> administrators,
            ArrayList<Appointment> appointments) {
        
        // Create lookup maps for faster object retrieval by ID
        Map<Integer, Patient> patientMap = new HashMap<>();
        Map<Integer, Doctor> doctorMap = new HashMap<>();
        
        for (Patient p : patients) {
            patientMap.put(p.getUserID(), p);
        }
        
        for (Doctor d : doctors) {
            doctorMap.put(d.getUserID(), d);
        }
        
        // Fix doctor-patient relationships
        for (Doctor doctor : doctors) {
            if (doctor.getPatients() != null) {
                ArrayList<Patient> updatedPatients = new ArrayList<>();
                for (Patient p : doctor.getPatients()) {
                    // Get the patient with the same ID from the current patients list
                    Patient currentPatient = patientMap.get(p.getUserID());
                    if (currentPatient != null) {
                        updatedPatients.add(currentPatient);
                    } else {
                        updatedPatients.add(p); // Keep original if not found
                    }
                }
                // Replace with updated list
                doctor.setPatients(updatedPatients);
            }
        }
        
        // Fix appointment relationships
        for (Appointment appointment : appointments) {
            // Update doctor reference
            if (appointment.getDoctor() != null) {
                Doctor currentDoctor = doctorMap.get(appointment.getDoctor().getUserID());
                if (currentDoctor != null) {
                    appointment.setDoctor(currentDoctor);
                }
            }
            
            // Update patient reference
            if (appointment.getPatient() != null) {
                Patient currentPatient = patientMap.get(appointment.getPatient().getUserID());
                if (currentPatient != null) {
                    appointment.setPatient(currentPatient);
                }
            }
        }
        
        // Fix feedback relationships
        for (Patient patient : patients) {
            if (patient.getMedicalHistory() != null) {
                for (Feedback feedback : patient.getMedicalHistory().getPastConsultations()) {
                    // Update doctor reference
                    if (feedback.getDoctor() != null) {
                        Doctor currentDoctor = doctorMap.get(feedback.getDoctor().getUserID());
                        if (currentDoctor != null) {
                            feedback.setDoctor(currentDoctor);
                        }
                    }
                    
                    // Update patient reference
                    feedback.setPatient(patient);
                }
            }
        }
    }
    
    /**
     * Saves all application data at once
     * @param patients List of patients
     * @param doctors List of doctors
     * @param administrators List of administrators
     * @param appointments List of appointments
     * @param chatServer ChatServer instance
     * @return true if data was saved successfully, false otherwise
     */
    public static boolean saveAllData(
            ArrayList<Patient> patients,
            ArrayList<Doctor> doctors,
            ArrayList<Administrator> administrators,
            ArrayList<Appointment> appointments,
            ChatServer chatServer) {
        
        try {
            saveUsers(patients, "patients");
            saveUsers(doctors, "doctors");
            saveUsers(administrators, "administrators");
            saveAppointments(appointments);
            saveChatServer(chatServer);
            return true;
        } catch (Exception e) {
            System.err.println("Error during data saving: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}