package com.rhms.persistence;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.rhms.appointmentScheduling.Appointment;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.emergencyAlert.PanicButton;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.userManagement.Administrator;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;
import com.rhms.userManagement.User;
import com.rhms.userManagement.UserIDManager;

/**
 * Manages persistence of application data by saving and loading from files
 */
public class DataPersistenceManager {
    // File paths for stored data
    private static final String DATA_DIRECTORY = "data";
    private static final String PATIENTS_FILE = DATA_DIRECTORY + "/patients.dat";
    private static final String DOCTORS_FILE = DATA_DIRECTORY + "/doctors.dat";
    private static final String ADMINS_FILE = DATA_DIRECTORY + "/admins.dat";
    private static final String APPOINTMENTS_FILE = DATA_DIRECTORY + "/appointments.dat";
    private static final String USER_IDS_FILE = DATA_DIRECTORY + "/user_ids.dat";

    // Preprocessing of data before serialization to avoid circular references
    private static void preprocessDataForSerialization(ArrayList<Patient> patients, ArrayList<Doctor> doctors) {
        // Remove circular references between doctors and patients
        for (Doctor doctor : doctors) {
            doctor.getPatients().clear(); // Clear the patients list temporarily
        }

        // Note: After loading, we'll need to rebuild these relationships
    }

    // Postprocessing of data after deserialization to restore relationships
    private static void postprocessDataAfterDeserialization(ArrayList<Patient> patients, ArrayList<Doctor> doctors) {
        // Map to quickly find doctors and patients by ID
        Map<Integer, Doctor> doctorMap = new HashMap<>();
        Map<Integer, Patient> patientMap = new HashMap<>();

        // Build the maps
        for (Doctor doctor : doctors) {
            doctorMap.put(doctor.getUserID(), doctor);
        }

        for (Patient patient : patients) {
            patientMap.put(patient.getUserID(), patient);
        }

        // Restore relationships based on domain knowledge 
        // This example assumes doctor-patient assignment is tracked elsewhere
    }

    /**
     * Save all application data to files
     * @param patients List of patients
     * @param doctors List of doctors
     * @param admins List of administrators
     * @param appointments List of appointments
     * @return true if saving was successful
     */
    public static boolean saveAllData(ArrayList<Patient> patients, ArrayList<Doctor> doctors, 
                                     ArrayList<Administrator> admins, ArrayList<Appointment> appointments) {
        // Create data directory if it doesn't exist
        createDataDirectory();

        // Preprocess to avoid circular references
        preprocessDataForSerialization(patients, doctors);

        boolean patientsSuccess = savePatients(patients);
        boolean doctorsSuccess = saveDoctors(doctors);
        boolean adminsSuccess = saveAdmins(admins);
        boolean appointmentsSuccess = saveAppointments(appointments);
        boolean userIDsSuccess = saveUserIDs();

        return patientsSuccess && doctorsSuccess && adminsSuccess && appointmentsSuccess && userIDsSuccess;
    }

    /**
     * Load all application data from files
     * @param patients List to populate with loaded patients
     * @param doctors List to populate with loaded doctors
     * @param admins List to populate with loaded administrators
     * @param appointments List to populate with loaded appointments
     * @return true if loading was successful
     */
    public static boolean loadAllData(ArrayList<Patient> patients, ArrayList<Doctor> doctors, 
                                     ArrayList<Administrator> admins, ArrayList<Appointment> appointments) {
        // Check if data directory exists
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            System.out.println("No saved data found. Starting with empty data.");
            return false;
        }

        boolean userIDsSuccess = loadUserIDs();
        boolean patientsSuccess = loadPatients(patients);
        boolean doctorsSuccess = loadDoctors(doctors);
        boolean adminsSuccess = loadAdmins(admins);
        boolean appointmentsSuccess = loadAppointments(appointments, patients, doctors);

        // Postprocess to restore relationships
        postprocessDataAfterDeserialization(patients, doctors);

