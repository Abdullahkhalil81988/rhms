package com.rhms.healthDataHandling;

public class VitalSign {
    private double heartRate;
    private double oxygenLevel;
    private double bloodPressure;
    private double temperature;

    public VitalSign(double heartRate, double oxygenLevel, double bloodPressure, double temperature) {
        this.heartRate = heartRate;
        this.oxygenLevel = oxygenLevel;
        this.bloodPressure = bloodPressure;
        this.temperature = temperature;
    }

    public double getHeartRate() {
        return heartRate;
    }

    public double getOxygenLevel() {
        return oxygenLevel;
    }

    public double getBloodPressure() {
        return bloodPressure;
    }

    public double getTemperature() {
        return temperature;
    }

    public void setHeartRate(double heartRate) {
        this.heartRate = heartRate;
    }

    public void setOxygenLevel(double oxygenLevel) {
        this.oxygenLevel = oxygenLevel;
    }

    public void setBloodPressure(double bloodPressure) {
        this.bloodPressure = bloodPressure;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void displayVitals() {
        System.out.println("Heart Rate: " + heartRate + " bpm");
        System.out.println("Oxygen Level: " + oxygenLevel + " %");
        System.out.println("Blood Pressure: " + bloodPressure + " mmHg");
        System.out.println("Temperature: " + temperature + " °C");
    }
}
