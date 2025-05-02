package com.rhms.exceptions;

import java.time.LocalDateTime;

/**
 * Custom exception class for RHMS-specific errors with enhanced tracking capabilities
 */
public class RHMSException extends Exception {
    private final ErrorCode errorCode;
    private final LocalDateTime timestamp;
    private String userAction;
    private String suggestedSolution;

    /**
     * Creates a new RHMS exception with error code and message
     * @param errorCode The specific error code for this exception
     * @param message Detailed explanation of the error
     */
    public RHMSException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.suggestedSolution = errorCode.getDefaultSolution();
    }

    /**
     * Creates a new RHMS exception with error code, message, and user action that caused it
     * @param errorCode The specific error code for this exception
     * @param message Detailed explanation of the error
     * @param userAction Description of what the user was attempting to do
     */
    public RHMSException(ErrorCode errorCode, String message, String userAction) {
        super(message);
        this.errorCode = errorCode;
        this.timestamp = LocalDateTime.now();
        this.userAction = userAction;
        this.suggestedSolution = errorCode.getDefaultSolution();
    }

    // Getters for exception properties
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    public String getUserAction() {
        return userAction;
    }
    
    public String getSuggestedSolution() {
        return suggestedSolution;
    }
    
    // Setter for custom solution
    public void setSuggestedSolution(String solution) {
        this.suggestedSolution = solution;
    }

    /**
     * Comprehensive error details for logging and debugging
     */
    public String getDetailedErrorInfo() {
        StringBuilder details = new StringBuilder();
        details.append("Error ").append(errorCode.getCode()).append(": ")
               .append(getMessage()).append("\n");
        details.append("Timestamp: ").append(timestamp).append("\n");
        
        if (userAction != null) {
            details.append("User action: ").append(userAction).append("\n");
        }
        
        if (suggestedSolution != null) {
            details.append("Suggested solution: ").append(suggestedSolution).append("\n");
        }
        
        return details.toString();
    }

    // Error codes for different types of exceptions
    public enum ErrorCode {
        // Date and time related errors
        INVALID_DATE_FORMAT(1001, "Invalid date format", 
                           "Please enter date in the format yyyy-MM-dd"),
        PAST_DATE_ERROR(1002, "Cannot schedule in the past", 
                       "Enter a future date for scheduling"),
        DATE_RANGE_ERROR(1003, "Date outside allowed range",
                        "Choose a date within the next 6 months"),
                        
        // User management errors
        PATIENT_NOT_FOUND(2001, "Patient not found", 
                         "Verify patient name or register if new"),
        DOCTOR_NOT_FOUND(2002, "Doctor not found",
                        "Verify doctor name or check available doctors"),
        AUTHENTICATION_ERROR(2003, "Authentication failed", 
                           "Check credentials and try again"),
        USER_ALREADY_EXISTS(2004, "User already exists",
                           "Use a different email or user ID"),
                           
        // Medical data errors
        INVALID_VITAL_SIGNS(3001, "Invalid vital signs", 
                           "Enter values within normal physiological ranges"),
        MEDICAL_RECORD_ERROR(3002, "Medical record error",
                            "Check record format and try again"),
        PRESCRIPTION_ERROR(3003, "Invalid prescription",
                          "Review medication details and dosage"),
                          
        // Communication errors
        COMMUNICATION_ERROR(4001, "Communication error",
                           "Check network connectivity and try again"),
        NOTIFICATION_FAILED(4002, "Failed to send notification",
                           "Verify contact details and retry"),
        VIDEO_CALL_ERROR(4003, "Video call connection failed",
                        "Check internet connection and browser settings"),
                        
        // Input validation errors
        INVALID_INPUT(5001, "Invalid input provided",
                      "Check input format and try again"),
        NUMERIC_INPUT_ERROR(5002, "Expected numeric input",
                           "Please enter a valid number"),
        TEXT_INPUT_ERROR(5003, "Invalid text input",
                        "Text contains invalid characters"),
        EMPTY_INPUT_ERROR(5004, "Required field is empty",
                         "Please fill in all required fields");

        private final int code;
        private final String defaultMessage;
        private final String defaultSolution;

        ErrorCode(int code, String defaultMessage, String defaultSolution) {
            this.code = code;
            this.defaultMessage = defaultMessage;
            this.defaultSolution = defaultSolution;
        }

        public int getCode() {
            return code;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
        
        public String getDefaultSolution() {
            return defaultSolution;
        }
    }
}