        return patientsSuccess && doctorsSuccess && adminsSuccess && appointmentsSuccess && userIDsSuccess;
    }

    /**
     * Save patients to file
     */
    private static boolean savePatients(ArrayList<Patient> patients) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PATIENTS_FILE))) {
            oos.writeObject(patients);
            System.out.println("Patients data saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving patients data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save doctors to file
     */
    private static boolean saveDoctors(ArrayList<Doctor> doctors) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DOCTORS_FILE))) {
            oos.writeObject(doctors);
            System.out.println("Doctors data saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving doctors data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save administrators to file
     */
    private static boolean saveAdmins(ArrayList<Administrator> admins) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ADMINS_FILE))) {
            oos.writeObject(admins);
            System.out.println("Administrators data saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving administrators data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save appointments to file
     * This requires special handling since Appointment objects contain references to Patients and Doctors
     */
    private static boolean saveAppointments(ArrayList<Appointment> appointments) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(APPOINTMENTS_FILE))) {
            // Create serializable appointment data objects
            ArrayList<SerializableAppointment> serializableAppointments = new ArrayList<>();

            for (Appointment appointment : appointments) {
                SerializableAppointment serAppointment = new SerializableAppointment(
                    appointment.getAppointmentDate(),
                    appointment.getDoctor().getUserID(),
                    appointment.getPatient().getUserID(),
                    appointment.getStatus(),
                    appointment.getNotes(),
                    appointment.getAppointmentID()
                );
                serializableAppointments.add(serAppointment);
            }

            oos.writeObject(serializableAppointments);
            System.out.println("Appointments data saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving appointments data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Save user ID counters to file to maintain ID sequences between runs
     */
    private static boolean saveUserIDs() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_IDS_FILE))) {
            UserIDCounter counter = new UserIDCounter(
                UserIDManager.getNextPatientID(),
                UserIDManager.getNextDoctorID(),
                UserIDManager.getNextAdminID()
            );
            oos.writeObject(counter);
            System.out.println("User ID counters saved successfully.");
            return true;
        } catch (IOException e) {
            System.err.println("Error saving user ID counters: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load patients from file
     */
    @SuppressWarnings("unchecked")
    private static boolean loadPatients(ArrayList<Patient> patients) {
        File file = new File(PATIENTS_FILE);
        if (!file.exists()) {
            System.out.println("No patients data file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Patient> loadedPatients = (ArrayList<Patient>) ois.readObject();
            patients.clear();
            patients.addAll(loadedPatients);
            System.out.println("Loaded " + patients.size() + " patients.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading patients data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load doctors from file
     */
    @SuppressWarnings("unchecked")
    private static boolean loadDoctors(ArrayList<Doctor> doctors) {
        File file = new File(DOCTORS_FILE);
        if (!file.exists()) {
            System.out.println("No doctors data file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Doctor> loadedDoctors = (ArrayList<Doctor>) ois.readObject();
            doctors.clear();
            doctors.addAll(loadedDoctors);
            System.out.println("Loaded " + doctors.size() + " doctors.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading doctors data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load administrators from file
     */
    @SuppressWarnings("unchecked")
    private static boolean loadAdmins(ArrayList<Administrator> admins) {
        File file = new File(ADMINS_FILE);
        if (!file.exists()) {
            System.out.println("No administrators data file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<Administrator> loadedAdmins = (ArrayList<Administrator>) ois.readObject();
            admins.clear();
            admins.addAll(loadedAdmins);
            System.out.println("Loaded " + admins.size() + " administrators.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading administrators data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load appointments from file and reconstruct with references to loaded patients and doctors
     */
    @SuppressWarnings("unchecked")
    private static boolean loadAppointments(ArrayList<Appointment> appointments,
                                         ArrayList<Patient> patients, ArrayList<Doctor> doctors) {
        File file = new File(APPOINTMENTS_FILE);
        if (!file.exists()) {
            System.out.println("No appointments data file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            ArrayList<SerializableAppointment> serializableAppointments = 
                (ArrayList<SerializableAppointment>) ois.readObject();

            appointments.clear();

            // Create appointment objects with references to actual patients and doctors
            for (SerializableAppointment serAppointment : serializableAppointments) {
                Doctor doctor = findDoctorByID(doctors, serAppointment.getDoctorID());
                Patient patient = findPatientByID(patients, serAppointment.getPatientID());

                if (doctor != null && patient != null) {
                    Appointment appointment = new Appointment(
                        serAppointment.getAppointmentDate(),
                        doctor,
                        patient,
                        serAppointment.getStatus(),
                        serAppointment.getNotes(),
                        serAppointment.getAppointmentID()
                    );
                    appointments.add(appointment);
                } else {
                    System.err.println("Warning: Could not find doctor or patient for appointment ID " + 
                                    serAppointment.getAppointmentID());
                }
            }

            System.out.println("Loaded " + appointments.size() + " appointments.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading appointments data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load user ID counters from file
     */
    private static boolean loadUserIDs() {
        File file = new File(USER_IDS_FILE);
        if (!file.exists()) {
            System.out.println("No user ID counters file found.");
            return false;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            UserIDCounter counter = (UserIDCounter) ois.readObject();
            UserIDManager.initializeCounters(
                counter.getNextPatientID(),
                counter.getNextDoctorID(),
                counter.getNextAdminID()
            );
            System.out.println("User ID counters loaded successfully.");
            return true;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading user ID counters: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Helper method to find a doctor by ID
     */
    private static Doctor findDoctorByID(ArrayList<Doctor> doctors, int doctorID) {
        for (Doctor doctor : doctors) {
            if (doctor.getUserID() == doctorID) {
                return doctor;
            }
        }
        return null;
    }

    /**
     * Helper method to find a patient by ID
     */
    private static Patient findPatientByID(ArrayList<Patient> patients, int patientID) {
        for (Patient patient : patients) {
            if (patient.getUserID() == patientID) {
                return patient;
            }
        }
        return null;
    }

    /**
     * Create data directory if it doesn't exist
     */
    private static void createDataDirectory() {
        File directory = new File(DATA_DIRECTORY);
        if (!directory.exists()) {
            boolean created = directory.mkdir();
            if (created) {
                System.out.println("Created data directory.");
            } else {
                System.err.println("Failed to create data directory.");
            }
        }
    }

    /**
     * Serializable class to store appointment data without direct references to objects
     */
    private static class SerializableAppointment implements Serializable {
        private static final long serialVersionUID = 1L;

        private Date appointmentDate;
        private int doctorID;
        private int patientID;
        private String status;
        private String notes;
        private int appointmentID;

        public SerializableAppointment(Date appointmentDate, int doctorID, int patientID,
                                       String status, String notes, int appointmentID) {
            this.appointmentDate = appointmentDate;
            this.doctorID = doctorID;
            this.patientID = patientID;
            this.status = status;
            this.notes = notes;
            this.appointmentID = appointmentID;
        }

        public Date getAppointmentDate() {
            return appointmentDate;
        }

        public int getDoctorID() {
            return doctorID;
        }

        public int getPatientID() {
            return patientID;
        }

        public String getStatus() {
            return status;
        }

        public String getNotes() {
            return notes;
        }

        public int getAppointmentID() {
            return appointmentID;
        }
    }

    /**
     * Serializable class to store user ID counters
     */
    private static class UserIDCounter implements Serializable {
        private static final long serialVersionUID = 1L;

        private int nextPatientID;
        private int nextDoctorID;
        private int nextAdminID;

        public UserIDCounter(int nextPatientID, int nextDoctorID, int nextAdminID) {
            this.nextPatientID = nextPatientID;
            this.nextDoctorID = nextDoctorID;
            this.nextAdminID = nextAdminID;
        }

        public int getNextPatientID() {
            return nextPatientID;
        }

        public int getNextDoctorID() {
            return nextDoctorID;
        }

        public int getNextAdminID() {
            return nextAdminID;
        }
    }
}