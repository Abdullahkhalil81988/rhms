package com.rhms.reporting;

import com.rhms.userManagement.Patient;
import com.rhms.userManagement.Doctor;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.analytics.HealthAnalytics;
import com.rhms.analytics.AnalyticsResult;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Generates downloadable reports for patients and doctors
 * including vitals history, medication history, and health trends
 */
public class ReportsGenerator {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    
    /**
     * Generates a comprehensive health report for a patient
     * @param patient The patient whose data will be included in the report
     * @param includeAnalytics Whether to include analytics in the report
     * @return Path to the generated report file
     */
    public String generatePatientReport(Patient patient, boolean includeAnalytics) {
        String filename = "Patient_Report_" + patient.getName().replaceAll("\\s+", "_") + 
                         "_" + DATE_FORMAT.format(new Date()) + ".txt";
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
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
            
            System.out.println("Report generated successfully: " + filename);
            return filename;
            
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
        if (medHistory == null || medHistory.getPastConsultations().isEmpty()) {
            writer.println("No medical history records available.");
            return;
        }
        
        int index = 1;
        for (Feedback feedback : medHistory.getPastConsultations()) {
            writer.println("Consultation #" + index++);
            writer.println("Doctor: Dr. " + feedback.getDoctor().getName());
            writer.println("Comments: " + feedback.getComments());
            
            Prescription prescription = feedback.getPrescription();
            if (prescription != null) {
                writer.println("Medication: " + prescription.getMedicationName());
                writer.println("Dosage: " + prescription.getDosage());
                writer.println("Schedule: " + prescription.getSchedule());
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
}