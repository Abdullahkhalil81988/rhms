# Remote Healthcare Management System (RHMS)

## Overview
RHMS is a comprehensive healthcare management system that enables remote interaction between patients and healthcare providers. The system facilitates appointment scheduling, vital signs monitoring, emergency alerts, and real-time communication.

## Features
- Multi-user support (Patients, Doctors, Administrators)
- Appointment scheduling and management
- Real-time vital signs monitoring
- Emergency alert system with panic button
- Video consultation capabilities
- Secure chat messaging
- Automated notification system (Email & SMS)
- Medical records management
- Doctor feedback system
- Secure user authentication and authorization

## Tech Stack
- Java 11+
- Maven for dependency management
- JUnit for testing
- javax.mail for email notifications
- Spring Security for authentication

## Project Structure
```
rhms/
├── src/
│   ├── com/rhms/
│   │   ├── appointmentScheduling/
│   │   ├── doctorPatientInteraction/
│   │   ├── emergencyAlert/
│   │   ├── exceptions/
│   │   ├── healthDataHandling/
│   │   ├── notifications/
│   │   ├── userManagement/
│   │   ├── authentication/
│   │   │   ├── config/
│   │   │   ├── controllers/
│   │   │   ├── models/
│   │   │   └── services/
├── test/
├── docs/
└── resources/
```

## Getting Started

### Prerequisites
- Java JDK 11 or higher
- Maven 3.6.0 or higher
- IDE (Visual Studio Code recommended)

### Installation
1. Clone the repository:
```bash
git clone https://github.com/yourusername/rhms.git
```

2. Navigate to project directory:
```bash
cd rhms
```

3. Build the project:
```bash
mvn clean install
```

4. Run the application:
```bash
java -jar target/rhms-1.0.0.jar
```

## Authentication Setup

### Configure Authentication
1. Add security dependencies to pom.xml:
```bash
mvn dependency:add -Dartifact=org.springframework.boot:spring-boot-starter-security
mvn dependency:add -Dartifact=io.jsonwebtoken:jjwt:0.9.1
```

2. Configure application.properties:
```bash
echo "jwt.secret=your_jwt_secret_key" >> src/main/resources/application.properties
echo "jwt.expiration=86400000" >> src/main/resources/application.properties
```

3. Create security configuration:
```bash
mkdir -p src/main/java/com/rhms/authentication/config
touch src/main/java/com/rhms/authentication/config/SecurityConfig.java
```

4. Set up user roles:
```bash
mkdir -p src/main/java/com/rhms/authentication/models
touch src/main/java/com/rhms/authentication/models/Role.java
```

### Authentication Operations

#### User Registration
```java
// Register a new user
UserService.register("username", "password", "ROLE_PATIENT");

// Create admin account
UserService.register("admin", "securePassword", "ROLE_ADMIN");

// Create doctor account
UserService.register("doctor", "medicalPass", "ROLE_DOCTOR");
```

#### User Login
```java
// Authenticate user
AuthenticationResponse response = authService.authenticate("username", "password");
String token = response.getToken();

// Use token for subsequent requests
httpClient.setHeader("Authorization", "Bearer " + token);
```

#### Password Management
```java
// Change password
UserService.changePassword("username", "oldPassword", "newPassword");

// Request password reset
UserService.requestPasswordReset("user@example.com");

// Complete password reset
UserService.resetPassword("resetToken", "newPassword");
```

## Usage

### User Types
1. **Patient**
   - Schedule appointments
   - View vital signs
   - Trigger emergency alerts
   - Access video consultations
   - Chat with doctors

2. **Doctor**
   - Manage appointments
   - Upload patient vitals
   - Start video consultations
   - Provide patient feedback
   - Access chat system

3. **Administrator**
   - Register patients and doctors
   - Manage appointments
   - Send system notifications
   - View system reports
   - Manage user permissions

### Common Operations
```java
// Register a new patient
registerPatient(name, email, phone, address);

// Schedule an appointment
scheduleAppointment(patient, doctor, date);

// Start video consultation
startVideoConsultation(meetingId);
```

## Testing
Run the test suite:
```bash
mvn test
```

Run authentication tests specifically:
```bash
mvn test -Dtest=com.rhms.authentication.**
```

## Security Best Practices
- Passwords are hashed using BCrypt 
- JWT tokens expire after 24 hours
- Role-based access control implemented
- HTTPS required for all communications
- Failed login attempts are rate-limited
- Security headers configured for XSS prevention

## Contributing
1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## Error Handling
The system implements comprehensive error handling for:
- Input validation
- Type conversion errors
- Network communication
- Database operations
- User authentication
- Authorization failures
- Token validation errors

## Logging
System events are logged using custom error handling:
- User actions
- System errors
- Security events
- Performance metrics
- Authentication attempts
- Permission changes

## Running the Application

### JavaFX Commands
Run the JavaFX application with Maven:
```bash
mvn javafx:run
```

Clean and run the JavaFX application:
```bash
mvn clean javafx:run
```

Create an executable JAR with dependencies:
```bash
mvn clean package
```

Run the packaged JAR file:
```bash
java -jar target/rhms-1.0.0-jar-with-dependencies.jar
```

### Maven Commands
Compile the project:
```bash
mvn compile
```

Clean the project (remove target directory):
```bash
mvn clean
```

Install the package without running tests:
```bash
mvn install -DskipTests
```

Update project dependencies:
```bash
mvn versions:display-dependency-updates
```

Generate JavaDoc:
```bash
mvn javadoc:javadoc
```

## Contact
Your Name - your.email@example.com
Project Link: https://github.com/yourusername/rhms

## Acknowledgments
- Java Development Team
- Healthcare Industry Standards
- Open Source Community
- Spring Security Team
````
