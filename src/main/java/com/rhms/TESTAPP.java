package com.rhms;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.rhms.analytics.AnalyticsResult;
import com.rhms.analytics.HealthAnalytics;
import com.rhms.analytics.HealthDataVisualizer;
import com.rhms.appointmentScheduling.*;
import com.rhms.doctorPatientInteraction.*;
import com.rhms.emergencyAlert.*;
import com.rhms.exceptions.ErrorHandler;
import com.rhms.exceptions.RHMSException;
import com.rhms.healthDataHandling.*;
import com.rhms.notifications.EmailNotification;
import com.rhms.notifications.ReminderService;
import com.rhms.notifications.SMSNotification;
import com.rhms.reporting.ReportsGenerator;
import com.rhms.userManagement.*;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class TESTAPP {
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static ArrayList<Administrator> administrators = new ArrayList<>();  // Added administrators list
    private static AppointmentManager appointmentManager = new AppointmentManager();
    private static EmergencyAlert emergencyAlert = new EmergencyAlert();
    private static Scanner scanner = new Scanner(System.in);
    private static String userType = ""; // Store user type
    private static User currentUser = null; // Track logged in user
    private static boolean isLoggedIn = false; // Track login status
    private static ChatServer chatServer = ChatServer.getInstance();
    private static SMSNotification smsNotification = new SMSNotification();
    // Add this with other field declarations
    private static EmailNotification emailNotification = new EmailNotification();

    public static void main(String[] args) {
        // Initialize demo data
        initializeDemoData();
        
        boolean running = true;
        while (running) {
            try {
                // Show login screen when not logged in
                if (!isLoggedIn) {
                    showLoginMenu();
                    int choice = Integer.parseInt(scanner.nextLine().trim());
                    
                    switch (choice) {
                        case 1:
                            performLogin();
                            break;
                        case 2:
                            // Registration handled in admin menu now
                            System.out.println("Registration must be done through an administrator.");
                            break;
                        case 0:
                            System.out.println("Exiting RHMS System. Goodbye!");
                            running = false;
                            break;
                        default:
                            throw new RHMSException(
                                RHMSException.ErrorCode.INVALID_INPUT,
                                "Invalid menu option: " + choice + ". Please choose options 0-2.",
                                "Login menu selection"
                            );
                    }
                } else {
                    // Show role-specific dashboard based on user type
                    if ("Admin".equals(userType)) {
                        showAdminDashboard();
                    } else if ("Patient".equals(userType)) {
                        showPatientDashboard();
                    } else if ("Doctor".equals(userType)) {
                        showDoctorDashboard();
                    }
                }
            } catch (RHMSException e) {
                ErrorHandler.handleException(e);
            } catch (Exception e) {
                ErrorHandler.handleGeneralException(e);
            }
        }
        scanner.close();
    }

    // Initialize demo users for testing
    private static void initializeDemoData() {
        // Add demo admin
        Administrator admin = new Administrator("Admin User", "admin@hospital.com", "admin123", 
                                              "123456789", "Hospital HQ", 1001);
        administrators.add(admin);
        
        // Add demo doctor with updated name: Abdullah
        Doctor doctor = new Doctor("Dr. Abdullah", "abdullah@hospital.com", "doctor123", 
                                  "987654321", "Medical Center", 2001, "Cardiology", 10);
        doctors.add(doctor);
        
        // Add demo patient with updated name: Ali
        Patient patient = new Patient("Ali", "ali@example.com", "patient123", 
                                     "555123456", "123 Main St", 3001);
        patients.add(patient);
        
        System.out.println("Demo accounts created:");
        System.out.println("Admin - Email: admin@hospital.com, Password: admin123");
        System.out.println("Doctor - Email: abdullah@hospital.com, Password: doctor123");
        System.out.println("Patient - Email: ali@example.com, Password: patient123");
    }
    
    // Display login menu
    private static void showLoginMenu() {
        System.out.println("\n===== RHMS Login =====");
        System.out.println("1. Login");
        System.out.println("2. Request Registration");
        System.out.println("0. Exit System");
        System.out.print("Choose an option: ");
    }
    
    // Handle user login process
    private static void performLogin() throws RHMSException {
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();
        
        // Try to authenticate as administrator
        for (Administrator admin : administrators) {
            if (admin.getEmail().equalsIgnoreCase(email) && admin.getPassword().equals(password)) {
                currentUser = admin;
                userType = "Admin";
                isLoggedIn = true;
                System.out.println("Welcome, Administrator " + admin.getName());
                return;
            }
        }
        
        // Try to authenticate as doctor
        for (Doctor doctor : doctors) {
            if (doctor.getEmail().equalsIgnoreCase(email) && doctor.getPassword().equals(password)) {
                currentUser = doctor;
                userType = "Doctor";
                isLoggedIn = true;
                System.out.println("Welcome, Dr. " + doctor.getName());
                return;
            }
        }
        
        // Try to authenticate as patient
        for (Patient patient : patients) {
            if (patient.getEmail().equalsIgnoreCase(email) && patient.getPassword().equals(password)) {
                currentUser = patient;
                userType = "Patient";
                isLoggedIn = true;
                System.out.println("Welcome, " + patient.getName());
                return;
            }
        }
        
        // If we reach here, authentication failed
        throw new RHMSException(
            RHMSException.ErrorCode.AUTHENTICATION_ERROR,
            "Invalid email or password.",
            "User login attempt"
        );
    }
    
    // Perform user logout
    private static void performLogout() {
        currentUser = null;
        userType = "";
        isLoggedIn = false;
        System.out.println("You have been successfully logged out.");
    }
    
    // Display admin dashboard
    private static void showAdminDashboard() throws RHMSException {
        System.out.println("\n===== Administrator Dashboard =====");
        System.out.println("Welcome, " + currentUser.getName());
        System.out.println("1. Register Patient");
        System.out.println("2. Register Doctor");
        System.out.println("3. Register Administrator");
        System.out.println("4. Schedule Appointment");
        System.out.println("5. Send Notifications");
        System.out.println("6. View All Appointments");
        System.out.println("7. View All Users");
        System.out.println("8. Logout");
        System.out.println("0. Exit System");
        System.out.print("Choose an option: ");
        
        int choice = Integer.parseInt(scanner.nextLine().trim());
        
        switch (choice) {
            case 1: registerPatient(); break;
            case 2: registerDoctor(); break;
            case 3: registerAdministrator(); break;
            case 4: scheduleAppointmentAdmin(); break;
            case 5: showNotificationMenu(); break;
            case 6: viewAllAppointments(); break;
            case 7: viewAllUsers(); break;
            case 8: performLogout(); break;
            case 0: 
                System.out.println("Exiting RHMS System. Goodbye!");
                System.exit(0);
                break;
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid admin dashboard option: " + choice + ". Please select a valid option.",
                    "Admin dashboard selection"
                );
        }
    }
    
    // Display patient dashboard
    private static void showPatientDashboard() throws RHMSException {
        System.out.println("\n===== Patient Dashboard =====");
        System.out.println("Welcome, " + currentUser.getName());
        System.out.println("1. Schedule an Appointment");
        System.out.println("2. View My Appointments");
        System.out.println("3. View My Vitals");
        System.out.println("4. Upload Vitals from CSV");
        System.out.println("5. View Doctor Feedback & Medications");
        System.out.println("6. Provide Doctor Feedback");
        System.out.println("7. View Health Trends & Analysis");  // New option
        System.out.println("8. View Vitals Graphs");             // New option
        System.out.println("9. Generate Health Report");         // New option
        System.out.println("10. Trigger Emergency Alert");
        System.out.println("11. Enable/Disable Panic Button");
        System.out.println("12. Join Video Consultation");
        System.out.println("13. Open Chat");
        System.out.println("14. Logout");
        System.out.println("15. Email My Doctor");               // New option
        System.out.println("0. Exit System");
        System.out.print("Choose an option: ");
        
        int choice = safeNextInt(null);
        
        switch (choice) {
            case 1: scheduleAppointmentPatient(); break;
            case 2: viewMyAppointments(); break;
            case 3: viewVitals(); break;
            case 4: uploadVitalsFromCSV(); break;
            case 5: viewMyFeedbackAndMedications(); break;
            case 6: provideFeedback(); break;
            case 7: viewHealthTrends(); break;           // New feature
            case 8: viewVitalsGraph(); break;            // New feature
            case 9: generateReport(); break;             // New feature
            case 10: triggerEmergencyAlert(); break;
            case 11: togglePanicButton(); break;
            case 12: joinVideoConsultation(); break;
            case 13: openChat(); break;
            case 14: performLogout(); break;
            case 15: emailMyDoctor(); break;             // New feature
            case 0: 
                System.out.println("Exiting RHMS System. Goodbye!");
                System.exit(0);
                break;
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid patient dashboard option: " + choice + ". Please select a valid option.",
                    "Patient dashboard selection"
                );
        }
    }
    
    // Display doctor dashboard
    private static void showDoctorDashboard() throws RHMSException {
        System.out.println("\n===== Doctor Dashboard =====");
        System.out.println("Welcome, Dr. " + currentUser.getName());
        System.out.println("1. View Pending Appointments");
        System.out.println("2. Approve Appointment");
        System.out.println("3. Cancel Appointment");
        System.out.println("4. Upload Patient Vitals");
        System.out.println("5. View Patient Vitals");
        System.out.println("6. Provide Prescription & Feedback");
        System.out.println("7. View Patient Medical History");
        System.out.println("8. View Patient Health Analytics");  // New option
        System.out.println("9. View Patient Vitals Graphs");     // New option
        System.out.println("10. Generate Patient Report");       // New option
        System.out.println("11. Start Video Consultation");
        System.out.println("12. Open Chat");
        System.out.println("13. Logout");
        System.out.println("0. Exit System");
        System.out.print("Choose an option: ");
        
        int choice = safeNextInt(null);
        
        switch (choice) {
            case 1: viewPendingAppointments(); break;
            case 2: approveAppointment(); break;
            case 3: cancelAppointment(); break;
            case 4: uploadVitals(); break;
            case 5: viewPatientVitals(); break;
            case 6: providePrescriptionAndFeedback(); break;
            case 7: viewPatientHistory(); break;
            case 8: viewHealthTrends(); break;           // New feature
            case 9: viewVitalsGraph(); break;            // New feature
            case 10: generateReport(); break;            // New feature
            case 11: startVideoConsultation(); break;
            case 12: openChat(); break;
            case 13: performLogout(); break;
            case 0: 
                System.out.println("Exiting RHMS System. Goodbye!");
                System.exit(0);
                break;
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid doctor dashboard option: " + choice + ". Please select a valid option.",
                    "Doctor dashboard selection"
                );
        }
    }
    
    // Register a new patient
    private static void registerPatient() {
        try {
            System.out.print("Enter Patient Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Address: ");
            String address = scanner.nextLine().trim();
            
            // Generate a unique patient ID
            int patientID = UserIDManager.getNextPatientID();
            
            // Create and add the patient
            Patient newPatient = new Patient(name, email, password, phone, address, patientID);
            patients.add(newPatient);
            
            System.out.println("Patient registered successfully with ID: " + patientID);
        } catch (Exception e) {
            System.out.println("Error registering patient: " + e.getMessage());
        }
    }
    
    // Register a new doctor
    private static void registerDoctor() {
        try {
            System.out.print("Enter Doctor Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Address: ");
            String address = scanner.nextLine().trim();
            String specialization = safeNextString("Enter Specialization: ", "Specialization", true);
            int experienceYears = getNumericInput("Enter Years of Experience: ", 0, 50);
            
            // Generate a unique doctor ID
            int doctorID = UserIDManager.getNextDoctorID();
            
            // Create and add the doctor
            Doctor newDoctor = new Doctor(name, email, password, phone, address, doctorID, 
                                         specialization, experienceYears);
            doctors.add(newDoctor);
            
            System.out.println("Doctor registered successfully with ID: " + doctorID);
        } catch (Exception e) {
            System.out.println("Error registering doctor: " + e.getMessage());
        }
    }
    
    // Register a new administrator
    private static void registerAdministrator() {
        try {
            System.out.print("Enter Administrator Name: ");
            String name = scanner.nextLine().trim();
            System.out.print("Enter Email: ");
            String email = scanner.nextLine().trim();
            System.out.print("Enter Password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter Phone: ");
            String phone = scanner.nextLine().trim();
            System.out.print("Enter Address: ");
            String address = scanner.nextLine().trim();
            
            // Generate a unique admin ID
            int adminID = UserIDManager.getNextAdminID();
            
            // Create and add the administrator
            Administrator newAdmin = new Administrator(name, email, password, phone, address, adminID);
            administrators.add(newAdmin);
            
            System.out.println("Administrator registered successfully with ID: " + adminID);
        } catch (Exception e) {
            System.out.println("Error registering administrator: " + e.getMessage());
        }
    }
    
    // View appointments for the logged-in patient
    private static void viewMyAppointments() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("Error: This feature is only available for patients.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        ArrayList<Appointment> patientAppointments = appointmentManager.getPatientAppointments(patient);
        
        System.out.println("\n=== My Appointments ===");
        if (patientAppointments.isEmpty()) {
            System.out.println("You have no appointments scheduled.");
        } else {
            for (Appointment appointment : patientAppointments) {
                System.out.println(appointment);
            }
        }
    }
    
    // View pending appointments for the logged-in doctor
    private static void viewPendingAppointments() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Error: This feature is only available for doctors.");
            return;
        }
        
        Doctor doctor = (Doctor) currentUser;
        ArrayList<Appointment> pendingAppointments = appointmentManager.getDoctorPendingAppointments(doctor);
        
        System.out.println("\n=== Pending Appointments ===");
        if (pendingAppointments.isEmpty()) {
            System.out.println("You have no pending appointments.");
        } else {
            for (Appointment appointment : pendingAppointments) {
                System.out.println(appointment);
            }
        }
    }
    
    // Approve a pending appointment
    private static void approveAppointment() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Only doctors can approve appointments.");
            return;
        }
        
        Doctor doctor = (Doctor) currentUser;
        ArrayList<Appointment> pendingAppointments = appointmentManager.getDoctorPendingAppointments(doctor);
        
        System.out.println("\n=== Approve Appointment ===");
        if (pendingAppointments.isEmpty()) {
            System.out.println("No pending appointments to approve.");
            return;
        }
        
        // Display pending appointments
        for (int i = 0; i < pendingAppointments.size(); i++) {
            System.out.println((i + 1) + ". " + pendingAppointments.get(i));
        }
        
        System.out.print("Enter appointment number to approve (0 to cancel): ");
        int selection = safeNextInt(null);
        
        if (selection == 0) {
            return;
        }
        
        if (selection < 1 || selection > pendingAppointments.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Appointment selectedAppointment = pendingAppointments.get(selection - 1);
        appointmentManager.approveAppointment(selectedAppointment);
        
        // Send notification to patient
        Patient patient = selectedAppointment.getPatient();
        String message = "Your appointment with Dr. " + doctor.getName() + 
                         " on " + selectedAppointment.getAppointmentDate() + 
                         " has been approved.";
        
        if (patient.getPhone() != null) {
            smsNotification.sendNotification(patient.getPhone(), message);
        }
        
        System.out.println("Appointment approved successfully!");
    }
    
    // Cancel an appointment
    private static void cancelAppointment() {
        ArrayList<Appointment> relevantAppointments = new ArrayList<>();
        
        System.out.println("\n=== Cancel Appointment ===");
        
        // Show different appointments based on user type
        if (currentUser instanceof Doctor) {
            Doctor doctor = (Doctor) currentUser;
            relevantAppointments = appointmentManager.getDoctorActiveAppointments(doctor);
        } else if (currentUser instanceof Patient) {
            Patient patient = (Patient) currentUser;
            relevantAppointments = appointmentManager.getPatientAppointments(patient);
            // Filter out cancelled appointments
            relevantAppointments.removeIf(appointment -> appointment.getStatus().equals("Cancelled"));
        } else {
            System.out.println("Only doctors and patients can cancel appointments.");
            return;
        }
        
        if (relevantAppointments.isEmpty()) {
            System.out.println("No active appointments to cancel.");
            return;
        }
        
        // Display active appointments
        for (int i = 0; i < relevantAppointments.size(); i++) {
            System.out.println((i + 1) + ". " + relevantAppointments.get(i));
        }
        
        System.out.print("Enter appointment number to cancel (0 to go back): ");
        int selection = safeNextInt(null);
        
        if (selection == 0) {
            return;
        }
        
        if (selection < 1 || selection > relevantAppointments.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Appointment selectedAppointment = relevantAppointments.get(selection - 1);
        appointmentManager.cancelAppointment(selectedAppointment);
        
        // Send notifications to relevant parties
        Doctor doctor = selectedAppointment.getDoctor();
        Patient patient = selectedAppointment.getPatient();
        
        String cancelledBy = (currentUser instanceof Doctor) ? "Dr. " + currentUser.getName() : patient.getName();
        String message = "Appointment on " + selectedAppointment.getAppointmentDate() + 
                         " has been cancelled by " + cancelledBy + ".";
        
        // Notify the other party
        if (currentUser instanceof Doctor && patient.getPhone() != null) {
            smsNotification.sendNotification(patient.getPhone(), message);
        } else if (currentUser instanceof Patient && doctor.getPhone() != null) {
            smsNotification.sendNotification(doctor.getPhone(), message);
        }
        
        System.out.println("Appointment cancelled successfully!");
    }
    
    // View all registered users (admin only)
    private static void viewAllUsers() {
        System.out.println("\n=== All Registered Users ===");
        
        System.out.println("\nAdministrators:");
        if (administrators.isEmpty()) {
            System.out.println("No administrators registered.");
        } else {
            for (Administrator admin : administrators) {
                System.out.println("- " + admin.getName() + " (ID: " + admin.getUserID() + ", Email: " + admin.getEmail() + ")");
            }
        }
        
        System.out.println("\nDoctors:");
        if (doctors.isEmpty()) {
            System.out.println("No doctors registered.");
        } else {
            for (Doctor doctor : doctors) {
                System.out.println("- Dr. " + doctor.getName() + " (ID: " + doctor.getUserID() + 
                                  ", Specialization: " + doctor.getSpecialization() + ")");
            }
        }
        
        System.out.println("\nPatients:");
        if (patients.isEmpty()) {
            System.out.println("No patients registered.");
        } else {
            for (Patient patient : patients) {
                System.out.println("- " + patient.getName() + " (ID: " + patient.getUserID() + ")");
            }
        }
    }
    
    // Safe input method for handling integer inputs with validation
    private static int safeNextInt(String prompt) {
        if (prompt != null) {
            System.out.print(prompt);
        }
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
            return safeNextInt(prompt);
        }
    }
    
    // Safe string input method with validation
    private static String safeNextString(String prompt, String fieldName, boolean required) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        
        if (required && input.isEmpty()) {
            System.out.println(fieldName + " cannot be empty. Please try again.");
            return safeNextString(prompt, fieldName, required);
        }
        return input;
    }
    
    // Get numeric input with range validation
    private static int getNumericInput(String prompt, int min, int max) {
        int value = safeNextInt(prompt);
        if (value < min || value > max) {
            System.out.println("Input must be between " + min + " and " + max + ". Please try again.");
            return getNumericInput(prompt, min, max);
        }
        return value;
    }
    
    // View all appointments
    private static void viewAllAppointments() {
        System.out.println("\n=== All Appointments ===");
        ArrayList<Appointment> allAppointments = appointmentManager.getAppointments();
        
        if (allAppointments.isEmpty()) {
            System.out.println("No appointments scheduled.");
        } else {
            for (Appointment appointment : allAppointments) {
                System.out.println(appointment);
            }
        }
    }
    
    // Upload vitals data (for doctors)
    private static void uploadVitals() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Only doctors can upload patient vital signs.");
            return;
        }
        
        try {
            // Show list of patients
            System.out.println("\n=== Upload Patient Vitals ===");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Select patient (enter number): ");
            int patientIndex = safeNextInt(null) - 1;
            
            if (patientIndex < 0 || patientIndex >= patients.size()) {
                System.out.println("Invalid patient selection.");
                return;
            }
            
            Patient selectedPatient = patients.get(patientIndex);
            
            System.out.println("\nEnter vital signs for " + selectedPatient.getName() + ":");
            
            // Get vital signs with validation
            double heartRate = getNumericInput("Enter heart rate (bpm): ", 40, 120);
            double oxygenLevel = getNumericInput("Enter oxygen level (%): ", 90, 100);
            double bloodPressure = getNumericInput("Enter blood pressure (systolic): ", 90, 140);
            double temperature = getNumericInput("Enter temperature (°C): ", 35, 40);
            
            // Create vital sign object
            VitalSign vitalSign = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
            
            // Add vital sign to the patient's database
            VitalsDatabase patientVitals = selectedPatient.getVitalsDatabase();
            if (patientVitals == null) {
                patientVitals = new VitalsDatabase(selectedPatient);
                // Assuming Patient has a field for vitals database
                if (selectedPatient.getMedicalHistory() == null) {
                    selectedPatient.setMedicalHistory(new MedicalHistory());
                }
                selectedPatient.getMedicalHistory().setVitalsDatabase(patientVitals);
            }
            
            patientVitals.addVitalRecord(vitalSign);
            System.out.println("Vital signs recorded successfully for " + selectedPatient.getName());
            
            // Check for abnormal vitals and trigger alert if needed
            emergencyAlert.checkVitals(selectedPatient, vitalSign);
            
        } catch (Exception e) {
            System.out.println("Error recording vital signs: " + e.getMessage());
        }
    }
    
    // View patient vitals (for both doctors and patients)
    private static void viewVitals() {
        try {
            if (currentUser instanceof Patient) {
                // Patient viewing their own vitals
                Patient patient = (Patient) currentUser;
                VitalsDatabase vitalsDB = patient.getVitalsDatabase();
                
                // If no vitals in patient object, check medical history
                if (vitalsDB == null && patient.getMedicalHistory() != null) {
                    vitalsDB = patient.getMedicalHistory().getVitalsDatabase();
                }
                
                if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
                    System.out.println("\nNo vital records found for you.");
                    return;
                }
                
                System.out.println("\n=== Your Vital Records ===");
                vitalsDB.displayAllVitals();
            }
            // Rest of the method remains unchanged
        } catch (Exception e) {
            System.out.println("Error viewing vitals: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Upload vitals data from CSV file
    private static void uploadVitalsFromCSV() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("This feature is only available for patients.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        
        try {
            System.out.println("\n=== Upload Vitals from CSV ===");
            System.out.print("Enter CSV filename or path: ");
            String filename = scanner.nextLine().trim();
            
            // Check if file exists
            java.io.File file = new java.io.File(filename);
            if (!file.exists()) {
                System.out.println("Error: File not found: " + filename);
                return;
            }
            
            // Process the CSV file
            CSVVitalsProcessor processor = new CSVVitalsProcessor();
            ArrayList<VitalSign> vitalSigns = processor.processCSVFile(filename);
            
            if (vitalSigns == null || vitalSigns.isEmpty()) {
                System.out.println("No vital signs found in the CSV file.");
                return;
            }
            
            // Initialize vitals database if needed
            VitalsDatabase vitalsDB;
            if (patient.getVitalsDatabase() == null) {
                System.out.println("Creating new vitals database for patient");
                vitalsDB = new VitalsDatabase(patient);
                patient.setVitalsDatabase(vitalsDB);
            } else {
                vitalsDB = patient.getVitalsDatabase();
                System.out.println("Using existing vitals database with " + 
                                  vitalsDB.getVitals().size() + " records");
            }
            
            // Add each vital sign to the patient's database
            int addedCount = 0;
            for (VitalSign vitalSign : vitalSigns) {
                vitalsDB.addVitalRecord(vitalSign);
                addedCount++;
                System.out.println("Recorded vital sign: " +
                    "Heart Rate=" + vitalSign.getHeartRate() + "bpm, " +
                    "Oxygen=" + vitalSign.getOxygenLevel() + "%, " +
                    "BP=" + vitalSign.getBloodPressure() + "mmHg, " +
                    "Temp=" + vitalSign.getTemperature() + "°C");
                
                // Check for abnormal values that might trigger an emergency
                emergencyAlert.checkVitals(patient, vitalSign);
            }
            
            // Ensure vitals database is set in both places consistently
            patient.setVitalsDatabase(vitalsDB);
            
            // If medical history exists, ensure it has the same reference 
            if (patient.getMedicalHistory() != null) {
                patient.getMedicalHistory().setVitalsDatabase(vitalsDB);
            }
            
            // Update the patient in the patients list
            for (int i = 0; i < patients.size(); i++) {
                if (patients.get(i).getUserID() == patient.getUserID()) {
                    System.out.println("Updating patient in system list with ID: " + patient.getUserID());
                    patients.set(i, patient);
                    break;
                }
            }
            
            System.out.println("\nSuccessfully uploaded " + addedCount + " vital records." +
                              "\nTotal records in database: " + vitalsDB.getVitals().size());
            
            // Save the data to ensure it persists
            saveApplicationData();
            
        } catch (Exception e) {
            System.out.println("Error processing file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void saveApplicationData() {
        try {
            System.out.println("Saving application data...");
            
            // Create data directory if it doesn't exist
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            // Save patients data
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("data/patients.dat"))) {
                out.writeObject(patients);
                System.out.println("Saved " + patients.size() + " patients");
            }
            
            // Save doctors data
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("data/doctors.dat"))) {
                out.writeObject(doctors);
                System.out.println("Saved " + doctors.size() + " doctors");
            }
            
            // Save administrators data
            try (ObjectOutputStream out = new ObjectOutputStream(
                    new FileOutputStream("data/admins.dat"))) {
                out.writeObject(administrators);
                System.out.println("Saved " + administrators.size() + " administrators");
            }
            
            System.out.println("Data saved successfully");
        } catch (Exception e) {
            System.out.println("Error saving data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // Trigger emergency alert
    private static void triggerEmergencyAlert() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("Only patients can use emergency alert features.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        
        System.out.print("Enter emergency reason: ");
        String emergencyReason = scanner.nextLine().trim();
        
        System.out.println("Emergency alert triggered! Help is on the way.");
        
        // Create panic button if not exists
        PanicButton panicButton = new PanicButton(patient);
        panicButton.triggerAlert(emergencyReason);
        
        // Let the emergency alert system handle it
        emergencyAlert.handleEmergency(patient, emergencyReason);
    }
    
    // Toggle panic button
    private static void togglePanicButton() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("Only patients can use panic button features.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        
        // Check if patient has a panic button
        if (patient.getPanicButton() == null) {
            patient.setPanicButton(new PanicButton(patient));
        }
        
        if (patient.getPanicButton().isActive()) {
            patient.getPanicButton().disable();
            System.out.println("Panic button has been disabled.");
        } else {
            patient.getPanicButton().enable();
            System.out.println("Panic button has been enabled.");
        }
    }
    
    // Join video consultation
    private static void joinVideoConsultation() {
        System.out.println("\n=== Join Video Consultation ===");
        System.out.println("1. Join with meeting ID");
        System.out.println("2. Start new meeting");
        System.out.print("Choose option: ");
        
        int choice = safeNextInt(null);
        
        if (choice == 1) {
            System.out.print("Enter meeting ID: ");
            String meetingId = scanner.nextLine().trim();
            System.out.println("Joining video call...");
            VideoCall.startVideoCall(meetingId);
        } else if (choice == 2) {
            String meetingId = VideoCall.generateMeetingId();
            System.out.println("Your meeting ID is: " + meetingId);
            System.out.println("Launching video call...");
            VideoCall.startVideoCall(meetingId);
        } else {
            System.out.println("Invalid choice.");
        }
    }
    
    // Start video consultation (doctor-side)
    private static void startVideoConsultation() {
        System.out.println("\n=== Start Video Consultation ===");
        String meetingId = VideoCall.generateMeetingId();
        System.out.println("Your meeting ID is: " + meetingId);
        System.out.println("Launching video call...");
        
        VideoCall.startVideoCall(meetingId);
        
        System.out.println("Please share this meeting ID with your patient.");
    }
    
    // Open chat interface
    private static void openChat() {
        System.out.println("\n=== Chat Interface ===");
        
        // Create chat client
        ChatClient chatClient = new ChatClient(currentUser);
        
        // Create a chat listener for real-time messages
        chatClient.addMessageListener(new ChatMessageListener() {
            @Override
            public void onNewMessage(ChatMessage message) {
                if (message.getSenderId() == currentUser.getUserID()) {
                    System.out.println("You: " + message.getContent());
                } else {
                    String senderName = "Unknown";
                    // Find sender name
                    if ("Patient".equals(userType)) {
                        for (Doctor doc : doctors) {
                            if (doc.getUserID() == message.getSenderId()) {
                                senderName = "Dr. " + doc.getName();
                                break;
                            }
                        }
                    } else {
                        for (Patient pat : patients) {
                            if (pat.getUserID() == message.getSenderId()) {
                                senderName = pat.getName();
                                break;
                            }
                        }
                    }
                    System.out.println(senderName + ": " + message.getContent());
                }
            }
        });
        
        User selectedUser = null;
        
        // Show users to chat with
        if ("Patient".equals(userType)) {
            System.out.println("Available doctors to chat with:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName());
            }
            
            System.out.print("Select doctor to chat with (enter number, or 0 to exit): ");
            int selection = safeNextInt(null);
            
            if (selection == 0) {
                return;
            }
            
            if (selection < 1 || selection > doctors.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            selectedUser = doctors.get(selection - 1);
        } else if ("Doctor".equals(userType)) {
            System.out.println("Available patients to chat with:");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Select patient to chat with (enter number, or 0 to exit): ");
            int selection = safeNextInt(null);
            
            if (selection == 0) {
                return;
            }
            
            if (selection < 1 || selection > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            selectedUser = patients.get(selection - 1);
        } else {
            System.out.println("Chat is only available for doctors and patients.");
            return;
        }
        
        if (selectedUser == null) {
            System.out.println("No user selected for chat.");
            return;
        }
        
        int recipientId = selectedUser.getUserID();
        
        // Mark existing messages as read
        chatClient.markMessagesAsRead(recipientId);
        
        // Show chat history
        List<ChatMessage> history = chatClient.getChatHistory(recipientId);
        System.out.println("\n=== Chat with " + selectedUser.getName() + " ===");
        
        if (history.isEmpty()) {
            System.out.println("No previous messages. Start a conversation!");
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (ChatMessage message : history) {
                String sender;
                if (message.getSenderId() == currentUser.getUserID()) {
                    sender = "You";
                } else {
                    sender = selectedUser.getName();
                }
                System.out.println("[" + dateFormat.format(message.getTimestamp()) + "] " + 
                                  sender + ": " + message.getContent());
            }
        }
        
        // Chat loop
        boolean chatting = true;
        while (chatting) {
            System.out.print("\nEnter message (or type 'exit' to return): ");
            String message = scanner.nextLine().trim();
            
            if (message.equalsIgnoreCase("exit")) {
                chatting = false;
            } else if (!message.isEmpty()) {
                chatClient.sendMessage(recipientId, message);
                
                // Check for any new messages from the other user
                List<ChatMessage> newMessages = chatClient.getUnreadMessages(recipientId);
                for (ChatMessage newMsg : newMessages) {
                    if (newMsg.getSenderId() == recipientId) {
                        System.out.println("[NEW] " + selectedUser.getName() + ": " + newMsg.getContent());
                        chatClient.markMessagesAsRead(recipientId);
                    }
                }
            }
        }
        
        System.out.println("Exiting chat...");
    }
    
    // Provide feedback to doctor
    private static void provideFeedback() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("Only patients can provide feedback to doctors.");
            return;
        }
        
        try {
            System.out.println("\n=== Provide Doctor Feedback ===");
            
            // Show available doctors
            if (doctors.isEmpty()) {
                System.out.println("No doctors available to provide feedback for.");
                return;
            }
            
            System.out.println("Select doctor to provide feedback:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName());
            }
            
            System.out.print("Enter choice (0 to cancel): ");
            int doctorChoice = safeNextInt(null);
            
            if (doctorChoice == 0) {
                return;
            }
            
            if (doctorChoice < 1 || doctorChoice > doctors.size()) {
                System.out.println("Invalid doctor selection.");
                return;
            }
            
            Doctor selectedDoctor = doctors.get(doctorChoice - 1);
            Patient patient = (Patient) currentUser;
            
            System.out.print("Enter your feedback: ");
            String feedback = scanner.nextLine().trim();
            
            // Create feedback record
            Feedback feedbackRecord = new Feedback(selectedDoctor, patient, feedback, null);
            
            // Add to patient's medical history
            if (patient.getMedicalHistory() == null) {
                patient.setMedicalHistory(new MedicalHistory());
            }
            
            patient.getMedicalHistory().addConsultation(feedbackRecord);
            
            // Record feedback via the doctor object
            selectedDoctor.provideFeedback(patient, feedback);
            
            System.out.println("Thank you for your feedback! It has been recorded.");
        } catch (Exception e) {
            System.out.println("Error providing feedback: " + e.getMessage());
        }
    }

    // Doctor views patient vitals
    private static void viewPatientVitals() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Only doctors can access this feature.");
            return;
        }
        
        try {
            System.out.println("\n=== View Patient Vitals ===");
            if (patients.isEmpty()) {
                System.out.println("No patients available in the system.");
                return;
            }
            
            // Show list of patients
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Select patient (enter number): ");
            int patientIndex = safeNextInt(null) - 1;
            
            if (patientIndex < 0 || patientIndex >= patients.size()) {
                System.out.println("Invalid patient selection.");
                return;
            }
            
            Patient selectedPatient = patients.get(patientIndex);
            VitalsDatabase vitalsDB = selectedPatient.getVitalsDatabase();
            
            if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
                System.out.println("No vital records found for " + selectedPatient.getName());
                return;
            }
            
            System.out.println("\n=== Vital Records for " + selectedPatient.getName() + " ===");
            vitalsDB.displayAllVitals();
            
        } catch (Exception e) {
            System.out.println("Error viewing patient vitals: " + e.getMessage());
        }
    }
    
    // Doctor provides feedback and prescribes medications
    private static void providePrescriptionAndFeedback() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Only doctors can access this feature.");
            return;
        }
        
        Doctor doctor = (Doctor) currentUser;
        
        try {
            System.out.println("\n=== Provide Prescription & Feedback ===");
            if (patients.isEmpty()) {
                System.out.println("No patients available in the system.");
                return;
            }
            
            // Show list of patients
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Select patient (enter number): ");
            int patientIndex = safeNextInt(null) - 1;
            
            if (patientIndex < 0 || patientIndex >= patients.size()) {
                System.out.println("Invalid patient selection.");
                return;
            }
            
            Patient selectedPatient = patients.get(patientIndex);
            
            // Get feedback from doctor
            System.out.print("Enter your medical feedback: ");
            String feedback = scanner.nextLine().trim();
            
            // Create prescription
            System.out.print("Do you want to prescribe medication? (Y/N): ");
            String prescribeChoice = scanner.nextLine().trim();
            
            Prescription prescription = null;
            if (prescribeChoice.equalsIgnoreCase("Y")) {
                System.out.print("Enter medication name: ");
                String medicationName = scanner.nextLine().trim();
                
                System.out.print("Enter dosage: ");
                String dosage = scanner.nextLine().trim();
                
                System.out.print("Enter schedule (e.g. twice daily): ");
                String schedule = scanner.nextLine().trim();
                
                prescription = new Prescription(medicationName, dosage, schedule);
            }
            
            // Create feedback object and store it
            Feedback feedbackRecord = new Feedback(doctor, selectedPatient, feedback, prescription);
            // Associate with patient's medical history
            MedicalHistory medicalHistory = selectedPatient.getMedicalHistory();
            if (medicalHistory == null) {
                medicalHistory = new MedicalHistory();
                selectedPatient.setMedicalHistory(medicalHistory);
            }
            medicalHistory.addConsultation(feedbackRecord);
            System.out.println("Feedback and prescription recorded successfully!");
        } catch (Exception e) {
            System.out.println("Error providing prescription and feedback: " + e.getMessage());
        }
    }
    
    // Doctor views patient's medical history
    private static void viewPatientHistory() {
        if (!(currentUser instanceof Doctor)) {
            System.out.println("Only doctors can access this feature.");
            return;
        }
        
        try {
            System.out.println("\n=== View Patient Medical History ===");
            if (patients.isEmpty()) {
                System.out.println("No patients available in the system.");
                return;
            }
            
            // Show list of patients
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Select patient (enter number): ");
            int patientIndex = safeNextInt(null) - 1;
            
            if (patientIndex < 0 || patientIndex >= patients.size()) {
                System.out.println("Invalid patient selection.");
                return;
            }
            
            Patient selectedPatient = patients.get(patientIndex);
            MedicalHistory medicalHistory = selectedPatient.getMedicalHistory();
            
            if (medicalHistory == null || medicalHistory.getPastConsultations().isEmpty()) {
                System.out.println("No medical history available for " + selectedPatient.getName());
                return;
            }
            
            System.out.println("\n=== Medical History for " + selectedPatient.getName() + " ===");
            medicalHistory.displayMedicalHistory();
            
        } catch (Exception e) {
            System.out.println("Error viewing patient history: " + e.getMessage());
        }
    }
    
    // Patient views doctor feedback and medication history
    private static void viewMyFeedbackAndMedications() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("Only patients can access this feature.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        MedicalHistory medicalHistory = patient.getMedicalHistory();
        if (medicalHistory == null || medicalHistory.getPastConsultations().isEmpty()) {
            System.out.println("\nNo feedback or medication records available.");
            return;
        }
        
        System.out.println("\n=== Your Medical Feedback & Medications ===");       
        medicalHistory.displayMedicalHistory();
    }
    
    // View and analyze health trends
    private static void viewHealthTrends() {
        Patient selectedPatient = null;
        
        if (currentUser instanceof Patient) {
            selectedPatient = (Patient) currentUser;
        } else if (currentUser instanceof Doctor) {
            System.out.println("\n=== Select Patient for Health Analytics ===");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Enter patient number (0 to cancel): ");
            int selection = safeNextInt(null);
            
            if (selection == 0) {
                return;
            }
            
            if (selection < 1 || selection > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            selectedPatient = patients.get(selection - 1);
        }
        
        // Get vitals database from medical history
        VitalsDatabase vitalsDB = null;
        if (selectedPatient.getMedicalHistory() != null) {
            vitalsDB = selectedPatient.getMedicalHistory().getVitalsDatabase();
        }
        
        // Check if patient has vitals data
        if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
            System.out.println("No vitals data available for " + selectedPatient.getName());
            return;
        }
        
        // Create analytics instance and analyze data
        HealthAnalytics analytics = new HealthAnalytics();
        AnalyticsResult result = analytics.analyzePatientData(selectedPatient);
        
        // Display analysis results
        result.displayResults();
    }
    
    // View vitals graphs
    private static void viewVitalsGraph() {
        if (!(currentUser instanceof Patient || currentUser instanceof Doctor)) {
            System.out.println("Error: This feature is only available for patients and doctors.");
            return;
        }
        
        Patient selectedPatient = null;
        
        // If current user is a patient, use their data
        if (currentUser instanceof Patient) {
            selectedPatient = (Patient) currentUser;
        }
        // If current user is a doctor, select a patient
        else if (currentUser instanceof Doctor) {
            System.out.println("\n=== Select Patient ===");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Enter patient number (0 to cancel): ");
            int selection = safeNextInt(null);
            
            if (selection == 0) {
                return;
            }
            
            if (selection < 1 || selection > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            selectedPatient = patients.get(selection - 1);
        }
        
        // Check if patient has vitals data
        if (selectedPatient.getVitalsDatabase() == null || 
            selectedPatient.getVitalsDatabase().getVitals().isEmpty()) {
            System.out.println("No vitals data available for " + selectedPatient.getName());
            return;
        }
        
        // Menu to select which vital sign to graph
        System.out.println("\n=== Select Vital Sign to Graph ===");
        System.out.println("1. Heart Rate");
        System.out.println("2. Oxygen Level");
        System.out.println("3. Blood Pressure");
        System.out.println("4. Temperature");
        System.out.println("0. Cancel");
        
        System.out.print("Enter selection: ");
        int vitalChoice = safeNextInt(null);
        
        if (vitalChoice == 0) {
            return;
        }
        
        String vitalType = "";
        switch (vitalChoice) {
            case 1: vitalType = "heartrate"; break;
            case 2: vitalType = "oxygen"; break;
            case 3: vitalType = "bloodpressure"; break;
            case 4: vitalType = "temperature"; break;
            default:
                System.out.println("Invalid selection.");
                return;
        }
        
        // Display graph using visualizer
        HealthDataVisualizer visualizer = new HealthDataVisualizer();
        visualizer.displayVitalSignGraph(selectedPatient, vitalType);
        
        System.out.println("Graph window opened. Close it to return to the menu.");
    }
    
    // Schedule appointment for admin
    private static void scheduleAppointmentAdmin() {
        System.out.println("\n=== Schedule Appointment (Admin) ===");
        scheduleAppointmentImpl();
    }
    
    // Schedule appointment for patient
    private static void scheduleAppointmentPatient() {
        System.out.println("\n=== Schedule Appointment ===");
        scheduleAppointmentImpl();
    }
    
    // Common appointment scheduling implementation
    private static void scheduleAppointmentImpl() {
        try {
            // First, select a patient
            Patient selectedPatient = null;
            
            if (currentUser instanceof Patient) {
                selectedPatient = (Patient) currentUser;
            } else {
                // For admin or doctor, show list of patients
                if (patients.isEmpty()) {
                    System.out.println("No patients available to schedule appointments for.");
                    return;
                }
                
                System.out.println("Select patient:");
                for (int i = 0; i < patients.size(); i++) {
                    System.out.println((i + 1) + ". " + patients.get(i).getName());
                }
                
                System.out.print("Enter patient number (0 to cancel): ");
                int patientSelection = safeNextInt(null);
                
                if (patientSelection == 0) {
                    return;
                }
                
                if (patientSelection < 1 || patientSelection > patients.size()) {
                    System.out.println("Invalid selection.");
                    return;
                }
                
                selectedPatient = patients.get(patientSelection - 1);
            }
            
            // Now select a doctor
            if (doctors.isEmpty()) {
                System.out.println("No doctors available to schedule appointments with.");
                return;
            }
            
            System.out.println("Select doctor:");
            for (int i = 0; i < doctors.size(); i++) {
                System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + 
                                  " (" + doctors.get(i).getSpecialization() + ")");
            }
            
            System.out.print("Enter doctor number (0 to cancel): ");
            int doctorSelection = safeNextInt(null);
            
            if (doctorSelection == 0) {
                return;
            }
            
            if (doctorSelection < 1 || doctorSelection > doctors.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            Doctor selectedDoctor = doctors.get(doctorSelection - 1);
            
            // Get appointment details
            System.out.print("Enter appointment date (YYYY-MM-DD): ");
            String dateStr = scanner.nextLine().trim();
            
            System.out.print("Enter appointment time (HH:MM): ");
            String timeStr = scanner.nextLine().trim();
            
            System.out.print("Enter reason for visit: ");
            String reason = scanner.nextLine().trim();
            
            // Parse date string to Date object
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date appointmentDate = dateFormat.parse(dateStr + " " + timeStr);
            
            // Create appointment and store it
            appointmentManager.requestAppointment(appointmentDate, selectedDoctor, selectedPatient);
            
            System.out.println("Appointment scheduled successfully! Awaiting doctor approval.");
            
        } catch (ParseException e) {
            System.out.println("Error: Invalid date or time format. Please use YYYY-MM-DD and HH:MM formats.");
        } catch (Exception e) {
            System.out.println("Error scheduling appointment: " + e.getMessage());
        }
    }
    
    // Show notification menu
    private static void showNotificationMenu() {
        System.out.println("\n=== Send Notifications ===");
        System.out.println("1. Send SMS to Patient");
        System.out.println("2. Send SMS to Doctor");
        System.out.println("3. Send Email to Patient");    // New option
        System.out.println("4. Send Email to Doctor");     // New option
        System.out.println("5. Send Bulk Email");          // New option
        System.out.println("0. Back to Dashboard");
        
        System.out.print("Choose an option: ");
        int choice = safeNextInt(null);
        
        switch (choice) {
            case 1:
                sendSMSToUser(patients);
                break;
            case 2:
                sendSMSToUser(doctors);
                break;
            case 3:
                sendEmailToUser(patients);
                break;
            case 4:
                sendEmailToUser(doctors);
                break;
            case 5:
                sendBulkEmail();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid option selected.");
        }
    }
    
    // Send SMS to a selected user
    private static void sendSMSToUser(ArrayList<? extends User> users) {
        if (users.isEmpty()) {
            System.out.println("No users available to send notifications to.");
            return;
        }
        
        System.out.println("Select recipient:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName());
        }
        
        System.out.print("Enter selection (0 to cancel): ");
        int selection = safeNextInt(null);
        
        if (selection == 0) {
            return;
        }
        
        if (selection < 1 || selection > users.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        User recipient = users.get(selection - 1);
        
        System.out.print("Enter message to send: ");
        String message = scanner.nextLine().trim();
        
        if (recipient.getPhone() != null) {
            smsNotification.sendNotification(recipient.getPhone(), message);
            System.out.println("SMS notification sent to " + recipient.getName());
        } else {
            System.out.println("Unable to send SMS: No phone number available for " + recipient.getName());
        }
    }
    
    private static void sendEmailToUser(ArrayList<? extends User> users) {
        if (users.isEmpty()) {
            System.out.println("No users available to send emails to.");
            return;
        }
        
        System.out.println("Select recipient:");
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getName() + 
                              " (" + users.get(i).getEmail() + ")");
        }
        
        System.out.print("Enter selection (0 to cancel): ");
        int selection = safeNextInt(null);
        
        if (selection == 0) return;
        
        if (selection < 1 || selection > users.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        User recipient = users.get(selection - 1);
        
        System.out.print("Enter email subject: ");
        String subject = scanner.nextLine().trim();
        
        System.out.println("Enter email message (type END on a new line to finish):");
        StringBuilder message = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            message.append(line).append("\n");
        }
        
        if (recipient.getEmail() != null && !recipient.getEmail().isEmpty()) {
            emailNotification.sendNotification(recipient.getEmail(), subject, message.toString());
            System.out.println("Email sent successfully to " + recipient.getName());
        } else {
            System.out.println("Unable to send email: No valid email address for " + recipient.getName());
        }
    }
    
    private static void sendBulkEmail() {
        System.out.println("\n=== Send Bulk Email ===");
        System.out.println("1. All Patients");
        System.out.println("2. All Doctors");
        System.out.println("3. All Users");
        System.out.println("0. Cancel");
        
        System.out.print("Select recipient group: ");
        int selection = safeNextInt(null);
        
        if (selection == 0) return;
        
        System.out.print("Enter email subject: ");
        String subject = scanner.nextLine().trim();
        
        System.out.println("Enter email message (type END on a new line to finish):");
        StringBuilder message = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            message.append(line).append("\n");
        }
        
        String emailContent = "Subject: " + subject + "\n\n" + message.toString();
        int sentCount = 0;
        
        switch (selection) {
            case 1: // All Patients
                for (Patient patient : patients) {
                    if (sendEmailToRecipient(patient, emailContent)) sentCount++;
                }
                break;
            case 2: // All Doctors
                for (Doctor doctor : doctors) {
                    if (sendEmailToRecipient(doctor, emailContent)) sentCount++;
                }
                break;
            case 3: // All Users
                for (User user : getAllUsers()) {
                    if (sendEmailToRecipient(user, emailContent)) sentCount++;
                }
                break;
            default:
                System.out.println("Invalid selection.");
                return;
        }
        
        System.out.println("Bulk email sent to " + sentCount + " recipients.");
    }
    
    private static boolean sendEmailToRecipient(User user, String content) {
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            // Parse content to extract subject and message
            String subject = "";
            String message = content;
            
            // Check if content contains a subject line
            if (content.startsWith("Subject:")) {
                int endOfSubject = content.indexOf("\n\n");
                if (endOfSubject != -1) {
                    subject = content.substring("Subject:".length(), endOfSubject).trim();
                    message = content.substring(endOfSubject + 2);
                }
            }
            
            emailNotification.sendNotification(user.getEmail(), subject, message);
            return true;
        }
        return false;
    }
    
    private static ArrayList<User> getAllUsers() {
        ArrayList<User> allUsers = new ArrayList<>();
        allUsers.addAll(patients);
        allUsers.addAll(doctors);
        allUsers.addAll(administrators);
        return allUsers;
    }
    
    // Generate downloadable report
    private static void generateReport() {
        if (!(currentUser instanceof Patient || currentUser instanceof Doctor)) {
            System.out.println("Error: This feature is only available for patients and doctors.");
            return;
        }
        
        Patient selectedPatient = null;
        
        // If current user is a patient, use their data
        if (currentUser instanceof Patient) {
            selectedPatient = (Patient) currentUser;
        }
        // If current user is a doctor, select a patient
        else if (currentUser instanceof Doctor) {
            System.out.println("\n=== Select Patient ===");
            for (int i = 0; i < patients.size(); i++) {
                System.out.println((i + 1) + ". " + patients.get(i).getName());
            }
            
            System.out.print("Enter patient number (0 to cancel): ");
            int selection = safeNextInt(null);
            
            if (selection == 0) {
                return;
            }
            
            if (selection < 1 || selection > patients.size()) {
                System.out.println("Invalid selection.");
                return;
            }
            
            selectedPatient = patients.get(selection - 1);
        }
        
        // Ask for report storage location
        System.out.println("\n=== Report Storage Location ===");
        System.out.println("1. Use default directory (d:\\rhms\\reports)");
        System.out.println("2. Specify custom directory");
        System.out.print("Enter choice: ");
        int dirChoice = safeNextInt(null);
        
        // Create the ReportsGenerator with the appropriate directory
        ReportsGenerator generator;
        if (dirChoice == 2) {
            System.out.print("Enter custom directory path: ");
            String customDir = scanner.nextLine().trim();
            generator = new ReportsGenerator(customDir);
        } else {
            generator = new ReportsGenerator(); // Use default
        }
        
        // Ask for report type
        System.out.println("\n=== Report Options ===");
        System.out.println("1. Basic Report (Vitals & Medical History)");
        System.out.println("2. Comprehensive Report (with Analytics)");
        System.out.println("0. Cancel");
        
        System.out.print("Enter selection: ");
        int reportType = safeNextInt(null);
        
        if (reportType == 0) {
            return;
        }
        
        if (reportType != 1 && reportType != 2) {
            System.out.println("Invalid selection.");
            return;
        }
        
        // Generate the report
        boolean includeAnalytics = (reportType == 2);
        
        String reportPath = generator.generatePatientReport(selectedPatient, includeAnalytics);
        
        if (reportPath != null) {
            System.out.println("Report generated successfully: " + reportPath);
            
            // Offer to open the directory
            System.out.print("Would you like to open the directory? (Y/N): ");
            String openChoice = scanner.nextLine().trim();
            
            if (openChoice.equalsIgnoreCase("Y")) {
                try {
                    File reportFile = new File(reportPath);
                    File parentDir = reportFile.getParentFile();
                    
                    // Use Windows-specific command to open the folder
                    Runtime.getRuntime().exec("explorer.exe \"" + parentDir.getAbsolutePath() + "\"");
                    System.out.println("Opening folder: " + parentDir.getAbsolutePath());
                } catch (Exception e) {
                    System.out.println("Couldn't open directory: " + e.getMessage());
                }
            }
        } else {
            System.out.println("Failed to generate report.");
        }
    }

    // Email my doctor feature
    private static void emailMyDoctor() {
        if (!(currentUser instanceof Patient)) {
            System.out.println("This feature is only available for patients.");
            return;
        }
        
        Patient patient = (Patient) currentUser;
        
        System.out.println("\n=== Email Your Doctor ===");
        
        if (doctors.isEmpty()) {
            System.out.println("No doctors available to email.");
            return;
        }
        
        // Show available doctors
        for (int i = 0; i < doctors.size(); i++) {
            System.out.println((i + 1) + ". Dr. " + doctors.get(i).getName() + 
                              " (" + doctors.get(i).getSpecialization() + ")");
        }
        
        System.out.print("Select doctor (0 to cancel): ");
        int selection = safeNextInt(null);
        
        if (selection == 0) return;
        if (selection < 1 || selection > doctors.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        
        Doctor selectedDoctor = doctors.get(selection - 1);
        
        System.out.print("Enter email subject: ");
        String subject = scanner.nextLine().trim();
        
        System.out.println("Enter your message (type END on a new line to finish):");
        StringBuilder message = new StringBuilder();
        String line;
        while (!(line = scanner.nextLine()).equals("END")) {
            message.append(line).append("\n");
        }
        
        // Add patient details footer
        message.append("\n-------------------\n");
        message.append("From Patient: ").append(patient.getName())
              .append(" (ID: ").append(patient.getUserID()).append(")\n");
        message.append("Contact: ").append(patient.getPhone());
        
        // Send email notification
        try {
            emailNotification.sendNotification(selectedDoctor.getEmail(), subject, message.toString());
            System.out.println("Email sent successfully to Dr. " + selectedDoctor.getName());
        } catch (Exception e) {
            System.out.println("Failed to send email: " + e.getMessage());
        }
    }
}
