package com.rhms.exceptions;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Centralized error handling utility for RHMS
 */
public class ErrorHandler {
    
    public static void validateAppointmentDate(String dateStr) throws RHMSException {
        try {
            if (!dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) {
                throw new RHMSException(
                    RHMSException.ErrorCode.INVALID_DATE_FORMAT,
                    "Date must be in format yyyy-MM-dd"
                );
            }

            Date appointmentDate = java.sql.Date.valueOf(dateStr);
            if (appointmentDate.before(new Date())) {
                throw new RHMSException(
                    RHMSException.ErrorCode.PAST_DATE_ERROR,
                    "Cannot schedule appointment in the past"
                );
            }
        } catch (IllegalArgumentException e) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_DATE_FORMAT,
                "Invalid date format: " + e.getMessage()
            );
        }
    }

    public static void validateVitalSigns(double heartRate, double oxygenLevel, 
                                        double bloodPressure, double temperature) 
                                        throws RHMSException {
        if (heartRate < 40 || heartRate > 120) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Heart rate must be between 40 and 120"
            );
        }
        if (oxygenLevel < 90 || oxygenLevel > 100) {
            throw new RHMSException(
                RHMSException.ErrorCode.INVALID_VITAL_SIGNS,
                "Oxygen level must be between 90 and 100"
            );
        }
        // Add other vital sign validations
    }

    public static void handleException(RHMSException e) {
        System.err.println("Error " + e.getErrorCode().getCode() + ": " + e.getMessage());
        // Could add logging here
    }
}