package com.rhms.healthDataHandling;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CSVVitalsProcessor {
    
    /**
     * Process a CSV file containing vital sign records
     * @param filename The path to the CSV file
     * @return ArrayList of VitalSign objects
     * @throws IOException If file cannot be read
     */
    public ArrayList<VitalSign> processCSVFile(String filename) throws IOException {
        ArrayList<VitalSign> vitalSigns = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Skip empty lines
                if (line.trim().isEmpty()) {
                    continue;
                }
                
                try {
                    // Split the line by comma
                    String[] values = line.split(",");
                    
                    // Check if we have all 4 required values
                    if (values.length >= 4) {
                        // Remove any quotes and trim whitespace from the values
                        String heartRateStr = values[0].trim().replace("\"", "");
                        String oxygenStr = values[1].trim().replace("\"", "");
                        String bpStr = values[2].trim().replace("\"", "");
                        String tempStr = values[3].trim().replace("\"", "");
                        
                        double heartRate = Double.parseDouble(heartRateStr);
                        double oxygenLevel = Double.parseDouble(oxygenStr);
                        double bloodPressure = Double.parseDouble(bpStr);
                        double temperature = Double.parseDouble(tempStr);
                        
                        // Create and add the vital sign
                        VitalSign vitalSign = new VitalSign(heartRate, oxygenLevel, bloodPressure, temperature);
                        vitalSigns.add(vitalSign);
                        
                        System.out.println("Parsed: HR=" + heartRate + ", O2=" + oxygenLevel + 
                                          ", BP=" + bloodPressure + ", Temp=" + temperature);
                    } else {
                        System.out.println("Warning: Incorrect number of values in CSV line: " + line);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Warning: Invalid number format in CSV line: \"" + line + 
                                      "\" - " + e.getMessage());
                }
            }
        }
        
        return vitalSigns;

    }
}