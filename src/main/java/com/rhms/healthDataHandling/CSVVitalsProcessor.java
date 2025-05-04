package com.rhms.healthDataHandling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVVitalsProcessor {
    
    /**
     * Process a CSV file containing vital sign records
     * @param filePath The path to the CSV file
     * @return ArrayList of VitalSign objects
     */
    public ArrayList<VitalSign> processCSVFile(String filePath) {
        ArrayList<VitalSign> importedVitals = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = reader.readLine()) != null) {
                // Skip comments
                if (line.trim().startsWith("//")) {
                    continue;
                }
                
                // Skip header line
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                try {
                    // Split the line by comma
                    String[] values = line.split(",");
                    
                    // Make sure we have 4 values
                    if (values.length >= 4) {
                        // Parse values, trimming any whitespace
                        double heartRate = Double.parseDouble(values[0].trim());
                        double oxygenLevel = Double.parseDouble(values[1].trim());
                        double bloodPressure = Double.parseDouble(values[2].trim());
                        double temperature = Double.parseDouble(values[3].trim());
                        
                        // Create vital sign and add to list
                        VitalSign vitalSign = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
                        importedVitals.add(vitalSign);
                        
                        System.out.println("Imported vital: " + heartRate + ", " + oxygenLevel + 
                                          ", " + bloodPressure + ", " + temperature);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Error parsing line: " + line + " - " + e.getMessage());
                }
            }
            
            System.out.println("Total imported vitals: " + importedVitals.size());
            
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            e.printStackTrace();
        }
        
        return importedVitals;
    }
}