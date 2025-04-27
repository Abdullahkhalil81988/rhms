package com.rhms.userManagement;

/**
 * Centralized ID manager to ensure unique IDs across all user types
 * This prevents ID conflicts between patients, doctors, and administrators
 */
public class UserIDManager {
    // Separate ID ranges for different user types
    private static final int ADMIN_ID_START = 1000;    // 1000-1999
    private static final int DOCTOR_ID_START = 2000;   // 2000-2999
    private static final int PATIENT_ID_START = 3000;  // 3000-3999
    
    // Track the next available ID for each user type
    private static int nextAdminID = ADMIN_ID_START;
    private static int nextDoctorID = DOCTOR_ID_START;
    private static int nextPatientID = PATIENT_ID_START;
    
    /**
     * Generates a unique ID for an administrator
     * @return new admin ID
     */
    public static synchronized int getNextAdminID() {
        return nextAdminID++;
    }
    
    /**
     * Generates a unique ID for a doctor
     * @return new doctor ID
     */
    public static synchronized int getNextDoctorID() {
        return nextDoctorID++;
    }
    
    /**
     * Generates a unique ID for a patient
     * @return new patient ID
     */
    public static synchronized int getNextPatientID() {
        return nextPatientID++;
    }
    
    /**
     * Validates if an ID belongs to an administrator
     * @param userID The ID to check
     * @return true if it's an admin ID
     */
    public static boolean isAdminID(int userID) {
        return userID >= ADMIN_ID_START && userID < DOCTOR_ID_START;
    }
    
    /**
     * Validates if an ID belongs to a doctor
     * @param userID The ID to check
     * @return true if it's a doctor ID
     */
    public static boolean isDoctorID(int userID) {
        return userID >= DOCTOR_ID_START && userID < PATIENT_ID_START;
    }
    
    /**
     * Validates if an ID belongs to a patient
     * @param userID The ID to check
     * @return true if it's a patient ID
     */
    public static boolean isPatientID(int userID) {
        return userID >= PATIENT_ID_START;
    }
}