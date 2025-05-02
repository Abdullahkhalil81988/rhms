package com.rhms.reporting;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.rhms.analytics.AnalyticsResult;
import com.rhms.analytics.HealthAnalytics;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.userManagement.Patient;

/**
 * Generates downloadable reports for patients and doctors
 * including vitals history, medication history, and health trends
 */
public class ReportsGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    private static final String DEFAULT_REPORTS_DIR = "d:\\rhms\\reports";
    private String reportsDirectory;
    
    public ReportsGenerator() {
        this(DEFAULT_REPORTS_DIR);
    }
    
    /**
     * Constructor that allows setting a custom reports directory
     * @param reportsDirectory The directory where reports will be stored
     */
    public ReportsGenerator(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
        initializeReportsDirectory();
    }
    
    /**
     * Creates the reports directory if it doesn't exist
     */
    private void initializeReportsDirectory() {
        File directory = new File(reportsDirectory);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Created reports directory: " + reportsDirectory);
            } else {
                System.out.println("Failed to create reports directory: " + reportsDirectory);
            }
        }
    }
    
    /**
     * Set a new reports directory
     * @param reportsDirectory The new directory for reports
     * @return true if directory was created or already exists
     */
    public boolean setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
        
        // Create directory if it doesn't exist
        File directory = new File(reportsDirectory);
        if (!directory.exists()) {
            return directory.mkdirs();
        }
        return true;
    }
    
    /**
     * Get the current reports directory
     * @return The directory path where reports are stored
     */
    public String getReportsDirectory() {
        return reportsDirectory;
    }
    
    /**
     * Generates a comprehensive health report for a patient
     * @param patient The patient whose data will be included in the report
     * @param includeAnalytics Whether to include analytics in the report
     * @return Path to the generated report file
     */
    public String generatePatientReport(Patient patient, boolean includeAnalytics) {
        if (patient == null) {
            return null;
        }
        
        // Create reports directory if it doesn't exist
        initializeReportsDirectory();
        
        // Create the filename based on patient ID and timestamp
        String filename = "Patient_Report_" + patient.getName().replaceAll("\\s+", "_") + 
                         "_" + DATE_FORMAT.format(new Date()) + ".txt";
        
        // Create the full file path
        String filePath = reportsDirectory + File.separator + filename;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            // Report header
            writer.println("=================================================");
            writer.println("           PATIENT HEALTH REPORT                 ");
            writer.println("=================================================");
            writer.println("Patient: " + patient.getName());
            writer.println("ID: " + patient.getUserID());
            writer.println("Date Generated: " + new Date());
            writer.println("=================================================\n");
            
            // Include vitals history
            writeVitalsHistory(writer, patient);
            
            // Include medical & medication history
            writeMedicalHistory(writer, patient);
            
            // Include analytics if requested
            if (includeAnalytics) {
                writeAnalytics(writer, patient);
            }
            
            writer.println("\n=================================================");
            writer.println("End of Report");
            writer.println("=================================================");
            
            System.out.println("Report generated successfully: " + filePath);
            return filePath;
            
        } catch (IOException e) {
            System.out.println("Error generating report: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Write vitals history to the report
     */
    private void writeVitalsHistory(PrintWriter writer, Patient patient) {
        writer.println("VITALS HISTORY");
        writer.println("-------------------------------------------------");
        
        VitalsDatabase vitalsDB = patient.getVitalsDatabase();
        if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
            writer.println("No vitals records available.");
            return;
        }
        
        writer.println(String.format("%-5s %-10s %-12s %-15s %-12s", 
            "No.", "Heart Rate", "Oxygen Level", "Blood Pressure", "Temperature"));
        writer.println("-------------------------------------------------");
        
        int index = 1;
        for (VitalSign vital : vitalsDB.getVitals()) {
            writer.println(String.format("%-5d %-10.1f %-12.1f %-15.1f %-12.1f", 
                index++, vital.getHeartRate(), vital.getOxygenLevel(), 
                vital.getBloodPressure(), vital.getTemperature()));
        }
        writer.println();
    }
    
    /**
     * Write medical and medication history to the report
     */
    private void writeMedicalHistory(PrintWriter writer, Patient patient) {
        writer.println("\nMEDICAL HISTORY & DOCTOR FEEDBACK");
        writer.println("-------------------------------------------------");
        
        MedicalHistory medHistory = patient.getMedicalHistory();
        if (medHistory == null || medHistory.getFeedbackList().isEmpty()) {
            writer.println("No medical history records available.");
            return;
        }
        
        int index = 1;
        for (Feedback feedback : medHistory.getFeedbackList()) {
            writer.println("Consultation #" + index++);
            writer.println("Doctor: Dr. " + (feedback.getDoctor() != null ? feedback.getDoctor().getName() : "Unknown"));
            writer.println("Date: " + feedback.getConsultationDate());
            writer.println("Comments: " + feedback.getComments());
            
            Prescription prescription = feedback.getPrescription();
            if (prescription != null) {
                writer.println("Medication: " + prescription.getMedication());
                writer.println("Dosage: " + prescription.getDosage());
                writer.println("Schedule: " + (prescription.getSchedule() != null ? prescription.getSchedule() : "Not specified"));
            } else {
                writer.println("No medications prescribed.");
            }
            writer.println("-------------------------------------------------");
        }
    }
    
    /**
     * Write analytics data to the report
     */
    private void writeAnalytics(PrintWriter writer, Patient patient) {
        writer.println("\nHEALTH TRENDS & ANALYTICS");
        writer.println("-------------------------------------------------");
        
        HealthAnalytics analytics = new HealthAnalytics();
        AnalyticsResult result = analytics.analyzePatientData(patient);
        
        // Write any messages
        for (String message : result.getMessages()) {
            writer.println(message);
        }
        
        // Write vital statistics if available
        if (result.getHeartRateAvg() > 0) {
            writer.println("\nVital Signs Analysis:");
            writer.printf("Heart Rate: %.1f bpm (Trend: %+.1f)\n", 
                         result.getHeartRateAvg(), result.getHeartRateTrend());
            writer.printf("Oxygen Level: %.1f%% (Trend: %+.1f)\n", 
                         result.getOxygenAvg(), result.getOxygenTrend());
            writer.printf("Blood Pressure: %.1f mmHg (Trend: %+.1f)\n", 
                         result.getBpAvg(), result.getBpTrend());
            writer.printf("Temperature: %.1f°C (Trend: %+.1f)\n", 
                         result.getTempAvg(), result.getTempTrend());
        }
        
        // Write insights
        if (!result.getInsights().isEmpty()) {
            writer.println("\nInsights:");
            for (String insight : result.getInsights()) {
                writer.println("- " + insight);
            }
        }
        
        // Write current medications
        if (!result.getMedications().isEmpty()) {
            writer.println("\nCurrent Medications:");
            for (var entry : result.getMedications().entrySet()) {
                writer.println("- " + entry.getKey() + " (" + entry.getValue() + ")");
            }
        }
    }
    
    /**
     * Generate a system report with the specified type
     * @param filename The output filename for the report
     * @param reportType The type of report to generate
     * @return The path to the generated report
     * @throws IOException If there is an error writing the report
     */
    public String generateReport(String filename, String reportType) throws IOException {
        // Create full file path
        String filePath = reportsDirectory + File.separator + filename;
        
        // Create file and writer
        File reportFile = new File(filePath);
        try (PrintWriter writer = new PrintWriter(new FileWriter(reportFile))) {
            writer.println("=================================================");
            writer.println("           " + reportType.toUpperCase());
            writer.println("=================================================");
            writer.println("Date Generated: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            writer.println("=================================================\n");
            
            // Report content based on type
            switch (reportType) {
                case "System Usage Report":
                    writeSystemUsageReport(writer);
                    break;
                case "Appointment Statistics":
                    writeAppointmentStatistics(writer);
                    break;
                case "Doctor Performance Report":
                    writeDoctorPerformanceReport(writer);
                    break;
                case "Patient Activity Report":
                    writePatientActivityReport(writer);
                    break;
                default:
                    writer.println("No specific content for report type: " + reportType);
            }
            
            writer.println("\n=================================================");
            writer.println("End of Report");
            writer.println("=================================================");
        }
        
        return filePath;
    }
    
    /**
     * Write system usage statistics to the report
     */
    private void writeSystemUsageReport(PrintWriter writer) {
        writer.println("SYSTEM USAGE STATISTICS");
        writer.println("-------------------------------------------------");
        writer.println("Total Logins Today: " + new Random().nextInt(50));
        writer.println("Average Session Duration: " + (new Random().nextInt(50) + 10) + " minutes");
        writer.println("Peak Usage Time: 10:00 AM - 11:00 AM");
        writer.println("Total API Calls: " + (new Random().nextInt(5000) + 1000));
        writer.println("System Uptime: 99.8%");
    }
    
    /**
     * Write appointment statistics to the report
     */
    private void writeAppointmentStatistics(PrintWriter writer) {
        writer.println("APPOINTMENT STATISTICS");
        writer.println("-------------------------------------------------");
        writer.println("Total Appointments: " + (new Random().nextInt(100) + 50));
        writer.println("Completed Appointments: " + (new Random().nextInt(50) + 20));
        writer.println("Cancelled Appointments: " + (new Random().nextInt(20)));
        writer.println("Pending Appointments: " + (new Random().nextInt(30) + 10));
        writer.println("Average Appointment Duration: " + (new Random().nextInt(30) + 15) + " minutes");
    }
    
    /**
     * Write doctor performance data to the report
     */
    private void writeDoctorPerformanceReport(PrintWriter writer) {
        writer.println("DOCTOR PERFORMANCE REPORT");
        writer.println("-------------------------------------------------");
        writer.println("Total Doctors: " + (new Random().nextInt(20) + 5));
        writer.println("Average Patient Rating: " + (new Random().nextInt(2) + 3) + ".8/5.0");
        writer.println("Average Appointments Per Day: " + (new Random().nextInt(5) + 3));
        writer.println("Most Active Department: Cardiology");
        writer.println("Least Active Department: Dermatology");
    }
    
    /**
     * Write patient activity data to the report
     */
    private void writePatientActivityReport(PrintWriter writer) {
        writer.println("PATIENT ACTIVITY REPORT");
        writer.println("-------------------------------------------------");
        writer.println("Total Patients: " + (new Random().nextInt(100) + 50));
        writer.println("New Patients This Month: " + (new Random().nextInt(20) + 5));
        writer.println("Average Visits Per Patient: " + (new Random().nextInt(3) + 1) + "." + (new Random().nextInt(9)));
        writer.println("Most Common Treatment: Consultation");
        writer.println("Average Patient Age: " + (new Random().nextInt(30) + 25));
    }
}