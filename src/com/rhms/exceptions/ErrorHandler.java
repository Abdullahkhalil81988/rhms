package com.rhms.exceptions;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

/**
 * Centralized error handling utility for RHMS with enhanced validation and logging
 */
public class ErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());
    private static boolean isLoggerInitialized = false;
    
    /**
     * Initialize the error logger system
     */
    private static void initializeLogger() {
        if (!isLoggerInitialized) {
            try {
                FileHandler fileHandler = new FileHandler("rhms_errors.log", true);
                fileHandler.setFormatter(new SimpleFormatter());
                LOGGER.addHandler(fileHandler);
                LOGGER.setLevel(Level.ALL);
                isLoggerInitialized = true;
            } catch (IOException e) {
                System.err.println("Failed to initialize error logging: " + e.getMessage());
            }
        }
    }
    
    /**
     * Validates appointment date format and ensures it's in the future
     * @param dateStr Date string to validate
     * @throws RHMSException if date format is invalid or date is in the past
     */
    public static void validateAppointmentDate(String dateStr) throws RHMSException {
        try {
            // Validate format
            if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_DATE_FORMAT,
                    "Date must be in format yyyy-MM-dd",
                    "Entering appointment date"
                );
            }

            // Validate date is not in past
            Date appointmentDate = java.sql.Date.valueOf(dateStr);
            if (appointmentDate.before(new Date())) {
                throw new RHMSException(
                    RHMSException.ErrorCode.PAST_DATE_ERROR,
                    "Cannot schedule appointment in the past",
                    "Scheduling appointment for date: " + dateStr
                );
            }
            
            // Validate date is not too far in future (e.g., 6 months)
            Date sixMonthsLater = new Date(System.currentTimeMillis() + (long)182 * 24 * 60 * 60 * 1000);
            if (appointmentDate.after(sixMonthsLater)) {
                throw new RHMSException(
                    RHMSException.ErrorCode.DATE_RANGE_ERROR,
                    "Cannot schedule appointments more than 6 months in advance",
                    "Scheduling appointment for distant date: " + dateStr
                );
            }
        } catch (IllegalArgumentException e) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_DATE_FORMAT,
                "Invalid date format: " + e.getMessage(),
                "Parsing date string: " + dateStr
            );
        }
    }

    /**
     * Validates vital signs are within medically acceptable ranges
     * @throws RHMSException if any vital sign is outside acceptable range
     */
    public static void validateVitalSigns(double heartRate, double oxygenLevel, 
                                         double bloodPressure, double temperature) 
                                         throws RHMSException {
        // Validate heart rate (40-120 bpm is normal range)
        if (heartRate < 40) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Heart rate too low: " + heartRate + " bpm (minimum 40 bpm)",
                "Recording patient vital signs"
            );
        }
        if (heartRate > 120) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Heart rate too high: " + heartRate + " bpm (maximum 120 bpm)",
                "Recording patient vital signs"
            );
        }
        
        // Validate oxygen level (90-100% is normal range)
        if (oxygenLevel < 90) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Oxygen level too low: " + oxygenLevel + "% (minimum 90%)",
                "Recording patient vital signs"
            );
        }
        if (oxygenLevel > 100) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Oxygen level cannot exceed 100%",
                "Recording patient vital signs"
            );
        }
        
        // Validate blood pressure (90-140 mmHg is typical range)
        if (bloodPressure < 90) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Blood pressure too low: " + bloodPressure + " mmHg (minimum 90 mmHg)",
                "Recording patient vital signs"
            );
        }
        if (bloodPressure > 140) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Blood pressure too high: " + bloodPressure + " mmHg (maximum 140 mmHg)",
                "Recording patient vital signs"
            );
        }
        
        // Validate temperature (35-40°C is normal clinical range)
        if (temperature < 35) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Temperature too low: " + temperature + "°C (minimum 35°C)",
                "Recording patient vital signs"
            );
        }
        if (temperature > 40) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Temperature too high: " + temperature + "°C (maximum 40°C)",
                "Recording patient vital signs"
            );
        }
    }
    
    /**
     * Validates user input for name fields
     * @throws RHMSException if the name is invalid
     */
    public static void validateName(String name, String fieldName) throws RHMSException {
        if (name == null || name.trim().isEmpty()) {
            throw new RHMSException(
                RHMSException.ErrorCode.EMPTY_INPUT_ERROR,
                fieldName + " cannot be empty",
                "Entering " + fieldName.toLowerCase()
            );
        }
        
        // Check for invalid characters in name
        if (!name.matches("[a-zA-Z\\s.'-]+")) {
            throw new RHMSException(
                RHMSException.ErrorCode.TEXT_INPUT_ERROR,
                fieldName + " contains invalid characters",
                "Entering " + fieldName.toLowerCase()
            );
        }
    }
    
    /**
     * Validates email format
     * @throws RHMSException if email format is invalid
     */
    public static void validateEmail(String email) throws RHMSException {
        if (email == null || email.trim().isEmpty()) {
            throw new RHMSException(
                RHMSException.ErrorCode.EMPTY_INPUT_ERROR,
                "Email cannot be empty",
                "Entering email address"
            );
        }
        
        // Simple email validation pattern
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_INPUT,
                "Invalid email format: " + email,
                "Entering email address"
            );
        }
    }
    
    /**
     * Validates phone number format
     * @throws RHMSException if phone number format is invalid
     */
    public static void validatePhone(String phone) throws RHMSException {
        if (phone == null || phone.trim().isEmpty()) {
            throw new RHMSException(
                RHMSException.ErrorCode.EMPTY_INPUT_ERROR,
                "Phone number cannot be empty",
                "Entering phone number"
            );
        }
        
        // Common phone formats: xxx-xxx-xxxx, (xxx) xxx-xxxx, xxxxxxxxxx
        if (!phone.matches("\\d{10}|\\d{3}[-\\s]\\d{3}[-\\s]\\d{4}|\\(\\d{3}\\)\\s*\\d{3}[-\\s]\\d{4}")) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_INPUT,
                "Invalid phone number format: " + phone,
                "Entering phone number"
            );
        }
    }
    
    /**
     * Validates numeric input range
     * @throws RHMSException if number is outside acceptable range
     */
    public static void validateNumericRange(double value, double min, double max, String fieldName) throws RHMSException {
        if (value < min || value > max) {
            throw new RHMSException(
                RHMSException.ErrorCode.NUMERIC_INPUT_ERROR,
                fieldName + " must be between " + min + " and " + max,
                "Entering " + fieldName.toLowerCase()
            );
        }
    }
    
    /**
     * Process exception, display error message, and log the error
     * @param e The RHMS exception to handle
     */
    public static void handleException(RHMSException e) {
        initializeLogger();
        
        // Console output with code, message and solution
        System.err.println("\n❌ ERROR " + e.getErrorCode().getCode() + ": " + e.getMessage());
        
        if (e.getSuggestedSolution() != null) {
            System.err.println("💡 Solution: " + e.getSuggestedSolution());
        }
        
        // Log detailed error info to file
        LOGGER.log(Level.SEVERE, e.getDetailedErrorInfo());
    }
    
    /**
     * Process general exceptions by wrapping them in RHMS exceptions
     * @param e Generic exception to handle
     */
    public static void handleGeneralException(Exception e) {
        initializeLogger();
        
        // Create wrapped RHMS exception
        RHMSException rhmsException = new RHMSException(
            RHMSException.ErrorCode.COMMUNICATION_ERROR,
            "Unexpected error: " + e.getMessage(),
            "System operation"
        );
        
        // Console output
        System.err.println("\n❌ SYSTEM ERROR: " + e.getMessage());
        System.err.println("💡 Solution: Please try again or contact support");
        
        // Log detailed error info
        LOGGER.log(Level.SEVERE, rhmsException.getDetailedErrorInfo(), e);
    }
}