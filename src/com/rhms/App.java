package com.rhms;

import com.rhms.userManagement.*;
import com.rhms.appointmentScheduling.*;
import com.rhms.healthDataHandling.*;
import com.rhms.doctorPatientInteraction.*;
import com.rhms.emergencyAlert.*;
import com.rhms.notifications.ReminderService;
import com.rhms.notifications.SMSNotification;
import com.rhms.notifications.EmailNotification;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class App {
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
        while (true) {
            // User Type Selection
            System.out.println("\n===== RHMS User Type Selection =====");
            System.out.println("1. Patient");
            System.out.println("2. Doctor");
            System.out.println("3. Admin");
            System.out.println("0. Exit System");
            System.out.print("Choose your user type: ");

            int userTypeChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (userTypeChoice == 0) {
                System.out.println("Exiting RHMS System. Goodbye!");
                return;
            }

            switch (userTypeChoice) {
                case 1: userType = "Patient"; break;
                case 2: userType = "Doctor"; break;
                case 3: userType = "Admin"; break;
                default:
                    System.out.println("Invalid choice! Please try again.");
                    continue;
            }

            // Sub-menu loop
            boolean stayInSubmenu = true;
            while (stayInSubmenu) {
                System.out.println("\n===== RHMS System Menu =====");
                if ("Admin".equals(userType)) {
                    showAdminMenu();
                } else if ("Patient".equals(userType)) {
                    showPatientMenu();
                } else if ("Doctor".equals(userType)) {
                    showDoctorMenu();
                }
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (choice == 9) {
                    stayInSubmenu = false; // Go back to user type selection
                    continue;
                }

                // Execute based on the user type
                if ("Admin".equals(userType)) {
                    switch (choice) {
                        case 1: registerPatient(); break;
                        case 2: registerDoctor(); break;
                        case 3: scheduleAppointment(); break;
                        case 4: showNotificationMenu(); break;
                        case 5: viewAllAppointments(); break;
                        case 0: stayInSubmenu = false; break;
                        default: System.out.println("Invalid choice!");
                    }
                } else if ("Patient".equals(userType)) {
                    switch (choice) {
                        case 1: scheduleAppointment(); break;
                        case 2: viewVitals(); break;
                        case 3: provideFeedback(); break;
                        case 4: triggerEmergencyAlert(); break;
                        case 5: togglePanicButton(); break;
                        case 6: joinVideoConsultation(); break;
                        case 7: openChat(); break;
                        case 0: System.out.println("Exiting RHMS System. Goodbye!"); return;
                        default: System.out.println("Invalid choice! Please try again.");
                    }
                } else if ("Doctor".equals(userType)) {
                    switch (choice) {
                        case 1: approveAppointment(); break;
                        case 2: cancelAppointment(); break;
                        case 3: uploadVitals(); break;
                        case 4: viewVitals(); break;
                        case 5: startVideoConsultation(); break;
                        case 6: openChat(); break;
                        case 0: System.out.println("Exiting RHMS System. Goodbye!"); return;
                        default: System.out.println("Invalid choice! Please try again.");
                    }
                }
            }
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
        System.out.print("Choose an option: ");
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
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Patient patient = new Patient(name, email, password, phone, address, userID);
        patients.add(patient);
        System.out.println("Patient " + name + " registered successfully.");
    }

    private static void registerDoctor() {
        System.out.print("Enter Doctor Name: ");
        String name = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine();
        System.out.print("Enter Address: ");
        String address = scanner.nextLine();
        System.out.print("Enter User ID: ");
        int userID = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter Specialization: ");
        String specialization = scanner.nextLine();
        System.out.print("Enter Years of Experience: ");
        int experienceYears = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        Doctor doctor = new Doctor(name, email, password, phone, address, userID, specialization, experienceYears);
        doctors.add(doctor);
        System.out.println("Doctor " + name + " registered successfully.");
    }

    private static void scheduleAppointment() {
        if (patients.isEmpty() || doctors.isEmpty()) {
            System.out.println("Error: Register at least one patient and one doctor first.");
            return;
        }

        System.out.print("Enter Patient Name: ");
        String patientName = scanner.nextLine();
        Patient patient = findPatient(patientName);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Enter Doctor Name: ");
        String doctorName = scanner.nextLine();
        Doctor doctor = findDoctor(doctorName);
        if (doctor == null) {
            System.out.println("Doctor not found!");
            return;
        }

        System.out.print("Enter Appointment Date (yyyy-MM-dd): ");
        Date appointmentDate = null;
        while (appointmentDate == null) {
            try {
                String appointmentDateInput = scanner.nextLine();
                if (!appointmentDateInput.matches("\\d{4}-\\d{2}-\\d{2}")) {
                    throw new IllegalArgumentException("Invalid date format. Please use yyyy-MM-dd");
                }
                appointmentDate = java.sql.Date.valueOf(appointmentDateInput);
                
                // Check if date is in the future
                if (appointmentDate.before(new Date())) {
                    System.out.println("Cannot schedule appointment in the past.");
                    System.out.print("Enter Appointment Date (yyyy-MM-dd): ");
                    appointmentDate = null;
                    continue;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                System.out.print("Please enter date in format yyyy-MM-dd (e.g., 2024-04-20): ");
            }
        }

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
        System.out.print("Enter Patient Name: ");
        String name = scanner.nextLine();
        Patient patient = findPatient(name);
        if (patient == null) {
            System.out.println("Patient not found!");
            return;
        }

        System.out.print("Enter Heart Rate: ");
        double heartRate = scanner.nextDouble();
        System.out.print("Enter Oxygen Level: ");
        double oxygenLevel = scanner.nextDouble();
        System.out.print("Enter Blood Pressure: ");
        double bloodPressure = scanner.nextDouble();
        System.out.print("Enter Temperature: ");
        double temperature = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        // Create vital sign record
        VitalSign vitals = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
        
        // Check for emergency conditions
        emergencyAlert.checkVitals(patient, vitals);

        // Store vitals record
        String vitalsRecord = String.format("HR: %.1f, O2: %.1f%%, BP: %.1f, Temp: %.1f°C", 
            heartRate, oxygenLevel, bloodPressure, temperature);
        patient.uploadMedicalRecord(vitalsRecord);

        System.out.println("Vitals uploaded successfully!");
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
}
