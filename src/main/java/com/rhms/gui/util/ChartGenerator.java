package com.rhms.gui.util;

import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import com.rhms.analytics.AnalyticsResult;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.userManagement.Patient;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to generate various charts for health data visualization
 */
public class ChartGenerator {
    
    /**
     * Generate a line chart for vital signs
     * @param vitals List of vital signs
     * @param vitalType Type of vital (heartRate, oxygenLevel, bloodPressure, temperature)
     * @return LineChart instance
     */
    public static LineChart<Number, Number> generateVitalLineChart(List<VitalSign> vitals, String vitalType) {
        if (vitals == null || vitals.isEmpty()) {
            // Return empty chart with message
            final NumberAxis xAxis = new NumberAxis();
            final NumberAxis yAxis = new NumberAxis();
            LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
            chart.setTitle("No Data Available");
            return chart;
        }
        
        // Create axes
        final NumberAxis xAxis = new NumberAxis(1, vitals.size(), 1);
        final NumberAxis yAxis = new NumberAxis();
        
        // Configure chart based on vital type
        LineChart<Number, Number> chart;
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        
        switch (vitalType) {
            case "heartRate":
                chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle("Heart Rate Trend");
                yAxis.setLabel("Heart Rate (bpm)");
                series.setName("Heart Rate");
                
                for (int i = 0; i < vitals.size(); i++) {
                    series.getData().add(new XYChart.Data<>(i + 1, vitals.get(i).getHeartRate()));
                }
                break;
                
            case "oxygenLevel":
                chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle("Oxygen Level Trend");
                yAxis.setLabel("Oxygen Level (%)");
                series.setName("Oxygen Level");
                
                for (int i = 0; i < vitals.size(); i++) {
                    series.getData().add(new XYChart.Data<>(i + 1, vitals.get(i).getOxygenLevel()));
                }
                break;
                
            case "bloodPressure":
                chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle("Blood Pressure Trend");
                yAxis.setLabel("Blood Pressure (mmHg)");
                series.setName("Blood Pressure");
                
                for (int i = 0; i < vitals.size(); i++) {
                    series.getData().add(new XYChart.Data<>(i + 1, vitals.get(i).getBloodPressure()));
                }
                break;
                
            case "temperature":
                chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle("Temperature Trend");
                yAxis.setLabel("Temperature (°C)");
                series.setName("Temperature");
                
                for (int i = 0; i < vitals.size(); i++) {
                    series.getData().add(new XYChart.Data<>(i + 1, vitals.get(i).getTemperature()));
                }
                break;
                
            default:
                chart = new LineChart<>(xAxis, yAxis);
                chart.setTitle("Vital Signs");
                yAxis.setLabel("Value");
                series.setName("Data");
        }
        
        xAxis.setLabel("Reading Number");
        chart.setLegendVisible(true);
        chart.setPrefHeight(400);
        chart.setPrefWidth(600);
        chart.getData().add(series);
        
        return chart;
    }
    
    /**
     * Generate a dashboard with multiple vital sign charts
     * @param patient Patient whose data to display
     * @return VBox containing multiple charts
     */
    public static VBox generateMultipleVitalCharts(Patient patient) {
        VBox chartsContainer = new VBox(20);
        
        if (patient == null || patient.getVitalSigns() == null || 
            patient.getVitalSigns().isEmpty()) {
            Text noDataText = new Text("No vital signs data available for this patient.");
            chartsContainer.getChildren().add(noDataText);
            return chartsContainer;
        }
        
        ArrayList<VitalSign> vitals = patient.getVitalSigns();
        
        // Heart Rate Chart
        LineChart<Number, Number> heartRateChart = generateVitalLineChart(vitals, "heartRate");
        heartRateChart.setPrefHeight(300);
        
        // Oxygen Level Chart
        LineChart<Number, Number> oxygenChart = generateVitalLineChart(vitals, "oxygenLevel");
        oxygenChart.setPrefHeight(300);
        
        // Blood Pressure Chart
        LineChart<Number, Number> bpChart = generateVitalLineChart(vitals, "bloodPressure");
        bpChart.setPrefHeight(300);
        
        // Temperature Chart
        LineChart<Number, Number> tempChart = generateVitalLineChart(vitals, "temperature");
        tempChart.setPrefHeight(300);
        
        chartsContainer.getChildren().addAll(heartRateChart, oxygenChart, bpChart, tempChart);
        return chartsContainer;
    }
    
    /**
     * Create a visualization of analytics results
     * @param result AnalyticsResult to visualize
     * @return VBox containing analytics visuals
     */
    public static VBox visualizeAnalyticsResult(AnalyticsResult result) {
        VBox container = new VBox(15);
        
        if (result == null) {
            Text noDataText = new Text("No analytics data available.");
            container.getChildren().add(noDataText);
            return container;
        }
        
        // Create summary text blocks
        Text headerText = new Text("Health Analytics Summary");
        headerText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        Text vitalsText = new Text(String.format(
            "Heart Rate: %.1f bpm (Trend: %+.1f)\n" +
            "Oxygen Level: %.1f%% (Trend: %+.1f)\n" +
            "Blood Pressure: %.1f mmHg (Trend: %+.1f)\n" +
            "Temperature: %.1f°C (Trend: %+.1f)",
            result.getHeartRateAvg(), result.getHeartRateTrend(),
            result.getOxygenAvg(), result.getOxygenTrend(),
            result.getBpAvg(), result.getBpTrend(),
            result.getTempAvg(), result.getTempTrend()
        ));
        vitalsText.setStyle("-fx-font-size: 14px;");
        
        // Create insights section
        Text insightsHeader = new Text("Insights & Recommendations");
        insightsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox insightsBox = new VBox(5);
        if (result.getInsights() != null && !result.getInsights().isEmpty()) {
            for (String insight : result.getInsights()) {
                Text insightText = new Text("• " + insight);
                insightText.setStyle("-fx-font-size: 14px;");
                insightsBox.getChildren().add(insightText);
            }
        } else {
            Text noInsightText = new Text("No specific insights available with current data.");
            insightsBox.getChildren().add(noInsightText);
        }
        
        // Create medications section
        Text medicationsHeader = new Text("Current Medications");
        medicationsHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox medicationsBox = new VBox(5);
        if (result.getMedications() != null && !result.getMedications().isEmpty()) {
            for (var entry : result.getMedications().entrySet()) {
                Text medicationText = new Text("• " + entry.getKey() + " (" + entry.getValue() + ")");
                medicationText.setStyle("-fx-font-size: 14px;");
                medicationsBox.getChildren().add(medicationText);
            }
        } else {
            Text noMedicationText = new Text("No medications currently prescribed.");
            medicationsBox.getChildren().add(noMedicationText);
        }
        
        container.getChildren().addAll(
            headerText, 
            vitalsText, 
            new javafx.scene.control.Separator(),
            insightsHeader,
            insightsBox,
            new javafx.scene.control.Separator(),
            medicationsHeader,
            medicationsBox
        );
        
        return container;
    }
}