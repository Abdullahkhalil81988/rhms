package com.rhms.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.beans.property.SimpleStringProperty;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import com.rhms.appointmentScheduling.Appointment;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.gui.SessionManager;
import com.rhms.notifications.EmailNotification;
import com.rhms.notifications.SMSNotification;
import com.rhms.reporting.ReportsGenerator;
import com.rhms.userManagement.Administrator;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;
import com.rhms.userManagement.User;
import com.rhms.userManagement.UserIDManager;

public class AdminDashboardController {
    @FXML private Label welcomeLabel;
    @FXML private StackPane contentArea;
    
    private Administrator currentAdmin;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
    
    @FXML
    public void initialize() {
        // Get current admin from session
        currentAdmin = (Administrator) SessionManager.getCurrentUser();
        
        if (currentAdmin != null) {
            welcomeLabel.setText("Welcome, Administrator " + currentAdmin.getName());
        }
    }
    
    @FXML
    private void handleRegisterPatient(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox registrationView = new VBox(15);
        registrationView.setPadding(new Insets(20));
        registrationView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Register New Patient");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        
        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        
        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        
        Button registerButton = new Button("Register Patient");
        registerButton.setOnAction(e -> {
            // Validate inputs
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                passwordField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                addressField.getText().isEmpty()) {
                
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "All fields are required.");
                return;
            }
            
            // Create new patient
            int patientID = UserIDManager.getNextPatientID();
            Patient newPatient = new Patient(
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                phoneField.getText(),
                addressField.getText(),
                patientID
            );
            
            // Register in system
            SessionManager.registerPatient(newPatient);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", 
                     "Patient registered successfully with ID: " + patientID);
            
