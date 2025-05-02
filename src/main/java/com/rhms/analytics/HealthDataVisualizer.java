package com.rhms.analytics;

import javax.swing.*;

import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.userManagement.Patient;

import java.awt.*;
import java.util.ArrayList;

/**
 * Visualizes health data using graphs and charts to help patients 
 * and doctors understand health trends
 */
public class HealthDataVisualizer {
    
    /**
     * Displays a graph of patient vital signs
     * @param patient The patient whose data will be displayed
     * @param vitalType Type of vital sign to display ("heartrate", "oxygen", "bloodpressure", "temperature")
     */
    public void displayVitalSignGraph(Patient patient, String vitalType) {
        VitalsDatabase vitalsDB = patient.getVitalsDatabase();
        
        if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
            System.out.println("No vital signs data available to visualize.");
            return;
        }
        
        ArrayList<VitalSign> vitals = vitalsDB.getVitals();
        ArrayList<Double> values = new ArrayList<>();
        
        // Extract the requested vital sign data
        for (VitalSign vital : vitals) {
            switch (vitalType.toLowerCase()) {
                case "heartrate":
                    values.add(vital.getHeartRate());
                    break;
                case "oxygen":
                    values.add(vital.getOxygenLevel());
                    break;
                case "bloodpressure":
                    values.add(vital.getBloodPressure());
                    break;
                case "temperature":
                    values.add(vital.getTemperature());
                    break;
                default:
                    System.out.println("Unknown vital sign type: " + vitalType);
                    return;
            }
        }
        
        // Create and display the graph
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Vital Sign Graph: " + vitalType.toUpperCase());
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(800, 600);
            
            Graph graph = new Graph(values, vitalType);
            frame.add(graph);
            
            frame.setVisible(true);
        });
    }
    
    /**
     * Simple graphing component for showing vital signs trends
     */
    private static class Graph extends JPanel {
        private ArrayList<Double> data;
        private String vitalType;
        
        public Graph(ArrayList<Double> data, String vitalType) {
            this.data = data;
            this.vitalType = vitalType;
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Graph styling
            int padding = 50;
            int labelPadding = 25;
            Color lineColor = new Color(44, 102, 230);
            Color pointColor = new Color(100, 100, 100, 180);
            Color gridColor = new Color(200, 200, 200, 200);
            Stroke GRAPH_STROKE = new BasicStroke(2f);
            int pointWidth = 4;
            int numberYDivisions = 10;
            
            // Get width and height
            int width = getWidth();
            int height = getHeight();
            
            // Get min and max values
            double minValue = Double.MAX_VALUE;
            double maxValue = Double.MIN_VALUE;
            for (Double value : data) {
                minValue = Math.min(minValue, value);
                maxValue = Math.max(maxValue, value);
            }
            
            // Add some padding to min/max
            double range = maxValue - minValue;
            maxValue += range * 0.1;
            minValue -= range * 0.1;
            
            // Draw background
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);
            
            // Create hatch marks and grid lines for y-axis
            g2.setColor(Color.BLACK);
            
            // Create y-axis labels
            for (int i = 0; i < numberYDivisions + 1; i++) {
                int y = height - ((i * (height - padding * 2)) / numberYDivisions + padding);
                String yLabel = String.format("%.1f", 
                                             ((maxValue - minValue) * i / numberYDivisions + minValue));
                
                // Draw grid line
                g2.setColor(gridColor);
                g2.drawLine(padding, y, width - padding, y);
                
                // Draw tick and label
                g2.setColor(Color.BLACK);
                g2.drawLine(padding - 5, y, padding, y);
                int labelWidth = g2.getFontMetrics().stringWidth(yLabel);
                g2.drawString(yLabel, padding - labelWidth - 5, y + 4);
            }
            
            // Draw x-axis labels
            if (data.size() > 1) {
                for (int i = 0; i < data.size(); i++) {
                    int x = ((i * (width - padding * 2)) / (data.size() - 1)) + padding;
                    
                    // Draw tick
                    g2.setColor(Color.BLACK);
                    g2.drawLine(x, height - padding, x, height - padding + 5);
                    
                    // Draw measurement number
                    String xLabel = String.valueOf(i + 1);
                    int labelWidth = g2.getFontMetrics().stringWidth(xLabel);
                    g2.drawString(xLabel, x - labelWidth / 2, height - padding + 20);
                }
            }
            
            // Draw axis lines
            g2.drawLine(padding, height - padding, width - padding, height - padding); // x-axis
            g2.drawLine(padding, height - padding, padding, padding); // y-axis
            
            // Draw axis labels
            g2.drawString("Measurement Number", width/2 - 50, height - 10);
            
            // Choose a unit based on vital type
            String unit = "";
            switch (vitalType.toLowerCase()) {
                case "heartrate": unit = "bpm"; break;
                case "oxygen": unit = "%"; break;
                case "bloodpressure": unit = "mmHg"; break;
                case "temperature": unit = "°C"; break;
            }
            
            // Draw the y-axis label (rotated)
            String yAxisLabel = vitalType.toUpperCase() + " (" + unit + ")";
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.rotate(-Math.PI/2, 20, height/2);
            g2d.drawString(yAxisLabel, 20, height/2);
            g2d.dispose();
            
            // Draw title
            g2.setFont(new Font("Arial", Font.BOLD, 18));
            g2.drawString(vitalType.toUpperCase() + " History", width/2 - 50, 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            
            // Create points for line
            int[] xPoints = new int[data.size()];
            int[] yPoints = new int[data.size()];
            
            // Create data points
            for (int i = 0; i < data.size(); i++) {
                double value = data.get(i);
                int x = ((i * (width - padding * 2)) / (data.size() - 1)) + padding;
                int y = (int) ((height - padding) - (((value - minValue) / (maxValue - minValue)) * (height - padding * 2)));
                xPoints[i] = x;
                yPoints[i] = y;
                
                // Draw point
                g2.setColor(pointColor);
                g2.fillOval(x - pointWidth/2, y - pointWidth/2, pointWidth, pointWidth);
            }
            
            // Draw lines between points
            g2.setColor(lineColor);
            g2.setStroke(GRAPH_STROKE);
            
            for (int i = 0; i < data.size() - 1; i++) {
                g2.drawLine(xPoints[i], yPoints[i], xPoints[i+1], yPoints[i+1]);
            }
        }
    }
}