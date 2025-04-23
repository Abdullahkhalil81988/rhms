package com.rhms;

import com.rhms.userManagement.*;
import com.rhms.appointmentScheduling.*;
import com.rhms.healthDataHandling.*;
import com.rhms.doctorPatientInteraction.*;
import com.rhms.emergencyAlert.*;
import com.rhms.notifications.ReminderService;
import com.rhms.notifications.SMSNotification;
import com.rhms.notifications.EmailNotification;
import com.rhms.exceptions.RHMSException;
import com.rhms.exceptions.ErrorHandler;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TESTAPP {
    private static ArrayList<Patient> patients = new ArrayList<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static AppointmentManager appointmentManager = new AppointmentManager();
    private static EmergencyAlert emergencyAlert = new EmergencyAlert();
    private static Scanner scanner = new Scanner(System.in);
    private static String userType = ""; // Store user type
    private static ChatServer chatServer = new ChatServer();
    private static Map<String, ChatClient> chatClients = new HashMap<>();
    private static ReminderService reminderService = new ReminderService();
    private static SMSNotification smsNotification = new SMSNotification();
    private static EmailNotification emailNotification = new EmailNotification();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            try {
                // User Type Selection
                System.out.println("\n===== RHMS User Type Selection =====");
                System.out.println("1. Patient");
                System.out.println("2. Doctor");
                System.out.println("3. Admin");
                System.out.println("0. Exit System");
                System.out.print("Choose your user type: ");

                int userTypeChoice = safeNextInt(null);

                if (userTypeChoice == 0) {
                    System.out.println("Exiting RHMS System. Goodbye!");
                    running = false;
                    continue;
                }

                switch (userTypeChoice) {
                    case 1: userType = "Patient"; break;
                    case 2: userType = "Doctor"; break;
                    case 3: userType = "Admin"; break;
                    default:
                        throw new RHMSException(
                            RHMSException.ErrorCode.INVALID_INPUT,
                            "Invalid user type selection: " + userTypeChoice + ". Please choose options 0-3.",
                            "User type selection"
                        );
                }

                // Sub-menu loop
                boolean stayInSubmenu = true;
                while (stayInSubmenu && running) {
                    try {
                        if ("Admin".equals(userType)) {
                            showAdminMenu();
                            int choice = safeNextInt("Choose an option: ");
                            stayInSubmenu = handleAdminMenu(choice);
                        } else if ("Patient".equals(userType)) {
                            showPatientMenu();
                            int choice = safeNextInt("Choose an option: ");
                            if (!handlePatientMenu(choice)) {
                                running = false;
                                break;
                            }
                            stayInSubmenu = (choice != 9 && choice != 0);
                        } else if ("Doctor".equals(userType)) {
                            showDoctorMenu();
                            int choice = safeNextInt("Choose an option: ");
                            if (!handleDoctorMenu(choice)) {
                                running = false;
                                break;
                            }
                            stayInSubmenu = (choice != 9 && choice != 0);
                        }
                    } catch (RHMSException e) {
                        ErrorHandler.handleException(e);
                    } catch (Exception e) {
                        ErrorHandler.handleGeneralException(e);
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

    private static boolean handleAdminMenu(int choice) throws RHMSException {
        switch (choice) {
            case 1: registerPatient(); break;
            case 2: registerDoctor(); break;
            case 3: scheduleAppointment(); break;
            case 4: showNotificationMenu(); break;
            case 5: viewAllAppointments(); break;
            case 0: return false;
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid admin menu option: " + choice + ". Please select options 0-5.",
                    "Admin menu selection"
                );
        }
        return true;
    }

    private static boolean handlePatientMenu(int choice) throws RHMSException {
        switch (choice) {
            case 1: scheduleAppointment(); break;
            case 2: viewVitals(); break;
            case 3: provideFeedback(); break;
            case 4: triggerEmergencyAlert(); break;
            case 5: togglePanicButton(); break;
            case 6: joinVideoConsultation(); break;
            case 7: openChat(); break;
            case 9: return true;  // Return to user type selection
            case 0: return false; // Exit system
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid patient menu option: " + choice + ". Please select options 0-7 or 9.",
                    "Patient menu selection"
                );
        }
        return true;
    }

    private static boolean handleDoctorMenu(int choice) throws RHMSException {
        switch (choice) {
            case 1: approveAppointment(); break;
            case 2: cancelAppointment(); break;
            case 3: uploadVitals(); break;
            case 4: viewVitals(); break;
            case 5: startVideoConsultation(); break;
            case 6: openChat(); break;
            case 9: return true;  // Return to user type selection
            case 0: return false; // Exit system
            default:
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_INPUT,
                    "Invalid doctor menu option: " + choice + ". Please select options 0-6 or 9.",
                    "Doctor menu selection"
                );
        }
        return true;
    }

    private static int safeNextInt(String prompt) throws RHMSException {
        try {
            if (prompt != null) {
                System.out.print(prompt);
            }
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                throw new RHMSException(
                    RHMSException.ErrorCode.EMPTY_INPUT_ERROR,
                    "Input cannot be empty",
                    "Reading numeric input"
                );
            }

            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new RHMSException(
                RHMSException.ErrorCode.NUMERIC_INPUT_ERROR,
                "Invalid number format. Please enter a valid number.",
                "Reading numeric input"
            );
        }
    }

    private static void showAdminMenu() {
        System.out.println("\n===== RHMS Admin Menu =====");
        System.out.println("1. Register Patient");
        System.out.println("2. Register Doctor");
        System.out.println("3. Schedule Appointment");
        System.out.println("4. Send Notifications");
        System.out.println("5. View All Appointments");
        System.out.println("0. Back to User Selection");
    }

    private static void showPatientMenu() {
        System.out.println("1. Schedule an Appointment");
        System.out.println("2. View Patient Vitals");
        System.out.println("3. Provide Doctor Feedback");
        System.out.println("4. Trigger Emergency Alert");
        System.out.println("5. Enable/Disable Panic Button");
        System.out.println("6. Join Video Consultation");
        System.out.println("7. Open Chat");
        System.out.println("9. Back to User Selection");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void showDoctorMenu() {
        System.out.println("1. Approve Appointment");
        System.out.println("2. Cancel Appointment");
        System.out.println("3. Upload Vital Signs");
        System.out.println("4. View Patient Vitals");
        System.out.println("5. Start Video Consultation");
        System.out.println("6. Open Chat");
        System.out.println("9. Back to User Selection");
        System.out.println("0. Exit");
        System.out.print("Choose an option: ");
    }

    private static void registerPatient() {
        try {
            String name = safeNextString("Enter Patient Name: ", "Patient name", true);
            String email = safeNextEmail("Enter Email: ");
            String password = safeNextString("Enter Password: ", "Password", true);
            String phone = safeNextPhone("Enter Phone: ");
            String address = safeNextString("Enter Address: ", "Address", true);
            int userID = getNumericInput("Enter User ID: ", 1, 99999);

            Patient patient = new Patient(name, email, password, phone, address, userID);
            patients.add(patient);
            System.out.println("Patient " + name + " registered successfully.");
        } catch (Exception e) {
            System.out.println("Error registering patient: " + e.getMessage());
        }
    }

    private static void registerDoctor() {
        try {
            String name = safeNextString("Enter Doctor Name: ", "Doctor name", true);
            String email = safeNextEmail("Enter Email: ");
            String password = safeNextString("Enter Password: ", "Password", true);
            String phone = safeNextPhone("Enter Phone: ");
            String address = safeNextString("Enter Address: ", "Address", true);
            int userID = getNumericInput("Enter User ID: ", 1, 99999);
            String specialization = safeNextString("Enter Specialization: ", "Specialization", true);
            int experienceYears = getNumericInput("Enter Years of Experience: ", 0, 70);

            Doctor doctor = new Doctor(name, email, password, phone, address, userID, specialization, experienceYears);
            doctors.add(doctor);
            System.out.println("Doctor " + name + " registered successfully.");
        } catch (Exception e) {
            System.out.println("Error registering doctor: " + e.getMessage());
        }
    }

    private static void scheduleAppointment() {
        try {
            System.out.print("Enter Patient Name: ");
            String patientName = scanner.nextLine();
            Patient patient = findPatient(patientName);
            if (patient == null) {
                throw new RHMSException(
                    RHMSException.ErrorCode.PATIENT_NOT_FOUND,
                    "Patient not found: " + patientName
                );
            }

            System.out.print("Enter Doctor Name: ");
            String doctorName = scanner.nextLine();
            Doctor doctor = findDoctor(doctorName);
            if (doctor == null) {
                throw new RHMSException(
                    RHMSException.ErrorCode.DOCTOR_NOT_FOUND,
                    "Doctor not found: " + doctorName
                );
            }

            System.out.print("Enter Appointment Date (yyyy-MM-dd): ");
            String dateStr = scanner.nextLine();
            ErrorHandler.validateAppointmentDate(dateStr);
            Date appointmentDate = java.sql.Date.valueOf(dateStr);

            String appointmentDetails = "Appointment on " + appointmentDate.toString();
            patient.scheduleAppointment(appointmentDetails);
            doctor.manageAppointment(appointmentDetails);

            Appointment appointment = new Appointment(appointmentDate, doctor, patient, "Pending");
            appointmentManager.getAppointments().add(appointment);

            // Send confirmation notifications
            String subject = "Appointment Confirmation";
            String message = String.format("Your appointment with Dr. %s is scheduled for %s", 
                doctor.getName(), appointmentDate.toString());
            
            emailNotification.sendNotification(patient.getEmail(), subject, message);
            smsNotification.sendNotification(patient.getPhone(), subject, message);
            
            System.out.println("Appointment scheduled successfully!");
        } catch (RHMSException e) {
            ErrorHandler.handleException(e);
        }
    }

    private static void approveAppointment() {
        if (appointmentManager.getAppointments().isEmpty()) {
            System.out.println("No appointments found.");
            return;
        }
        appointmentManager.getAppointments().get(0).setStatus("Approved");
        System.out.println("Appointment Approved!");
    }

    private static void cancelAppointment() {
        if (appointmentManager.getAppointments().isEmpty()) {
            System.out.println("No appointments to cancel.");
            return;
        }
        appointmentManager.getAppointments().get(0).setStatus("Cancelled");
        System.out.println("Appointment Cancelled!");
    }

    private static void uploadVitals() {
        try {
            String name = safeNextString("Enter Patient Name: ", "Patient name", true);
            Patient patient = findPatient(name);
            if (patient == null) {
                System.out.println("Patient not found!");
                return;
            }

            double heartRate = Double.parseDouble(safeNextString("Enter Heart Rate: ", "Heart Rate", true));
            double oxygenLevel = Double.parseDouble(safeNextString("Enter Oxygen Level: ", "Oxygen Level", true));
            double bloodPressure = Double.parseDouble(safeNextString("Enter Blood Pressure: ", "Blood Pressure", true));
            double temperature = Double.parseDouble(safeNextString("Enter Temperature: ", "Temperature", true));

            // Create vital sign record
            VitalSign vitals = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
            
            // Check for emergency conditions and store vitals
            emergencyAlert.checkVitals(patient, vitals);
            String vitalsRecord = String.format("HR: %.1f, O2: %.1f%%, BP: %.1f, Temp: %.1f°C", 
                heartRate, oxygenLevel, bloodPressure, temperature);
            patient.uploadMedicalRecord(vitalsRecord);

            System.out.println("Vitals uploaded successfully!");
        } catch (NumberFormatException e) {
            System.out.println("Error: Please enter valid numbers for vital signs.");
        } catch (Exception e) {
            System.out.println("Error uploading vitals: " + e.getMessage());
        }
    }

    private static void viewVitals() {
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        Patient patient = findPatient(name);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.println("\nMedical Records:");
        for (String record : patient.getDoctorFeedback()) {
            System.out.println(record);
        }
    }

    private static void provideFeedback() {
        System.out.print("Enter Doctor Name: ");
        String doctorName = scanner.nextLine();
        Doctor doctor = findDoctor(doctorName);
        if (doctor == null) {
            System.out.println("Doctor not found!");
            return;
        }

        System.out.print("Enter Patient Name: ");
        String patientName = scanner.nextLine();
        Patient patient = findPatient(patientName);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Enter Feedback: ");
        String feedback = scanner.nextLine();

        doctor.provideFeedback(patient, feedback);
        System.out.println("Feedback recorded successfully!");
    }

    private static void triggerEmergencyAlert() {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        Patient patient = findPatient(name);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Enter emergency reason: ");
        String reason = scanner.nextLine();

        PanicButton panicButton = new PanicButton(patient);
        panicButton.triggerAlert(reason);
    }

    private static void togglePanicButton() {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        Patient patient = findPatient(name);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.println("\nCurrent panic button status: " + patient.getPanicButton().getStatus());
        System.out.println("1. Enable Panic Button");
        System.out.println("2. Disable Panic Button");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        
        switch (choice) {
            case 1:
                patient.enablePanicButton();
                break;
            case 2:
                patient.disablePanicButton();
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void startVideoConsultation() {
        System.out.print("Enter patient name: ");
        String patientName = scanner.nextLine();
        Patient patient = findPatient(patientName);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        String meetingId = VideoCall.generateMeetingId();
        System.out.println("Starting video consultation...");
        System.out.println("Meeting ID: " + meetingId);
        
        VideoCall.startVideoCall(meetingId);
    }

    private static void joinVideoConsultation() {
        System.out.print("Enter meeting ID: ");
        String meetingId = scanner.nextLine();
        
        VideoCall.startVideoCall(meetingId);
    }

    private static void openChat() {
        System.out.print("Enter the name of user to chat with: ");
        String otherUser = scanner.nextLine();
        
        if (userType.equals("Doctor")) {
            Patient patient = findPatient(otherUser);
            if (patient == null) {
                System.out.println("Patient not found!");
                return;
            }
            otherUser = patient.getName();
        } else {
            Doctor doctor = findDoctor(otherUser);
            if (doctor == null) {
                System.out.println("Doctor not found!");
                return;
            }
            otherUser = doctor.getName();
        }

        ChatClient chatClient = chatClients.computeIfAbsent(
            userType.equals("Doctor") ? otherUser : otherUser,
            name -> new ChatClient(
                userType.equals("Doctor") ? findDoctor(userType) : findPatient(userType),
                chatServer
            )
        );

        while (true) {
            System.out.println("\n1. Send Message");
            System.out.println("2. View Chat History");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            
            switch (choice) {
                case 1:
                    System.out.print("Enter message: ");
                    String message = scanner.nextLine();
                    chatClient.sendMessage(otherUser, message);
                    break;
                case 2:
                    chatClient.displayChat(otherUser);
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static Patient findPatient(String name) {
        for (Patient patient : patients) {
            if (patient.getName().equalsIgnoreCase(name)) {
                return patient;
            }
        }
        return null;
    }

    private static Doctor findDoctor(String name) {
        for (Doctor doctor : doctors) {
            if (doctor.getName().equalsIgnoreCase(name)) {
                return doctor;
            }
        }
        return null;
    }

    private static void sendNotification() {
        System.out.println("\n=== Send Notification ===");
        System.out.println("1. Send Appointment Reminder");
        System.out.println("2. Send Medication Reminder");
        System.out.println("3. Send Custom Message");
        System.out.println("0. Back");
        System.out.print("Choose an option: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Enter patient name: ");
        String patientName = scanner.nextLine();
        Patient patient = findPatient(patientName);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        switch (choice) {
            case 1:
                sendAppointmentReminder(patient);
                break;
            case 2:
                sendMedicationReminder(patient);
                break;
            case 3:
                sendCustomMessage(patient);
                break;
            case 0:
                return;
            default:
                System.out.println("Invalid choice!");
        }
    }

    private static void sendAppointmentReminder(Patient patient) {
        System.out.print("Enter appointment date (e.g., tomorrow 2:30 PM): ");
        String appointmentTime = scanner.nextLine();
        
        String subject = "Appointment Reminder";
        String message = String.format("Dear %s, you have an appointment scheduled for %s.", 
            patient.getName(), appointmentTime);
        
        smsNotification.sendNotification(patient.getPhone(), subject, message);
        reminderService.sendImmediateReminder(patient, subject, message);
    }

    private static void sendMedicationReminder(Patient patient) {
        System.out.print("Enter medication name: ");
        String medication = scanner.nextLine();
        System.out.print("Enter schedule (e.g., twice daily): ");
        String schedule = scanner.nextLine();
        
        reminderService.scheduleMedicationReminder(patient, medication, schedule);
        System.out.println("Medication reminder set successfully!");
    }

    private static void sendCustomMessage(Patient patient) {
        System.out.print("Enter message subject: ");
        String subject = scanner.nextLine();
        System.out.print("Enter message content: ");
        String message = scanner.nextLine();
        
        smsNotification.sendNotification(patient.getPhone(), subject, message);
        System.out.println("Custom message sent successfully!");
    }

    private static void showNotificationMenu() {
        while (true) {
            System.out.println("\n===== Notification Menu =====");
            System.out.println("1. Send Email");
            System.out.println("2. Send SMS");
            System.out.println("3. Send Both Email and SMS");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 0) return;

            System.out.print("Enter patient name: ");
            String patientName = scanner.nextLine();
            Patient patient = findPatient(patientName);
            
            if (patient == null) {
                System.out.println("Patient not found!");
                continue;
            }

            System.out.print("Enter subject: ");
            String subject = scanner.nextLine();
            System.out.print("Enter message: ");
            String message = scanner.nextLine();

            switch (choice) {
                case 1:
                    emailNotification.sendNotification(patient.getEmail(), subject, message);
                    break;
                case 2:
                    smsNotification.sendNotification(patient.getPhone(), subject, message);
                    break;
                case 3:
                    emailNotification.sendNotification(patient.getEmail(), subject, message);
                    smsNotification.sendNotification(patient.getPhone(), subject, message);
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
    }

    private static void viewAllAppointments() {
        System.out.println("\n=== All Appointments ===");
        for (Appointment appointment : appointmentManager.getAppointments()) {
            System.out.println(appointment);
        }
    }

    /**
     * Safely captures a string input with validation
     * @param prompt Message to display to the user
     * @param fieldName Name of the field (for error messages)
     * @param required Whether the field is required
     * @return The validated string input
     * @throws RHMSException if validation fails
     */
    private static String safeNextString(String prompt, String fieldName, boolean required) throws RHMSException {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        
        if (required && input.isEmpty()) {
            throw new RHMSException(
                RHMSException.ErrorCode.EMPTY_INPUT_ERROR,
                fieldName + " cannot be empty",
                "Entering " + fieldName.toLowerCase()
            );
        }
        
        return input;
    }

    /**
     * Safely captures a date input with validation
     * @param prompt Message to display to the user
     * @return The validated date string
     * @throws RHMSException if validation fails
     */
    private static String safeNextDate(String prompt) throws RHMSException {
        System.out.print(prompt);
        String dateStr = scanner.nextLine().trim();
        ErrorHandler.validateAppointmentDate(dateStr);
        return dateStr;
    }

    /**
     * Safely captures an email input with validation
     * @param prompt Message to display to the user
     * @return The validated email string
     * @throws RHMSException if validation fails
     */
    private static String safeNextEmail(String prompt) throws RHMSException {
        System.out.print(prompt);
        String email = scanner.nextLine().trim();
        ErrorHandler.validateEmail(email);
        return email;
    }

    /**
     * Safely captures a phone input with validation
     * @param prompt Message to display to the user
     * @return The validated phone string
     * @throws RHMSException if validation fails
     */
    private static String safeNextPhone(String prompt) throws RHMSException {
        System.out.print(prompt);
        String phone = scanner.nextLine().trim();
        ErrorHandler.validatePhone(phone);
        return phone;
    }

    /**
     * Safely captures a name input with validation
     * @param prompt Message to display to the user
     * @param fieldName Name of the field (for error messages)
     * @return The validated name string
     * @throws RHMSException if validation fails
     */
    private static String safeNextName(String prompt, String fieldName) throws RHMSException {
        System.out.print(prompt);
        String name = scanner.nextLine().trim();
        ErrorHandler.validateName(name, fieldName);
        return name;
    }

    /**
     * Safely captures a numeric input with validation
     * @param prompt Message to display to the user
     * @param minValue Minimum acceptable value
     * @param maxValue Maximum acceptable value
     * @return The validated numeric input
     */
    private static int getNumericInput(String prompt, int minValue, int maxValue) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine().trim();
                
                // Check for empty input
                if (input.isEmpty()) {
                    System.out.println("Error: Input cannot be empty. Please try again.");
                    continue;
                }

                // Try to convert to integer
                int value = Integer.parseInt(input);
                
                // Validate range
                if (value < minValue || value > maxValue) {
                    System.out.printf("Error: Please enter a number between %d and %d\n", minValue, maxValue);
                    continue;
                }

                return value;

            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
    }
}
