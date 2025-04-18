package com.rhms.notifications;

import com.rhms.appointmentScheduling.Appointment;
import com.rhms.userManagement.Patient;
import java.util.*;
import java.time.LocalDateTime;

public class ReminderService {
    private final SMSNotification smsNotification;
    private final Timer scheduler;

    public ReminderService() {
        this.smsNotification = new SMSNotification();
        this.scheduler = new Timer(true);
    }

    public void scheduleAppointmentReminder(Appointment appointment) {
        Date appointmentDate = appointment.getAppointmentDate();
        Date reminderTime = new Date(appointmentDate.getTime() - (24 * 60 * 60 * 1000));

        Patient patient = appointment.getPatient();
        if (patient != null) {
            final String subject = "Appointment Reminder";
            final String message = String.format(
                "Reminder: You have an appointment with Dr. %s tomorrow at %s",
                appointment.getDoctor().getName(),
                appointmentDate.toString()
            );

            scheduler.schedule(new TimerTask() {
                @Override
                public void run() {
                    smsNotification.sendNotification(patient.getPhone(), subject, message);
                }
            }, reminderTime);
        }
    }

    public void scheduleMedicationReminder(Patient patient, String medication, String schedule) {
        final String subject = "Medication Reminder";
        final String message = String.format(
            "Reminder: Time to take your %s as per schedule: %s",
            medication,
            schedule
        );

        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                smsNotification.sendNotification(patient.getPhone(), subject, message);
            }
        }, 0, 24 * 60 * 60 * 1000); // Daily reminder
    }

    public void sendImmediateReminder(Patient patient, String subject, String message) {
        smsNotification.sendNotification(patient.getPhone(), subject, message);
    }

    public void cancelReminders(Patient patient) {
        scheduler.purge();
    }
}