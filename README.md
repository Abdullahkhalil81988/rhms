Remote Hospital Management System (RHMS)
Overview

RHMS is a console-based Java application designed to manage remote hospital operations. It facilitates user management (patients, doctors, and admins), appointment scheduling, health data tracking, and doctor-patient interactions. The system ensures efficient communication between patients and doctors while maintaining medical records.
Features
User Roles

    Patient: Can register, schedule appointments, upload vital signs, and view doctor feedback.

    Doctor: Can approve/cancel appointments and provide medical feedback to patients.

    Admin: (Future feature) Can manage doctors, patients, and appointments.

Functionalities

    Patient Registration – Allows users to register as patients.

    Doctor Registration – Allows doctors to register with specialization and experience.

    Schedule Appointment – Patients can request an appointment with a registered doctor.

    Approve/Cancel Appointment – Doctors can approve or cancel appointment requests.

    Upload Vital Signs – Patients can input their health data (heart rate, oxygen level, blood pressure, temperature).

    View Patient Vitals – Doctors can access patient health records.

    Provide Doctor Feedback – Doctors can add prescriptions or comments on a patient's health.

User Type Selection

At the start, the system prompts the user to select their role (Patient, Doctor, or Admin). Once a role is selected, the menu displays options relevant to that role.

👉 Note: There is no option to switch user roles after selection. This is intentional because a doctor cannot suddenly act as a patient, and an admin does not need patient functionalities. Each user interacts with the system based on their designated role.
How to Run

    Compile the Java files using:

javac -d . App.java

Run the application:

    java com.rhms.App

    Follow on-screen instructions to navigate the system.

Future Improvements

    Implement an admin role for better system management.

    Add persistent data storage (e.g., database or file handling).

    Develop a GUI version for better user experience.