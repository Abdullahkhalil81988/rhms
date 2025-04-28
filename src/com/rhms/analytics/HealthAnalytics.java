package com.rhms.analytics;

import com.rhms.userManagement.Patient;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.Prescription;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Analyzes patient health data to identify trends, medication effectiveness,
 * and anomalies in vital signs
 */
public class HealthAnalytics {
    
    /**
     * Analyzes vitals for trends and anomalies
     * @param patient The patient whose data is being analyzed
     * @return AnalyticsResult containing trend data and anomalies
     */
    public AnalyticsResult analyzePatientData(Patient patient) {
        AnalyticsResult result = new AnalyticsResult();
        
        VitalsDatabase vitalsDB = patient.getVitalsDatabase();
        MedicalHistory medHistory = patient.getMedicalHistory();
        
        if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
            result.addMessage("Not enough vitals data for analysis");
            return result;
        }
        
        // Analyze vitals trends
        ArrayList<VitalSign> vitals = vitalsDB.getVitals();
        analyzeVitalsTrends(vitals, result);
        
        // Analyze medication effectiveness if we have medical history
        if (medHistory != null && !medHistory.getPastConsultations().isEmpty()) {
            analyzeMedicationEffectiveness(vitals, medHistory, result);
        }
        
        return result;
    }
    
    /**
     * Analyzes trends in vital signs
     */
    private void analyzeVitalsTrends(ArrayList<VitalSign> vitals, AnalyticsResult result) {
        // Need at least 2 measurements to determine a trend
        if (vitals.size() < 2) {
            result.addMessage("Need more data points for trend analysis");
            return;
        }
        
        // Calculate trends for each vital sign
        double heartRateAvg = 0, heartRateTrend = 0;
        double oxygenAvg = 0, oxygenTrend = 0;
        double bpAvg = 0, bpTrend = 0;
        double tempAvg = 0, tempTrend = 0;
        
        for (int i = 0; i < vitals.size(); i++) {
            VitalSign vital = vitals.get(i);
            
            // Add to averages
            heartRateAvg += vital.getHeartRate();
            oxygenAvg += vital.getOxygenLevel();
            bpAvg += vital.getBloodPressure();
            tempAvg += vital.getTemperature();
            
            // Calculate trends (difference between consecutive readings)
            if (i > 0) {
                heartRateTrend += vital.getHeartRate() - vitals.get(i-1).getHeartRate();
                oxygenTrend += vital.getOxygenLevel() - vitals.get(i-1).getOxygenLevel();
                bpTrend += vital.getBloodPressure() - vitals.get(i-1).getBloodPressure();
                tempTrend += vital.getTemperature() - vitals.get(i-1).getTemperature();
            }
        }
        
        // Calculate final averages
        int count = vitals.size();
        heartRateAvg /= count;
        oxygenAvg /= count;
        bpAvg /= count;
        tempAvg /= count;
        
        // Calculate final trends (normalized by number of changes)
        int changes = count - 1;
        if (changes > 0) {
            heartRateTrend /= changes;
            oxygenTrend /= changes;
            bpTrend /= changes;
            tempTrend /= changes;
        }
        
        // Store results
        result.setHeartRateAvg(heartRateAvg);
        result.setHeartRateTrend(heartRateTrend);
        result.setOxygenAvg(oxygenAvg);
        result.setOxygenTrend(oxygenTrend);
        result.setBpAvg(bpAvg);
        result.setBpTrend(bpTrend);
        result.setTempAvg(tempAvg);
        result.setTempTrend(tempTrend);
        
        // Analyze trends and add insights
        analyzeTrendInsights(result);
    }
    
    /**
     * Generate insights based on calculated trends
     */
    private void analyzeTrendInsights(AnalyticsResult result) {
        if (result.getHeartRateTrend() > 5) {
            result.addInsight("Heart rate is trending upward significantly");
        } else if (result.getHeartRateTrend() < -5) {
            result.addInsight("Heart rate is trending downward significantly");
        }
        
        if (result.getOxygenTrend() < -2) {
            result.addInsight("Oxygen levels are decreasing, monitor closely");
        }
        
        if (result.getBpTrend() > 5) {
            result.addInsight("Blood pressure is trending upward, consider lifestyle changes");
        } else if (result.getBpTrend() < -5) {
            result.addInsight("Blood pressure is decreasing, monitor for hypotension");
        }
        
        if (result.getTempTrend() > 0.5) {
            result.addInsight("Temperature is trending upward, watch for fever");
        }
    }
    
    /**
     * Analyzes how medications affect vital signs
     */
    private void analyzeMedicationEffectiveness(ArrayList<VitalSign> vitals, 
                                              MedicalHistory medHistory, 
                                              AnalyticsResult result) {
        
        // Extract medications from history
        Map<String, String> medications = new HashMap<>();
        for (Feedback feedback : medHistory.getPastConsultations()) {
            Prescription prescription = feedback.getPrescription();
            if (prescription != null) {
                medications.put(prescription.getMedicationName(), prescription.getDosage());
            }
        }
        
        if (medications.isEmpty()) {
            result.addMessage("No medications found in history");
            return;
        }
        
        // Simple analysis: check if BP or heart rate improved after medications
        if (vitals.size() >= 3 && !medications.isEmpty()) {
            VitalSign firstReading = vitals.get(0);
            VitalSign lastReading = vitals.get(vitals.size() - 1);
            
            // Check blood pressure changes
            if (medications.keySet().stream().anyMatch(med -> 
                    med.toLowerCase().contains("pressure") || 
                    med.toLowerCase().contains("tension"))) {
                
                if (lastReading.getBloodPressure() < firstReading.getBloodPressure()) {
                    result.addInsight("Blood pressure medication appears effective");
                } else {
                    result.addInsight("Blood pressure medication may need adjustment");
                }
            }
            
            // Check heart rate changes
            if (medications.keySet().stream().anyMatch(med -> 
                    med.toLowerCase().contains("heart") || 
                    med.toLowerCase().contains("cardiac"))) {
                
                if (Math.abs(lastReading.getHeartRate() - 75) < 
                    Math.abs(firstReading.getHeartRate() - 75)) {
                    result.addInsight("Heart medication appears to be normalizing heart rate");
                }
            }
        }
        
        // Add medications to result
        result.setMedications(medications);
    }
}