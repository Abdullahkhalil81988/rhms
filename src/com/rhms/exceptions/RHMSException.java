package com.rhms.exceptions;

/**
 * Custom exception class for RHMS-specific errors
 */
public class RHMSException extends Exception {
    private final ErrorCode errorCode;

    public RHMSException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    // Error codes for different types of exceptions
    public enum ErrorCode {
        INVALID_DATE_FORMAT(1001, "Invalid date format"),
        PAST_DATE_ERROR(1002, "Cannot schedule in the past"),
        PATIENT_NOT_FOUND(2001, "Patient not found"),
        DOCTOR_NOT_FOUND(2002, "Doctor not found"),
        INVALID_VITAL_SIGNS(3001, "Invalid vital signs"),
        COMMUNICATION_ERROR(4001, "Communication error"),
        INVALID_INPUT(5001, "Invalid input provided");

        private final int code;
        private final String defaultMessage;

        ErrorCode(int code, String defaultMessage) {
            this.code = code;
            this.defaultMessage = defaultMessage;
        }

        public int getCode() {
            return code;
        }

        public String getDefaultMessage() {
            return defaultMessage;
        }
    }
}