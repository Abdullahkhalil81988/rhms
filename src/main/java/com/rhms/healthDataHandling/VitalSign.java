package com.rhms.healthDataHandling;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a patient's vital signs measurement
 * Stores and validates heart rate, oxygen level, blood pressure, and temperature
 */
public class VitalSign implements Serializable {
    private static final long serialVersionUID = 1L;

    private double heartRate;      // beats per minute
    private double oxygenLevel;    // percentage
    private double bloodPressure;  // mmHg
    private double temperature;    // °C
    private Date timestamp;

    /**
     * Creates a new vital signs record
     */
    public VitalSign(double heartRate, double oxygenLevel, double bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
        this.timestamp = new Date(); // Current date/time
    }

    /**
     * Get the heart rate (in bpm)
     * @return Heart rate
     */
    public double getHeartRate() {
        return heartRate;
    }

    /**
     * Get the oxygen level (in %)
     * @return Oxygen level
     */
    public double getOxygenLevel() {
        return oxygenLevel;
    }

    /**
     * Get the blood pressure (systolic, in mmHg)
     * @return Blood pressure
     */
    public double getBloodPressure() {
        return bloodPressure;
    }

    /**
     * Get the body temperature (in °C)
     * @return Temperature
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Get the timestamp when the vital sign was recorded
     * @return Timestamp
     */
    public Date getTimestamp() {
        return timestamp;
    }
}
