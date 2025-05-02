package com.rhms.analytics;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Contains the results of health data analysis including trends,
 * insights, and medication effectiveness
 */
public class AnalyticsResult {
    private double heartRateAvg;
    private double heartRateTrend;
    private double oxygenAvg;
    private double oxygenTrend;
    private double bpAvg;
    private double bpTrend;
    private double tempAvg;
    private double tempTrend;
    
    private ArrayList<String> insights;
    private ArrayList<String> messages;
    private Map<String, String> medications;
    
    public AnalyticsResult() {
        this.insights = new ArrayList<>();
        this.messages = new ArrayList<>();
        this.medications = new HashMap<>();
    }
    
    // Add insight from analytics
    public void addInsight(String insight) {
        insights.add(insight);
    }
    
    // Add message (info, warnings, etc)
    public void addMessage(String message) {
        messages.add(message);
    }
    
    // Getters and setters
    public double getHeartRateAvg() {
        return heartRateAvg;
    }
    
    public void setHeartRateAvg(double heartRateAvg) {
        this.heartRateAvg = heartRateAvg;
    }
    
    public double getHeartRateTrend() {
        return heartRateTrend;
    }
    
    public void setHeartRateTrend(double heartRateTrend) {
        this.heartRateTrend = heartRateTrend;
    }
    
    public double getOxygenAvg() {
        return oxygenAvg;
    }
    
    public void setOxygenAvg(double oxygenAvg) {
        this.oxygenAvg = oxygenAvg;
    }
    
    public double getOxygenTrend() {
        return oxygenTrend;
    }
    
    public void setOxygenTrend(double oxygenTrend) {
        this.oxygenTrend = oxygenTrend;
    }
    
    public double getBpAvg() {
        return bpAvg;
    }
    
    public void setBpAvg(double bpAvg) {
        this.bpAvg = bpAvg;
    }
    
    public double getBpTrend() {
        return bpTrend;
    }
    
    public void setBpTrend(double bpTrend) {
        this.bpTrend = bpTrend;
    }
    
    public double getTempAvg() {
        return tempAvg;
    }
    
    public void setTempAvg(double tempAvg) {
        this.tempAvg = tempAvg;
    }
    
    public double getTempTrend() {
        return tempTrend;
    }
    
    public void setTempTrend(double tempTrend) {
        this.tempTrend = tempTrend;
    }
    
    public ArrayList<String> getInsights() {
        return insights;
    }
    
    public ArrayList<String> getMessages() {
        return messages;
    }
    
    public Map<String, String> getMedications() {
        return medications;
    }
    
    public void setMedications(Map<String, String> medications) {
        this.medications = medications;
    }
    
    // Display the analysis results in a formatted way
    public void displayResults() {
        System.out.println("\n===== Health Analytics Results =====");
        
        // Display any messages first
        if (!messages.isEmpty()) {
            System.out.println("\nMessages:");
            for (String message : messages) {
                System.out.println("- " + message);
            }
        }
        
        // Display vital statistics
        if (heartRateAvg > 0) {
            System.out.println("\nVital Signs Analysis:");
            System.out.printf("Heart Rate: %.1f bpm (Trend: %+.1f)\n", heartRateAvg, heartRateTrend);
            System.out.printf("Oxygen Level: %.1f%% (Trend: %+.1f)\n", oxygenAvg, oxygenTrend);
            System.out.printf("Blood Pressure: %.1f mmHg (Trend: %+.1f)\n", bpAvg, bpTrend);
            System.out.printf("Temperature: %.1f°C (Trend: %+.1f)\n", tempAvg, tempTrend);
        }
        
        // Display insights
        if (!insights.isEmpty()) {
            System.out.println("\nInsights:");
            for (String insight : insights) {
                System.out.println("- " + insight);
            }
        }
        
        // Display medications
        if (!medications.isEmpty()) {
            System.out.println("\nCurrent Medications:");
            for (Map.Entry<String, String> med : medications.entrySet()) {
                System.out.println("- " + med.getKey() + " (" + med.getValue() + ")");
            }
        }
        
        System.out.println("\n==================================");
    }
}