            // Clear form
            nameField.clear();
            emailField.clear();
            passwordField.clear();
            phoneField.clear();
            addressField.clear();
        });
        
        // Add elements to form
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);
        formGrid.add(addressLabel, 0, 4);
        formGrid.add(addressField, 1, 4);
        
        registrationView.getChildren().addAll(headerLabel, formGrid, registerButton);
        contentArea.getChildren().add(registrationView);
    }
    
    @FXML
    private void handleRegisterDoctor(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox registrationView = new VBox(15);
        registrationView.setPadding(new Insets(20));
        registrationView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Register New Doctor");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        
        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        
        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        
        Label specLabel = new Label("Specialization:");
        TextField specField = new TextField();
        
        Label expLabel = new Label("Years of Experience:");
        Spinner<Integer> expSpinner = new Spinner<>(1, 50, 5);
        
        Button registerButton = new Button("Register Doctor");
        registerButton.setOnAction(e -> {
            // Validate inputs
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                passwordField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                addressField.getText().isEmpty() || specField.getText().isEmpty()) {
                
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "All fields are required.");
                return;
            }
            
            // Create new doctor
            int doctorID = UserIDManager.getNextDoctorID();
            Doctor newDoctor = new Doctor(
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                phoneField.getText(),
                addressField.getText(),
                doctorID,
                specField.getText(),
                expSpinner.getValue()
            );
            
            // Register in system
            SessionManager.registerDoctor(newDoctor);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", 
                     "Doctor registered successfully with ID: " + doctorID);
            
            // Clear form
            nameField.clear();
            emailField.clear();
            passwordField.clear();
            phoneField.clear();
            addressField.clear();
            specField.clear();
            expSpinner.getValueFactory().setValue(5);
        });
        
        // Add elements to form
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);
        formGrid.add(addressLabel, 0, 4);
        formGrid.add(addressField, 1, 4);
        formGrid.add(specLabel, 0, 5);
        formGrid.add(specField, 1, 5);
        formGrid.add(expLabel, 0, 6);
        formGrid.add(expSpinner, 1, 6);
        
        registrationView.getChildren().addAll(headerLabel, formGrid, registerButton);
        contentArea.getChildren().add(registrationView);
    }
    
    @FXML
    private void handleRegisterAdmin(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox registrationView = new VBox(15);
        registrationView.setPadding(new Insets(20));
        registrationView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Register New Administrator");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();
        
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();
        
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        
        Label phoneLabel = new Label("Phone:");
        TextField phoneField = new TextField();
        
        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();
        
        Button registerButton = new Button("Register Administrator");
        registerButton.setOnAction(e -> {
            // Validate inputs
            if (nameField.getText().isEmpty() || emailField.getText().isEmpty() || 
                passwordField.getText().isEmpty() || phoneField.getText().isEmpty() ||
                addressField.getText().isEmpty()) {
                
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "All fields are required.");
                return;
            }
            
            // Create new admin
            int adminID = UserIDManager.getNextAdminID();
            Administrator newAdmin = new Administrator(
                nameField.getText(),
                emailField.getText(),
                passwordField.getText(),
                phoneField.getText(),
                addressField.getText(),
                adminID
            );
            
            // Register in system
            SessionManager.registerAdministrator(newAdmin);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Registration Success", 
                     "Administrator registered successfully with ID: " + adminID);
            
            // Clear form
            nameField.clear();
            emailField.clear();
            passwordField.clear();
            phoneField.clear();
            addressField.clear();
        });
        
        // Add elements to form
        formGrid.add(nameLabel, 0, 0);
        formGrid.add(nameField, 1, 0);
        formGrid.add(emailLabel, 0, 1);
        formGrid.add(emailField, 1, 1);
        formGrid.add(passwordLabel, 0, 2);
        formGrid.add(passwordField, 1, 2);
        formGrid.add(phoneLabel, 0, 3);
        formGrid.add(phoneField, 1, 3);
        formGrid.add(addressLabel, 0, 4);
        formGrid.add(addressField, 1, 4);
        
        registrationView.getChildren().addAll(headerLabel, formGrid, registerButton);
        contentArea.getChildren().add(registrationView);
    }
    
    @FXML
    private void handleScheduleAppointment(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox appointmentView = new VBox(15);
        appointmentView.setPadding(new Insets(20));
        appointmentView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Schedule Appointment");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        // Get all doctors and patients
        ArrayList<Doctor> doctors = SessionManager.getDoctors();
        ArrayList<Patient> patients = SessionManager.getPatients();
        
        // Doctor selection
        Label doctorLabel = new Label("Select Doctor:");
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        
        // Display doctor names in dropdown
        doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSpecialization() + ")");
                }
            }
        });
        
        // Set displayed value in ComboBox
        doctorComboBox.setButtonCell(new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSpecialization() + ")");
                }
            }
        });
        
        // Add doctors to combobox
        ObservableList<Doctor> doctorsData = FXCollections.observableArrayList(doctors);
        doctorComboBox.setItems(doctorsData);
        
        if (!doctorsData.isEmpty()) {
            doctorComboBox.setValue(doctorsData.get(0));
        }
        
        // Patient selection
        Label patientLabel = new Label("Select Patient:");
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        
        // Display patient names in dropdown
        patientComboBox.setCellFactory(param -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getUserID() + ")");
                }
            }
        });
        
        // Set displayed value in ComboBox
        patientComboBox.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (ID: " + item.getUserID() + ")");
                }
            }
        });
        
        // Add patients to combobox
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(patients);
        patientComboBox.setItems(patientsData);
        
        if (!patientsData.isEmpty()) {
            patientComboBox.setValue(patientsData.get(0));
        }
        
        // Date picker for appointment date
        Label dateLabel = new Label("Select Date:");
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(java.time.LocalDate.now());
        
        // Time selection
        Label timeLabel = new Label("Select Time:");
        ComboBox<String> timeComboBox = new ComboBox<>();
        timeComboBox.getItems().addAll(
                "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM", 
                "01:00 PM", "02:00 PM", "03:00 PM", "04:00 PM"
        );
        timeComboBox.setValue("09:00 AM");
        
        // Notes
        Label notesLabel = new Label("Notes:");
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        
        Button scheduleButton = new Button("Schedule Appointment");
        scheduleButton.setOnAction(e -> {
            // Validate selections
            if (doctorComboBox.getValue() == null || patientComboBox.getValue() == null || 
                datePicker.getValue() == null) {
                
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "Doctor, patient, and date are required.");
                return;
            }
            
            // Get selected doctor and patient
            Doctor selectedDoctor = doctorComboBox.getValue();
            Patient selectedPatient = patientComboBox.getValue();
            
            // Convert date to Date object
            Date appointmentDate = java.sql.Date.valueOf(datePicker.getValue());
            
            // Create appointment
            Appointment appointment = SessionManager.getAppointmentManager()
                    .requestAppointment(appointmentDate, selectedDoctor, selectedPatient);
            
            // Since this is admin-scheduled, approve it automatically
            SessionManager.getAppointmentManager().approveAppointment(appointment);
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Appointment Scheduled", 
                     "Appointment scheduled and approved for " + selectedPatient.getName() +
                     " with Dr. " + selectedDoctor.getName());
        });
        
        // Add elements to form
        formGrid.add(doctorLabel, 0, 0);
        formGrid.add(doctorComboBox, 1, 0);
        formGrid.add(patientLabel, 0, 1);
        formGrid.add(patientComboBox, 1, 1);
        formGrid.add(dateLabel, 0, 2);
        formGrid.add(datePicker, 1, 2);
        formGrid.add(timeLabel, 0, 3);
        formGrid.add(timeComboBox, 1, 3);
        formGrid.add(notesLabel, 0, 4);
        formGrid.add(notesArea, 1, 4);
        
        appointmentView.getChildren().addAll(headerLabel, formGrid, scheduleButton);
        contentArea.getChildren().add(appointmentView);
    }
    
    @FXML
    private void handleEmailPatient(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox emailView = new VBox(15);
        emailView.setPadding(new Insets(20));
        emailView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Email Patient");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all patients
        ArrayList<Patient> allPatients = SessionManager.getPatients();
        
        // Create form
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(20));
        
        Label patientLabel = new Label("Select Patient:");
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        
        // Create custom cell factory to display patient names
        patientComboBox.setCellFactory(param -> new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getEmail() + ")");
                }
            }
        });
        
        // Set the button cell to also display the patient name
        patientComboBox.setButtonCell(new ListCell<Patient>() {
            @Override
            protected void updateItem(Patient item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getEmail() + ")");
                }
            }
        });
        
        // Add patients to combo box
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(allPatients);
        patientComboBox.setItems(patientsData);
        
        if (!patientsData.isEmpty()) {
            patientComboBox.setValue(patientsData.get(0));
        }
        
        // Subject and message
        Label subjectLabel = new Label("Subject:");
        TextField subjectField = new TextField();
        subjectField.setPromptText("Enter email subject");
        
        Label messageLabel = new Label("Message:");
        TextArea messageArea = new TextArea();
        messageArea.setPromptText("Enter your message");
        messageArea.setPrefRowCount(10);
        
        // Status label
        Label statusLabel = new Label();
        statusLabel.setVisible(false);
        
        // Check if email is configured
        EmailNotification emailService = SessionManager.getEmailNotification();
        if (emailService.isSimulateMode()) {
            Label warningLabel = new Label("⚠️ Email is in simulation mode. Configure email settings first to send real emails.");
            warningLabel.setStyle("-fx-text-fill: orange;");
            emailView.getChildren().add(warningLabel);
        }
        
        // Send button
        Button sendButton = new Button("Send Email");
        sendButton.setOnAction(e -> {
            statusLabel.setVisible(true);
            
            Patient selectedPatient = patientComboBox.getValue();
            String subject = subjectField.getText();
            String message = messageArea.getText();
            
            if (selectedPatient == null) {
                statusLabel.setText("Please select a patient");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            if (subject.isEmpty() || message.isEmpty()) {
                statusLabel.setText("Subject and message are required");
                statusLabel.setStyle("-fx-text-fill: red;");
                return;
            }
            
            // Send email
            emailService.sendNotification(selectedPatient.getEmail(), subject, message);
            
            if (emailService.isSimulateMode()) {
                statusLabel.setText("Email simulated (check console for details)");
                statusLabel.setStyle("-fx-text-fill: blue;");
            } else {
                statusLabel.setText("Email sent successfully!");
                statusLabel.setStyle("-fx-text-fill: green;");
            }
            
            // Clear fields
            subjectField.clear();
            messageArea.clear();
        });
        
        // Add fields to form
        formGrid.add(patientLabel, 0, 0);
        formGrid.add(patientComboBox, 1, 0);
        formGrid.add(subjectLabel, 0, 1);
        formGrid.add(subjectField, 1, 1);
        formGrid.add(messageLabel, 0, 2);
        formGrid.add(messageArea, 1, 2);
        formGrid.add(statusLabel, 0, 3, 2, 1);
        
        emailView.getChildren().addAll(headerLabel, formGrid, sendButton);
        contentArea.getChildren().add(emailView);
    }
    
    @FXML
    private void handleSendNotifications(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox notificationsView = new VBox(15);
        notificationsView.setPadding(new Insets(20));
        notificationsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Send Notifications");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Tabbed pane for different notification types
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // SMS Tab
        Tab smsTab = new Tab("SMS Notifications");
        VBox smsContent = new VBox(15);
        smsContent.setPadding(new Insets(20));
        
        Label smsRecipientLabel = new Label("Select Recipient Type:");
        ComboBox<String> smsRecipientCombo = new ComboBox<>();
        smsRecipientCombo.getItems().addAll("All Users", "Doctors", "Patients", "Administrators");
        smsRecipientCombo.setValue("All Users");
        
        Label smsMessageLabel = new Label("SMS Message:");
        TextArea smsMessageArea = new TextArea();
        smsMessageArea.setPrefRowCount(5);
        
        Button sendSmsButton = new Button("Send SMS");
        sendSmsButton.setOnAction(e -> {
            String recipientType = smsRecipientCombo.getValue();
            String message = smsMessageArea.getText();
            
            if (message.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "Message cannot be empty.");
                return;
            }
            
            // Get recipients based on selection
            ArrayList<User> recipients = new ArrayList<>();
            switch (recipientType) {
                case "Doctors":
                    recipients.addAll(SessionManager.getDoctors());
                    break;
                case "Patients":
                    recipients.addAll(SessionManager.getPatients());
                    break;
                case "Administrators":
                    recipients.addAll(SessionManager.getAdministrators());
                    break;
                default:
                    recipients.addAll(SessionManager.getDoctors());
                    recipients.addAll(SessionManager.getPatients());
                    recipients.addAll(SessionManager.getAdministrators());
            }
            
            // Send SMS to each recipient
            SMSNotification smsService = SessionManager.getSmsNotification();
            for (User user : recipients) {
                smsService.sendNotification(user.getPhone(), "RHMS Notification", message);
            }
            
            showAlert(Alert.AlertType.INFORMATION, "SMS Sent", 
                     "SMS notifications sent to " + recipients.size() + " recipients.");
            
            // Clear message
            smsMessageArea.clear();
        });
        
        smsContent.getChildren().addAll(smsRecipientLabel, smsRecipientCombo, 
                                      smsMessageLabel, smsMessageArea, sendSmsButton);
        smsTab.setContent(smsContent);
        
        // Email Tab
        Tab emailTab = new Tab("Email Notifications");
        VBox emailContent = new VBox(15);
        emailContent.setPadding(new Insets(20));
        
        Label emailRecipientLabel = new Label("Select Recipient Type:");
        ComboBox<String> emailRecipientCombo = new ComboBox<>();
        emailRecipientCombo.getItems().addAll("All Users", "Doctors", "Patients", "Administrators");
        emailRecipientCombo.setValue("All Users");
        
        Label emailSubjectLabel = new Label("Email Subject:");
        TextField emailSubjectField = new TextField();
        
        Label emailMessageLabel = new Label("Email Message:");
        TextArea emailMessageArea = new TextArea();
        emailMessageArea.setPrefRowCount(5);
        
        Button sendEmailButton = new Button("Send Email");
        sendEmailButton.setOnAction(e -> {
            String recipientType = emailRecipientCombo.getValue();
            String subject = emailSubjectField.getText();
            String message = emailMessageArea.getText();
            
            if (subject.isEmpty() || message.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Validation Error", 
                         "Subject and message cannot be empty.");
                return;
            }
            
            // Get recipients based on selection
            ArrayList<User> recipients = new ArrayList<>();
            switch (recipientType) {
                case "Doctors":
                    recipients.addAll(SessionManager.getDoctors());
                    break;
                case "Patients":
                    recipients.addAll(SessionManager.getPatients());
                    break;
                case "Administrators":
                    recipients.addAll(SessionManager.getAdministrators());
                    break;
                default:
                    recipients.addAll(SessionManager.getDoctors());
                    recipients.addAll(SessionManager.getPatients());
                    recipients.addAll(SessionManager.getAdministrators());
            }
            
            // Send email to each recipient
            EmailNotification emailService = SessionManager.getEmailNotification();
            for (User user : recipients) {
                emailService.sendNotification(user.getEmail(), subject, message);
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Email Sent", 
                     "Email notifications sent to " + recipients.size() + " recipients.");
            
            // Clear fields
            emailSubjectField.clear();
            emailMessageArea.clear();
        });
        
        emailContent.getChildren().addAll(emailRecipientLabel, emailRecipientCombo, 
                                        emailSubjectLabel, emailSubjectField,
                                        emailMessageLabel, emailMessageArea, 
                                        sendEmailButton);
        emailTab.setContent(emailContent);
        
        tabPane.getTabs().addAll(smsTab, emailTab);
        
        notificationsView.getChildren().addAll(headerLabel, tabPane);
        contentArea.getChildren().add(notificationsView);
    }
    
    @FXML
    private void handleViewAllAppointments(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox appointmentsView = new VBox(15);
        appointmentsView.setPadding(new Insets(20));
        appointmentsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("All Appointments");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all appointments
        ArrayList<Appointment> allAppointments = SessionManager.getAppointmentManager().getAppointments();
        
        // Create table
        TableView<Appointment> appointmentsTable = new TableView<>();
        
        // Create columns
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(date));
        });
        dateColumn.setPrefWidth(150);
        
        TableColumn<Appointment, String> doctorColumn = new TableColumn<>("Doctor");
        doctorColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDoctor().getName());
        });
        doctorColumn.setPrefWidth(150);
        
        TableColumn<Appointment, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPatient().getName());
        });
        patientColumn.setPrefWidth(150);
        
        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getStatus());
        });
        statusColumn.setPrefWidth(100);
        
        appointmentsTable.getColumns().add(dateColumn);
        appointmentsTable.getColumns().add(doctorColumn);
        appointmentsTable.getColumns().add(patientColumn);
        appointmentsTable.getColumns().add(statusColumn);
        appointmentsTable.setPrefHeight(400);
        
        // Add data to table
        ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList(allAppointments);
        appointmentsTable.setItems(appointmentsData);
        
        // Add placeholder text if no appointments
        appointmentsTable.setPlaceholder(new Label("No appointments found"));
        
        // Add buttons for various actions
        Button approveButton = new Button("Approve Selected");
        approveButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                if (selectedAppointment.getStatus().equals("Pending")) {
                    SessionManager.getAppointmentManager().approveAppointment(selectedAppointment);
                    
                    // Refresh table
                    appointmentsTable.refresh();
                    
                    showAlert(Alert.AlertType.INFORMATION, "Appointment Approved", 
                             "The appointment has been approved successfully.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Cannot Approve", 
                             "Only pending appointments can be approved.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select an appointment to approve.");
            }
        });
        
        Button cancelButton = new Button("Cancel Selected");
        cancelButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentsTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                if (!selectedAppointment.getStatus().equals("Cancelled")) {
                    // Confirm cancellation
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Cancel Appointment");
                    alert.setHeaderText("Cancel Appointment");
                    alert.setContentText("Are you sure you want to cancel this appointment?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        SessionManager.getAppointmentManager().cancelAppointment(selectedAppointment);
                        
                        // Refresh table
                        appointmentsTable.refresh();
                        
                        showAlert(Alert.AlertType.INFORMATION, "Appointment Cancelled", 
                                 "The appointment has been cancelled successfully.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Already Cancelled", 
                             "This appointment is already cancelled.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select an appointment to cancel.");
            }
        });
        
        Button refreshButton = new Button("Refresh");
        refreshButton.setOnAction(e -> {
            // Get fresh appointments list from AppointmentManager
            ArrayList<Appointment> refreshedAppointments = SessionManager.getAppointmentManager().getAppointments();
            
            // Update table without clearing the underlying data
            appointmentsData.clear();
            appointmentsData.addAll(refreshedAppointments);
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(approveButton, cancelButton, refreshButton);
        
        appointmentsView.getChildren().addAll(headerLabel, appointmentsTable, buttonBox);
        contentArea.getChildren().add(appointmentsView);
    }
    
    @FXML
    private void handleViewAllUsers(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox usersView = new VBox(15);
        usersView.setPadding(new Insets(20));
        usersView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Manage All Users");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Tabbed pane for different user types
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Doctors tab
        Tab doctorsTab = new Tab("Doctors");
        VBox doctorsContent = createUsersTable(SessionManager.getDoctors(), "Doctor");
        doctorsTab.setContent(doctorsContent);
        
        // Patients tab
        Tab patientsTab = new Tab("Patients");
        VBox patientsContent = createUsersTable(SessionManager.getPatients(), "Patient");
        patientsTab.setContent(patientsContent);
        
        // Administrators tab
        Tab adminsTab = new Tab("Administrators");
        VBox adminsContent = createUsersTable(SessionManager.getAdministrators(), "Administrator");
        adminsTab.setContent(adminsContent);
        
        tabPane.getTabs().addAll(doctorsTab, patientsTab, adminsTab);
        
        usersView.getChildren().addAll(headerLabel, tabPane);
        contentArea.getChildren().add(usersView);
    }
    
    private <T extends User> VBox createUsersTable(ArrayList<T> users, String userType) {
        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        
        TableView<T> usersTable = new TableView<>();
        
        // Create table columns
        TableColumn<T, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    String.valueOf(cellData.getValue().getUserID()));
        });
        idColumn.setPrefWidth(50);
        
        TableColumn<T, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getName());
        });
        nameColumn.setPrefWidth(150);
        
        TableColumn<T, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEmail());
        });
        emailColumn.setPrefWidth(200);
        
        TableColumn<T, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPhone());
        });
        phoneColumn.setPrefWidth(100);
        
        TableColumn<T, String> addressColumn = new TableColumn<>("Address");
        addressColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getAddress());
        });
        addressColumn.setPrefWidth(200);
        
        usersTable.getColumns().add(idColumn);
        usersTable.getColumns().add(nameColumn);
        usersTable.getColumns().add(emailColumn);
        usersTable.getColumns().add(phoneColumn);
        usersTable.getColumns().add(addressColumn);
        
        // Add specialization column only for doctors
        if (userType.equals("Doctor")) {
            TableColumn<T, String> specializationColumn = new TableColumn<>("Specialization");
            specializationColumn.setCellValueFactory(cellData -> {
                if (cellData.getValue() instanceof Doctor) {
                    return new javafx.beans.property.SimpleStringProperty(
                            ((Doctor) cellData.getValue()).getSpecialization());
                }
                return new javafx.beans.property.SimpleStringProperty("");
            });
            specializationColumn.setPrefWidth(150);
            
            usersTable.getColumns().add(specializationColumn);
        }
        
        usersTable.setPrefHeight(400);
        
        // Add data to table
        ObservableList<T> userData = FXCollections.observableArrayList(users);
        usersTable.setItems(userData);
        
        // Add placeholder text if no users
        usersTable.setPlaceholder(new Label("No " + userType.toLowerCase() + "s found"));
        
        // Add controls based on user type
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        
        if (!userType.equals("Administrator")) {
            Button removeButton = new Button("Remove Selected");
            removeButton.setOnAction(e -> {
                T selectedUser = usersTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    // Confirm removal
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Remove " + userType);
                    alert.setHeaderText("Remove " + userType);
                    alert.setContentText("Are you sure you want to remove " + selectedUser.getName() + "?");
                    
                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        // Remove user based on type
                        if (userType.equals("Doctor")) {
                            SessionManager.removeDoctor((Doctor) selectedUser);
                        } else if (userType.equals("Patient")) {
                            SessionManager.removePatient((Patient) selectedUser);
                        }
                        
                        // Remove from table
                        userData.remove(selectedUser);
                        
                        showAlert(Alert.AlertType.INFORMATION, userType + " Removed", 
                                 userType + " has been removed successfully.");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Selection", 
                             "Please select a " + userType.toLowerCase() + " to remove.");
                }
            });
            
            Button viewButton = new Button("View Details");
            viewButton.setOnAction(e -> {
                T selectedUser = usersTable.getSelectionModel().getSelectedItem();
                if (selectedUser != null) {
                    showUserDetailsDialog(selectedUser, userType);
                } else {
                    showAlert(Alert.AlertType.WARNING, "No Selection", 
                             "Please select a " + userType.toLowerCase() + " to view details.");
                }
            });
            
            buttonBox.getChildren().addAll(viewButton, removeButton);
        }
        
        content.getChildren().addAll(usersTable, buttonBox);
        return content;
    }
    
    private void showUserDetailsDialog(User user, String userType) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(userType + " Details");
        dialog.setHeaderText("Details for " + user.getName());
        
        // Create content
        GridPane gridPane = new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(20));
        
        gridPane.add(new Label("ID:"), 0, 0);
        gridPane.add(new Label(String.valueOf(user.getUserID())), 1, 0);
        
        gridPane.add(new Label("Name:"), 0, 1);
        gridPane.add(new Label(user.getName()), 1, 1);
        
        gridPane.add(new Label("Email:"), 0, 2);
        gridPane.add(new Label(user.getEmail()), 1, 2);
        
        gridPane.add(new Label("Phone:"), 0, 3);
        gridPane.add(new Label(user.getPhone()), 1, 3);
        
        gridPane.add(new Label("Address:"), 0, 4);
        gridPane.add(new Label(user.getAddress()), 1, 4);
        
        // Add doctor-specific details
        if (user instanceof Doctor) {
            Doctor doctor = (Doctor) user;
            
            gridPane.add(new Label("Specialization:"), 0, 5);
            gridPane.add(new Label(doctor.getSpecialization()), 1, 5);
        }
        
        // Add patient-specific details
        if (user instanceof Patient) {
            Patient patient = (Patient) user;
            
            // Check if patient has medical history
            if (patient.getMedicalHistory() != null) {
                int consultationCount = patient.getMedicalHistory().getPastConsultations().size();
                gridPane.add(new Label("Consultations:"), 0, 5);
                gridPane.add(new Label(String.valueOf(consultationCount)), 1, 5);
            }
            
            // Check if panic button is enabled
            if (patient.getPanicButton() != null) {
                gridPane.add(new Label("Panic Button:"), 0, 6);
                gridPane.add(new Label(patient.getPanicButton().isActive() ? "Active" : "Inactive"), 1, 6);
            }
        }
        
        dialog.getDialogPane().setContent(gridPane);
        
        // Add OK button
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        
        dialog.showAndWait();
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Log out user
            SessionManager.logout();
            
            // Return to login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            
            // Set the login scene
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("RHMS - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Error logging out: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    @FXML
    private void handleSystemReports(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox reportsView = new VBox(15);
        reportsView.setPadding(new Insets(20));
        reportsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("System Reports");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create report options
        VBox reportOptions = new VBox(15);
        reportOptions.setPadding(new Insets(20));
        reportOptions.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label optionsLabel = new Label("Generate Reports");
        optionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // User activity report
        Button userActivityButton = new Button("User Activity Report");
        userActivityButton.setPrefWidth(200);
        userActivityButton.setOnAction(e -> generateUserActivityReport());
        
        // Appointment statistics report
        Button appointmentStatsButton = new Button("Appointment Statistics");
        appointmentStatsButton.setPrefWidth(200);
        appointmentStatsButton.setOnAction(e -> generateAppointmentStatisticsReport());
        
        // System usage report
        Button systemUsageButton = new Button("System Usage Report");
        systemUsageButton.setPrefWidth(200);
        systemUsageButton.setOnAction(e -> generateSystemUsageReport());
        
        // All doctors report
        Button doctorsReportButton = new Button("Doctors Report");
        doctorsReportButton.setPrefWidth(200);
        doctorsReportButton.setOnAction(e -> generateDoctorsReport());
        
        // All patients report
        Button patientsReportButton = new Button("Patients Report");
        patientsReportButton.setPrefWidth(200);
        patientsReportButton.setOnAction(e -> generatePatientsReport());
        
        // Add email settings button to report options
        Button emailSettingsButton = new Button("Configure Email Settings");
        emailSettingsButton.setOnAction(e -> handleSystemSettings(new ActionEvent()));
        
        reportOptions.getChildren().addAll(
            optionsLabel, 
            userActivityButton,
            appointmentStatsButton,
            systemUsageButton,
            doctorsReportButton,
            patientsReportButton,
            emailSettingsButton
        );
        
        // Create activity log section
        VBox activityLogSection = new VBox(10);
        activityLogSection.setPadding(new Insets(20));
        activityLogSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label activityLabel = new Label("Recent System Activity");
        activityLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create a table to display recent activities
        TableView<SystemActivity> activityTable = new TableView<>();
        
        TableColumn<SystemActivity, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(dateFormat.format(cellData.getValue().getTimestamp())));
        timeColumn.setPrefWidth(150);
        
        TableColumn<SystemActivity, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getUserName()));
        userColumn.setPrefWidth(150);
        
        TableColumn<SystemActivity, String> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getAction()));
        actionColumn.setPrefWidth(300);
        
        activityTable.getColumns().clear();
        activityTable.getColumns().addAll(timeColumn, userColumn, actionColumn);
        activityTable.setPrefHeight(300);
        
        // Add sample data (in a real app, this would come from a log or database)
        ObservableList<SystemActivity> activityData = FXCollections.observableArrayList(
            createSampleActivityLog()
        );
        activityTable.setItems(activityData);
        
        Button refreshActivityButton = new Button("Refresh Activity Log");
        refreshActivityButton.setOnAction(e -> {
            activityData.clear();
            activityData.addAll(createSampleActivityLog());
        });
        
        activityLogSection.getChildren().addAll(activityLabel, activityTable, refreshActivityButton);
        
        reportsView.getChildren().addAll(headerLabel, reportOptions, activityLogSection);
        contentArea.getChildren().add(reportsView);
    }
    
    private ArrayList<SystemActivity> createSampleActivityLog() {
        ArrayList<SystemActivity> activities = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        // Add some sample activities - in a real app, these would come from actual system logs
        activities.add(new SystemActivity(new Date(currentTime - 5 * 60000), 
                                        "Admin (ID: " + currentAdmin.getUserID() + ")", 
                                        "Logged into the system"));
        
        activities.add(new SystemActivity(new Date(currentTime - 10 * 60000), 
                                        "System", 
                                        "Scheduled daily backup"));
        
        if (!SessionManager.getDoctors().isEmpty()) {
            Doctor sampleDoctor = SessionManager.getDoctors().get(0);
            activities.add(new SystemActivity(new Date(currentTime - 30 * 60000), 
                                           "Dr. " + sampleDoctor.getName(), 
                                           "Updated patient medical records"));
        }
        
        if (!SessionManager.getPatients().isEmpty()) {
            Patient samplePatient = SessionManager.getPatients().get(0);
            activities.add(new SystemActivity(new Date(currentTime - 45 * 60000), 
                                           samplePatient.getName(), 
                                           "Uploaded new vital signs data"));
        }
        
        activities.add(new SystemActivity(new Date(currentTime - 120 * 60000), 
                                        "System", 
                                        "Automatic notification sent to 3 patients for upcoming appointments"));
                                        
        return activities;
    }
    
    private void generateUserActivityReport() {
        ReportsGenerator reportsGenerator = new ReportsGenerator();
        try {
            String filename = "User_Activity_Report_" + DATE_FORMAT.format(new Date()) + ".txt";
            reportsGenerator.generateReport(filename, "System Usage Report");
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                    "User Activity Report has been generated at: " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    private void generateAppointmentStatisticsReport() {
        ReportsGenerator reportsGenerator = new ReportsGenerator();
        try {
            String filename = "Appointment_Statistics_" + DATE_FORMAT.format(new Date()) + ".txt";
            reportsGenerator.generateReport(filename, "Appointment Statistics");
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                    "Appointment Statistics Report has been generated at: " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    private void generateSystemUsageReport() {
        ReportsGenerator reportsGenerator = new ReportsGenerator();
        try {
            String filename = "System_Usage_Report_" + DATE_FORMAT.format(new Date()) + ".txt";
            reportsGenerator.generateReport(filename, "System Usage Report");
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                    "System Usage Report has been generated at: " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    private void generateDoctorsReport() {
        ReportsGenerator reportsGenerator = new ReportsGenerator();
        try {
            String filename = "Doctors_Report_" + DATE_FORMAT.format(new Date()) + ".txt";
            reportsGenerator.generateReport(filename, "Doctor Performance Report");
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                    "Doctors Report has been generated at: " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    private void generatePatientsReport() {
        ReportsGenerator reportsGenerator = new ReportsGenerator();
        try {
            String filename = "Patients_Report_" + DATE_FORMAT.format(new Date()) + ".txt";
            reportsGenerator.generateReport(filename, "Patient Activity Report");
            showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                    "Patients Report has been generated at: " + filename);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleEmergencyAlerts(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox emergencyView = new VBox(15);
        emergencyView.setPadding(new Insets(20));
        emergencyView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Emergency Alert Management");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create active alerts section
        VBox activeAlertsSection = new VBox(10);
        activeAlertsSection.setPadding(new Insets(20));
        activeAlertsSection.setStyle("-fx-border-color: #ff6666; -fx-border-radius: 5px; -fx-background-color: #fff8f8;");
        
        Label activeAlertsLabel = new Label("Active Emergency Alerts");
        activeAlertsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #cc0000;");
        
        // Create a table to display active alerts
        TableView<EmergencyAlertData> alertsTable = new TableView<>();
        
        TableColumn<EmergencyAlertData, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(dateFormat.format(cellData.getValue().getTimestamp())));
        timeColumn.setPrefWidth(150);
        
        TableColumn<EmergencyAlertData, String> patientColumn = new TableColumn<>("Patient");
        patientColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getPatientName()));
        patientColumn.setPrefWidth(150);
        
        TableColumn<EmergencyAlertData, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getStatus()));
        statusColumn.setPrefWidth(100);
        
        TableColumn<EmergencyAlertData, String> locationColumn = new TableColumn<>("Location");
        locationColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(cellData.getValue().getLocation()));
        locationColumn.setPrefWidth(200);
        
        alertsTable.getColumns().clear();
        alertsTable.getColumns().addAll(timeColumn, patientColumn, statusColumn, locationColumn);
        alertsTable.setPrefHeight(200);
        
        // Add sample data (in a real app, this would come from EmergencyAlert system)
        ObservableList<EmergencyAlertData> alertsData = FXCollections.observableArrayList(
            createSampleEmergencyAlerts()
        );
        alertsTable.setItems(alertsData);
        
        Button respondButton = new Button("Respond to Selected Alert");
        respondButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white;");
        respondButton.setOnAction(e -> {
            EmergencyAlertData selectedAlert = alertsTable.getSelectionModel().getSelectedItem();
            if (selectedAlert != null) {
                handleEmergencyResponse(selectedAlert);
                alertsData.remove(selectedAlert);
                
                if (alertsData.isEmpty()) {
                    // Add a placeholder when all alerts are handled
                    Label noAlertsLabel = new Label("No active emergency alerts");
                    noAlertsLabel.setStyle("-fx-font-style: italic;");
                    activeAlertsSection.getChildren().clear();
                    activeAlertsSection.getChildren().addAll(activeAlertsLabel, alertsTable, noAlertsLabel);
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                         "Please select an alert to respond to.");
            }
        });
        
        Button refreshAlertsButton = new Button("Refresh Alerts");
        refreshAlertsButton.setOnAction(e -> {
            alertsData.clear();
            alertsData.addAll(createSampleEmergencyAlerts());
        });
        
        HBox alertButtonsBox = new HBox(10);
        alertButtonsBox.setAlignment(Pos.CENTER);
        alertButtonsBox.getChildren().addAll(respondButton, refreshAlertsButton);
        
        activeAlertsSection.getChildren().addAll(activeAlertsLabel, alertsTable, alertButtonsBox);
        
        // Create alert configuration section
        VBox alertConfigSection = new VBox(10);
        alertConfigSection.setPadding(new Insets(20));
        alertConfigSection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label configLabel = new Label("Emergency Alert Configuration");
        configLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane configGrid = new GridPane();
        configGrid.setHgap(10);
        configGrid.setVgap(10);
        configGrid.setPadding(new Insets(10));
        
        configGrid.add(new Label("Auto-notify emergency services:"), 0, 0);
        CheckBox autoNotifyCheckbox = new CheckBox();
        autoNotifyCheckbox.setSelected(true);
        configGrid.add(autoNotifyCheckbox, 1, 0);
        
        configGrid.add(new Label("Auto-assign doctor for emergency:"), 0, 1);
        CheckBox autoAssignCheckbox = new CheckBox();
        autoAssignCheckbox.setSelected(true);
        configGrid.add(autoAssignCheckbox, 1, 1);
        
        configGrid.add(new Label("Send SMS to emergency contacts:"), 0, 2);
        CheckBox smsContactsCheckbox = new CheckBox();
        smsContactsCheckbox.setSelected(true);
        configGrid.add(smsContactsCheckbox, 1, 2);
        
        configGrid.add(new Label("Response time threshold (minutes):"), 0, 3);
        Spinner<Integer> timeThresholdSpinner = new Spinner<>(1, 30, 5);
        configGrid.add(timeThresholdSpinner, 1, 3);
        
        Button saveConfigButton = new Button("Save Configuration");
        saveConfigButton.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Settings Saved", 
                    "Emergency alert settings have been updated successfully.");
        });
        
        alertConfigSection.getChildren().addAll(configLabel, configGrid, saveConfigButton);
        
        // Create alert history section
        VBox alertHistorySection = new VBox(10);
        alertHistorySection.setPadding(new Insets(20));
        alertHistorySection.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label historyLabel = new Label("Emergency Alert History");
        historyLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        DatePicker startDatePicker = new DatePicker();
        startDatePicker.setValue(java.time.LocalDate.now().minusDays(7));
        startDatePicker.setPromptText("Start Date");
        
        DatePicker endDatePicker = new DatePicker();
        endDatePicker.setValue(java.time.LocalDate.now());
        endDatePicker.setPromptText("End Date");
        
        Button viewHistoryButton = new Button("View History");
        viewHistoryButton.setOnAction(e -> {
            showAlert(Alert.AlertType.INFORMATION, "Alert History", 
                    "Emergency alert history would be shown for the selected date range.");
        });
        
        HBox dateRangeBox = new HBox(10);
        dateRangeBox.setAlignment(Pos.CENTER);
        dateRangeBox.getChildren().addAll(
            new Label("From:"), startDatePicker,
            new Label("To:"), endDatePicker,
            viewHistoryButton
        );
        
        alertHistorySection.getChildren().addAll(historyLabel, dateRangeBox);
        
        emergencyView.getChildren().addAll(headerLabel, activeAlertsSection, alertConfigSection, alertHistorySection);
        contentArea.getChildren().add(emergencyView);
    }
    
    private ArrayList<EmergencyAlertData> createSampleEmergencyAlerts() {
        ArrayList<EmergencyAlertData> alerts = new ArrayList<>();
        
        // Check if there are any patients in the system
        if (!SessionManager.getPatients().isEmpty()) {
            // Add random alerts for some patients
            ArrayList<Patient> patients = SessionManager.getPatients();
            Random random = new Random();
            
            // Determine how many alerts to create (0-2)
            int alertCount = random.nextInt(3);
            
            for (int i = 0; i < alertCount && i < patients.size(); i++) {
                Patient patient = patients.get(i);
                alerts.add(new EmergencyAlertData(
                    new Date(System.currentTimeMillis() - (random.nextInt(30) * 60000)),
                    patient.getName(),
                    patient.getUserID(),
                    "URGENT",
                    patient.getAddress()
                ));
            }
        }
        
        return alerts;
    }
    
    private void handleEmergencyResponse(EmergencyAlertData alert) {
        // In a real app, this would send notifications and track response
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Emergency Response");
        dialog.setHeaderText("Responding to emergency alert for " + alert.getPatientName());
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));
        
        grid.add(new Label("Patient:"), 0, 0);
        grid.add(new Label(alert.getPatientName()), 1, 0);
        
        grid.add(new Label("Alert Time:"), 0, 1);
        grid.add(new Label(dateFormat.format(alert.getTimestamp())), 1, 1);
        
        grid.add(new Label("Location:"), 0, 2);
        grid.add(new Label(alert.getLocation()), 1, 2);
        
        grid.add(new Label("Assign Doctor:"), 0, 3);
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        
        // Display doctor names in dropdown
        doctorComboBox.setCellFactory(param -> new ListCell<Doctor>() {
            @Override
            protected void updateItem(Doctor item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName() + " (" + item.getSpecialization() + ")");
                }
            }
        });
        
        // Fill dropdown with doctors
        ArrayList<Doctor> doctors = SessionManager.getDoctors();
        doctorComboBox.setItems(FXCollections.observableArrayList(doctors));
        if (!doctors.isEmpty()) {
            doctorComboBox.setValue(doctors.get(0));
        }
        
        grid.add(doctorComboBox, 1, 3);
        
        grid.add(new Label("Notify Emergency Services:"), 0, 4);
        CheckBox notifyEmergencyCheckbox = new CheckBox();
        notifyEmergencyCheckbox.setSelected(true);
        grid.add(notifyEmergencyCheckbox, 1, 4);
        
        grid.add(new Label("Notes:"), 0, 5);
        TextArea notesArea = new TextArea();
        notesArea.setPrefRowCount(3);
        grid.add(notesArea, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        ButtonType sendResponseButton = new ButtonType("Send Response", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendResponseButton, ButtonType.CANCEL);
        
        Optional<Void> result = dialog.showAndWait();
        
        if (result.isPresent()) {
            showAlert(Alert.AlertType.INFORMATION, "Response Sent", 
                     "Emergency response has been dispatched for " + alert.getPatientName() + ".");
        }
    }
    
    @FXML
    private void handleSystemSettings(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox settingsView = new VBox(15);
        settingsView.setPadding(new Insets(20));
        settingsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("System Settings");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Create sections
        VBox emailSection = createEmailSettingsSection();
        
        // Add all sections to the view
        settingsView.getChildren().addAll(headerLabel, emailSection);
        
        contentArea.getChildren().add(settingsView);
    }
    
    private VBox createEmailSettingsSection() {
        VBox section = new VBox(10);
        section.setPadding(new Insets(10));
        section.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label sectionLabel = new Label("Email Settings");
        sectionLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Get current email settings
        EmailNotification emailService = SessionManager.getEmailNotification();
        
        // Create form fields
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setPadding(new Insets(10));
        
        Label hostLabel = new Label("SMTP Host:");
        TextField hostField = new TextField("smtp.gmail.com");
        
        Label portLabel = new Label("SMTP Port:");
        TextField portField = new TextField("587");
        
        Label usernameLabel = new Label("Email Username:");
        TextField usernameField = new TextField();
        
        Label passwordLabel = new Label("Email Password:");
        PasswordField passwordField = new PasswordField();
        
        CheckBox enableEmailCheckbox = new CheckBox("Enable Real Email Sending");
        
        Button testEmailButton = new Button("Test Email Connection");
        testEmailButton.setOnAction(e -> {
            try {
                // Get values from form
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                String password = passwordField.getText();
                boolean enableEmail = enableEmailCheckbox.isSelected();
                
                // Temporarily set email configuration for test
                emailService.setEmailConfig(host, port, username, password);
                emailService.setSimulateMode(!enableEmail);
                
                // Send test email to admin
                String adminEmail = currentAdmin.getEmail();
                emailService.sendNotification(
                    adminEmail,
                    "RHMS Email Test",
                    "This is a test email from RHMS. If you received this, email is working correctly."
                );
                
                showAlert(Alert.AlertType.INFORMATION, "Test Email", 
                        enableEmail ? "Test email sent to " + adminEmail : 
                                  "Email simulation mode is enabled. Check console for details.");
                
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Port", 
                        "Please enter a valid port number.");
            }
        });
        
        Button saveButton = new Button("Save Email Settings");
        saveButton.setOnAction(e -> {
            try {
                // Get values from form
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                String password = passwordField.getText();
                boolean enableEmail = enableEmailCheckbox.isSelected();
                
                // Update email service configuration
                emailService.setEmailConfig(host, port, username, password);
                emailService.setSimulateMode(!enableEmail);
                
                showAlert(Alert.AlertType.INFORMATION, "Settings Saved", 
                        "Email settings have been saved successfully.");
                
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Port", 
                        "Please enter a valid port number.");
            }
        });
        
        // Add elements to form
        formGrid.add(hostLabel, 0, 0);
        formGrid.add(hostField, 1, 0);
        formGrid.add(portLabel, 0, 1);
        formGrid.add(portField, 1, 1);
        formGrid.add(usernameLabel, 0, 2);
        formGrid.add(usernameField, 1, 2);
        formGrid.add(passwordLabel, 0, 3);
        formGrid.add(passwordField, 1, 3);
        formGrid.add(enableEmailCheckbox, 0, 4, 2, 1);
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(testEmailButton, saveButton);
        
        section.getChildren().addAll(sectionLabel, formGrid, buttonBox);
        
        return section;
    }
    
    @FXML
    private void handleSystemMonitoring(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox monitoringView = new VBox(15);
        monitoringView.setPadding(new Insets(20));
        monitoringView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("System Monitoring");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // System statistics section
        VBox statsBox = new VBox(10);
        statsBox.setPadding(new Insets(15));
        statsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label statsLabel = new Label("System Statistics");
        statsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create grid for statistics
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(20);
        statsGrid.setVgap(10);
        
        // Total users
        statsGrid.add(new Label("Total Users:"), 0, 0);
        int totalUsers = SessionManager.getPatients().size() + 
                         SessionManager.getDoctors().size() + 
                         SessionManager.getAdministrators().size();
        statsGrid.add(new Label(String.valueOf(totalUsers)), 1, 0);
        
        // Total patients
        statsGrid.add(new Label("Total Patients:"), 0, 1);
        statsGrid.add(new Label(String.valueOf(SessionManager.getPatients().size())), 1, 1);
        
        // Total doctors
        statsGrid.add(new Label("Total Doctors:"), 0, 2);
        statsGrid.add(new Label(String.valueOf(SessionManager.getDoctors().size())), 1, 2);
        
        // Total appointments
        statsGrid.add(new Label("Total Appointments:"), 0, 3);
        statsGrid.add(new Label(String.valueOf(SessionManager.getAppointmentManager().getAppointments().size())), 1, 3);
        
        // Pending appointments
        int pendingCount = 0;
        for (Appointment appt : SessionManager.getAppointmentManager().getAppointments()) {
            if (appt.getStatus().equals("Pending")) pendingCount++;
        }
        statsGrid.add(new Label("Pending Appointments:"), 0, 4);
        statsGrid.add(new Label(String.valueOf(pendingCount)), 1, 4);
        
        // Current active emergency alerts
        int activeAlerts = 0;
        // Count emergency alerts from patients with active panic buttons
        for (Patient patient : SessionManager.getPatients()) {
            if (patient.getPanicButton() != null && patient.getPanicButton().isActive()) {
                activeAlerts++;
            }
        }
        statsGrid.add(new Label("Active Emergency Alerts:"), 0, 5);
        Label alertsLabel = new Label(String.valueOf(activeAlerts));
        alertsLabel.setStyle(activeAlerts > 0 ? "-fx-text-fill: red; -fx-font-weight: bold;" : "");
        statsGrid.add(alertsLabel, 1, 5);
        
        statsBox.getChildren().addAll(statsLabel, statsGrid);
        
        // Active users section
        VBox activeUsersBox = new VBox(10);
        activeUsersBox.setPadding(new Insets(15));
        activeUsersBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label activeUsersLabel = new Label("Recent User Activity");
        activeUsersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create table for active users
        TableView<UserActivity> activityTable = new TableView<>();
        
        TableColumn<UserActivity, String> userColumn = new TableColumn<>("User");
        userColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getUserName()));
        userColumn.setPrefWidth(150);
        
        TableColumn<UserActivity, String> activityTypeColumn = new TableColumn<>("Activity");
        activityTypeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getActivityType()));
        activityTypeColumn.setPrefWidth(150);
        
        TableColumn<UserActivity, String> timeColumn = new TableColumn<>("Time");
        timeColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(
                dateFormat.format(cellData.getValue().getTimestamp())));
        timeColumn.setPrefWidth(150);
        
        activityTable.getColumns().clear();
        activityTable.getColumns().addAll(userColumn, activityTypeColumn, timeColumn);
        activityTable.setPrefHeight(200);
        
        // Add sample data
        ObservableList<UserActivity> activityData = FXCollections.observableArrayList(
            new UserActivity("Dr. Abdullah", "Login", new Date(System.currentTimeMillis() - 5 * 60000)),
            new UserActivity("Ali", "Viewed Appointments", new Date(System.currentTimeMillis() - 10 * 60000)),
            new UserActivity("Admin User", "Created User", new Date(System.currentTimeMillis() - 15 * 60000))
        );
        activityTable.setItems(activityData);
        
        activeUsersBox.getChildren().addAll(activeUsersLabel, activityTable);
        
        // System health monitoring
        VBox systemHealthBox = new VBox(10);
        systemHealthBox.setPadding(new Insets(15));
        systemHealthBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label systemHealthLabel = new Label("System Health");
        systemHealthLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create grid for health metrics
        GridPane healthGrid = new GridPane();
        healthGrid.setHgap(20);
        healthGrid.setVgap(10);
        
        healthGrid.add(new Label("Server Status:"), 0, 0);
        Label serverStatusLabel = new Label("Online");
        serverStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        healthGrid.add(serverStatusLabel, 1, 0);
        
        healthGrid.add(new Label("Database Status:"), 0, 1);
        Label dbStatusLabel = new Label("Connected");
        dbStatusLabel.setStyle("-fx-text-fill: green; -fx-font-weight: bold;");
        healthGrid.add(dbStatusLabel, 1, 1);
        
        healthGrid.add(new Label("Last Backup:"), 0, 2);
        healthGrid.add(new Label(dateFormat.format(new Date(System.currentTimeMillis() - 24 * 60 * 60000))), 1, 2);
        
        healthGrid.add(new Label("System Memory:"), 0, 3);
        ProgressBar memoryBar = new ProgressBar(0.45);
        memoryBar.setPrefWidth(150);
        healthGrid.add(memoryBar, 1, 3);
        
        Button refreshButton = new Button("Refresh Status");
        refreshButton.setOnAction(e -> {
            // In a real system, this would refresh actual system metrics
            showAlert(Alert.AlertType.INFORMATION, "Refresh", "System status refreshed.");
        });
        
        systemHealthBox.getChildren().addAll(systemHealthLabel, healthGrid, refreshButton);
        
        monitoringView.getChildren().addAll(headerLabel, statsBox, activeUsersBox, systemHealthBox);
        contentArea.getChildren().add(monitoringView);
    }
    
    // Helper class for user activity
    private class UserActivity {
        private String userName;
        private String activityType;
        private Date timestamp;
        
        public UserActivity(String userName, String activityType, Date timestamp) {
            this.userName = userName;
            this.activityType = activityType;
            this.timestamp = timestamp;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public String getActivityType() {
            return activityType;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
    }
    
    // Helper class for System Activity logging
    private class SystemActivity {
        private Date timestamp;
        private String userName;
        private String action;
        
        public SystemActivity(Date timestamp, String userName, String action) {
            this.timestamp = timestamp;
            this.userName = userName;
            this.action = action;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
        
        public String getUserName() {
            return userName;
        }
        
        public String getAction() {
            return action;
        }
    }
    
    // Helper class for Emergency Alerts data
    private class EmergencyAlertData {
        private Date timestamp;
        private String patientName;
        private int patientId;
        private String status;
        private String location;
        
        public EmergencyAlertData(Date timestamp, String patientName, int patientId, 
                                 String status, String location) {
            this.timestamp = timestamp;
            this.patientName = patientName;
            this.patientId = patientId;
            this.status = status;
            this.location = location;
        }
        
        public Date getTimestamp() {
            return timestamp;
        }
        
        public String getPatientName() {
            return patientName;
        }
        
        public int getPatientId() {
            return patientId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public String getLocation() {
            return location;
        }
    }
    
    private void configureEmailSettings() {
        // Create a dialog for email settings
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Email Configuration");
        dialog.setHeaderText("Configure Email Server Settings");
        
        // Create form content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField hostField = new TextField();
        hostField.setText("smtp.gmail.com");
        hostField.setPromptText("e.g., smtp.gmail.com");
        
        TextField portField = new TextField();
        portField.setText("587");
        portField.setPromptText("e.g., 587 for TLS, 465 for SSL");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Your email address");
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("For Gmail, use App Password");
        
        CheckBox enabledCheckbox = new CheckBox("Enable Real Email Sending");
        enabledCheckbox.setSelected(false);
        
        // Instructions for Gmail
        VBox helpBox = new VBox(5);
        helpBox.setPadding(new Insets(10));
        helpBox.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #dee2e6; -fx-border-radius: 5px;");
        
        Label helpLabel = new Label("Gmail Setup Instructions:");
        helpLabel.setStyle("-fx-font-weight: bold;");
        
        VBox instructions = new VBox(5);
        instructions.getChildren().addAll(
            new Label("1. Use your Gmail address as the username"),
            new Label("2. For password, create an 'App Password' in your Google Account"),
            new Label("3. Go to Google Account > Security > App Passwords"),
            new Label("4. Select 'Mail' for app and 'Other' for device (name it 'RHMS')"),
            new Label("5. Use the generated 16-character password")
        );
        
        helpBox.getChildren().addAll(helpLabel, instructions);
        
        grid.add(new Label("SMTP Host:"), 0, 0);
        grid.add(hostField, 1, 0);
        grid.add(new Label("SMTP Port:"), 0, 1);
        grid.add(portField, 1, 1);
        grid.add(new Label("Username:"), 0, 2);
        grid.add(usernameField, 1, 2);
        grid.add(new Label("Password:"), 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(enabledCheckbox, 0, 4, 2, 1);
        grid.add(helpBox, 0, 5, 2, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Add buttons
        ButtonType testButtonType = new ButtonType("Test Connection", ButtonBar.ButtonData.OTHER);
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(testButtonType, saveButtonType, ButtonType.CANCEL);
        
        // Get the Test button and add an action
        Button testButton = (Button) dialog.getDialogPane().lookupButton(testButtonType);
        testButton.setOnAction(e -> {
            try {
                String host = hostField.getText();
                int port = Integer.parseInt(portField.getText());
                String username = usernameField.getText();
                String password = passwordField.getText();
                boolean enabled = enabledCheckbox.isSelected();
                
                // Validate required fields
                if (username.isEmpty() || password.isEmpty()) {
                    showAlert(Alert.AlertType.ERROR, "Missing Fields", 
                            "Username and password are required.");
                    return;
                }
                
                // Update email notification service temporarily for testing
                EmailNotification emailService = SessionManager.getEmailNotification();
                emailService.setEmailConfig(host, port, username, password);
                emailService.setSimulateMode(!enabled);
                
                // Send test email
                String adminEmail = currentAdmin.getEmail();
                emailService.sendNotification(
                    adminEmail,
                    "RHMS Email Test",
                    "This is a test email from your RHMS system. If you received this, your email configuration is working correctly."
                );
                
                showAlert(Alert.AlertType.INFORMATION, "Test Email", 
                        enabled ? "Test email sent to " + adminEmail + ". Please check your inbox." : 
                                "Test email simulated (simulation mode is ON). Check the console for details.");
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                        "Please enter a valid port number.");
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Email Test Failed", 
                        "Error: " + ex.getMessage());
            }
        });
        
        // Set the result converter for the Save button
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    String host = hostField.getText();
                    int port = Integer.parseInt(portField.getText());
                    String username = usernameField.getText();
                    String password = passwordField.getText();
                    boolean enabled = enabledCheckbox.isSelected();
                    
                    // Validate required fields
                    if (username.isEmpty() || password.isEmpty()) {
                        showAlert(Alert.AlertType.ERROR, "Missing Fields", 
                                "Username and password are required.");
                        return null;
                    }
                    
                    // Update email notification service
                    EmailNotification emailService = SessionManager.getEmailNotification();
                    emailService.setEmailConfig(host, port, username, password);
                    emailService.setSimulateMode(!enabled);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Settings Saved", 
                            "Email configuration has been updated." + 
                            (enabled ? " Real emails will now be sent." : " Email simulation mode is still enabled."));
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                            "Please enter a valid port number.");
                }
            }
            return null;
        });
        
        dialog.showAndWait();
    }
    
    @FXML
    private void handleEmailSettings(ActionEvent event) {
        // Call the existing configuration dialog
        configureEmailSettings();
    }
}