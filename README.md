# Remote Hospital Management System

A comprehensive Java-based system for managing remote hospital operations.

## Features

- User Management (Patients, Doctors, Administrators)
- Appointment Scheduling
- Doctor-Patient Chat System with Encryption
- Emergency Alert System
- Email & SMS Notifications
- Health Data Management
- Secure Data Handling

## Setup

1. Clone the repository:
```bash
git clone https://github.com/Abdullahkhalil81988/Remote_Hospital_Management_System.git
cd Remote_Hospital_Management_System
```

2. Install dependencies:
```bash
mvn clean install
```

3. Configure email settings:
   - Update `EmailNotification.java` with your Gmail credentials
   - Enable 2-Step Verification in your Google Account
   - Generate an App Password for the application

4. Run the application:
```bash
java -cp target/classes com.rhms.App
```

## Requirements

- Java 11 or higher
- Maven 3.6 or higher
- Git
- Gmail account for notifications

## Project Structure

```
src/
├── com.rhms/
    ├── appointmentScheduling/   # Appointment management
    ├── doctorPatientInteraction/# Chat and consultations
    ├── emergencyAlert/         # Emergency response system
    ├── healthDataHandling/     # Patient health records
    ├── notifications/          # Email and SMS services
    └── userManagement/         # User authentication and roles
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Author

- Abdullah Khalil (@Abdullahkhalil81988)

## License

This project is licensed under the MIT License - see the LICENSE file for details