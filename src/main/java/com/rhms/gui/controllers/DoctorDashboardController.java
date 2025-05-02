package com.rhms.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.Random;

import com.rhms.analytics.AnalyticsResult;
import com.rhms.analytics.ChartGenerator;
import com.rhms.analytics.HealthAnalytics;
import com.rhms.appointmentScheduling.Appointment;
import com.rhms.doctorPatientInteraction.ChatClient;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.Prescription;
import com.rhms.doctorPatientInteraction.VideoCall;
import com.rhms.gui.SessionManager;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.reporting.ReportsGenerator;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;

public class DoctorDashboardController {
    @FXML private Label userNameLabel;
    @FXML private StackPane contentArea;
    
    private Doctor currentDoctor;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    @FXML
    public void initialize() {
        // Get current doctor from session
        currentDoctor = (Doctor) SessionManager.getCurrentUser();
        
        if (currentDoctor != null) {
            userNameLabel.setText("Welcome, Dr. " + currentDoctor.getName());
        }
    }
    
    @FXML
    private void handleViewSchedule(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox scheduleView = new VBox(15);
        scheduleView.setPadding(new Insets(20));
        scheduleView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("My Schedule");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get active appointments for the current doctor
        ArrayList<Appointment> appointments = SessionManager.getAppointmentManager()
                .getDoctorActiveAppointments(currentDoctor);
        
        TableView<Appointment> appointmentTable = new TableView<>();
        
        // Create columns
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(date));
        });
        dateColumn.setPrefWidth(150);
        
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
        
        appointmentTable.getColumns().addAll(dateColumn, patientColumn, statusColumn);
        appointmentTable.setPrefHeight(300);
        
        // Add data to table
        ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList(appointments);
        appointmentTable.setItems(appointmentsData);
        
        // Add placeholder text if no appointments
        appointmentTable.setPlaceholder(new Label("No appointments scheduled"));
        
        // Add button for starting video consultation
        Button startVideoButton = new Button("Start Video Consultation");
        startVideoButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                if (selectedAppointment.getStatus().equals("Approved")) {
                    handleStartVideoConsultation(selectedAppointment.getPatient());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Approved", 
                            "Only approved appointments can start video consultations.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select an appointment to start a video consultation.");
            }
        });
        
        // Add button for canceling selected appointment
        Button cancelButton = new Button("Cancel Selected Appointment");
        cancelButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                // Confirm cancellation
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cancel Appointment");
                alert.setHeaderText("Cancel Appointment");
                alert.setContentText("Are you sure you want to cancel this appointment?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Cancel the appointment
                    SessionManager.getAppointmentManager().cancelAppointment(selectedAppointment);
                    
                    // Update the table
                    appointmentsData.remove(selectedAppointment);
                    
                    // Show confirmation
                    showAlert(Alert.AlertType.INFORMATION, "Appointment Cancelled", 
                            "The appointment has been cancelled successfully.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select an appointment to cancel.");
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(startVideoButton, cancelButton);
        
        scheduleView.getChildren().addAll(headerLabel, appointmentTable, buttonBox);
        contentArea.getChildren().add(scheduleView);
    }
    
    @FXML
    private void handlePendingAppointments(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox pendingView = new VBox(15);
        pendingView.setPadding(new Insets(20));
        pendingView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Pending Appointments");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get pending appointments for the current doctor
        ArrayList<Appointment> pendingAppointments = SessionManager.getAppointmentManager()
                .getDoctorPendingAppointments(currentDoctor);
        
        TableView<Appointment> appointmentTable = new TableView<>();
        
        // Create columns
        TableColumn<Appointment, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(cellData -> {
            Date date = cellData.getValue().getAppointmentDate();
            return new javafx.beans.property.SimpleStringProperty(
                    dateFormat.format(date));
        });
        dateColumn.setPrefWidth(150);
        
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
        
        appointmentTable.getColumns().addAll(dateColumn, patientColumn, statusColumn);
        appointmentTable.setPrefHeight(300);
        
        // Add data to table
        ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList(pendingAppointments);
        appointmentTable.setItems(appointmentsData);
        
        // Add placeholder text if no appointments
        appointmentTable.setPlaceholder(new Label("No pending appointments"));
        
        // Add buttons for approving or rejecting appointments
        Button approveButton = new Button("Approve Selected");
        approveButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                // Approve the appointment
                SessionManager.getAppointmentManager().approveAppointment(selectedAppointment);
                
                // Update the table (remove from pending list)
                appointmentsData.remove(selectedAppointment);
                
                // Show confirmation
                showAlert(Alert.AlertType.INFORMATION, "Appointment Approved", 
                        "The appointment has been approved successfully.");
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select an appointment to approve.");
            }
        });
        
        Button rejectButton = new Button("Reject Selected");
        rejectButton.setOnAction(e -> {
            Appointment selectedAppointment = appointmentTable.getSelectionModel().getSelectedItem();
            if (selectedAppointment != null) {
                // Confirm rejection
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Reject Appointment");
                alert.setHeaderText("Reject Appointment");
                alert.setContentText("Are you sure you want to reject this appointment?");
                
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Cancel (reject) the appointment
                    SessionManager.getAppointmentManager().cancelAppointment(selectedAppointment);
                    
                    // Update the table
                    appointmentsData.remove(selectedAppointment);
                    
                    // Show confirmation
                    showAlert(Alert.AlertType.INFORMATION, "Appointment Rejected", 
                            "The appointment has been rejected successfully.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select an appointment to reject.");
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(approveButton, rejectButton);
        
        pendingView.getChildren().addAll(headerLabel, appointmentTable, buttonBox);
        contentArea.getChildren().add(pendingView);
    }
    
    @FXML
    private void handlePatientRecords(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox patientRecordsView = new VBox(15);
        patientRecordsView.setPadding(new Insets(20));
        patientRecordsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Patient Records");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all patients in the system
        ArrayList<Patient> patients = SessionManager.getPatients();
        
        TableView<Patient> patientTable = new TableView<>();
        
        // Create columns
        TableColumn<Patient, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getName());
        });
        nameColumn.setPrefWidth(150);
        
        TableColumn<Patient, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getEmail());
        });
        emailColumn.setPrefWidth(200);
        
        TableColumn<Patient, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getPhone());
        });
        phoneColumn.setPrefWidth(150);
        
        patientTable.getColumns().addAll(nameColumn, emailColumn, phoneColumn);
        patientTable.setPrefHeight(300);
        
        // Add data to table
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(patients);
        patientTable.setItems(patientsData);
        
        // Add placeholder text if no patients
        patientTable.setPlaceholder(new Label("No patients in the system"));
        
        // Add buttons for viewing patient details, vitals, and adding feedback
        Button viewDetailsButton = new Button("View Medical History");
        viewDetailsButton.setOnAction(e -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                handleViewPatientHistory(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select a patient to view their medical history.");
            }
        });
        
        Button viewVitalsButton = new Button("View Vitals");
        viewVitalsButton.setOnAction(e -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                handleViewPatientVitals(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select a patient to view their vitals.");
            }
        });
        
        Button addFeedbackButton = new Button("Add Feedback/Prescription");
        addFeedbackButton.setOnAction(e -> {
            Patient selectedPatient = patientTable.getSelectionModel().getSelectedItem();
            if (selectedPatient != null) {
                handleProvideFeedback(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select a patient to add feedback or prescription.");
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(viewDetailsButton, viewVitalsButton, addFeedbackButton);
        
        patientRecordsView.getChildren().addAll(headerLabel, patientTable, buttonBox);
        contentArea.getChildren().add(patientRecordsView);
    }
    
    private void handleViewPatientHistory(Patient patient) {
        // Create a popup dialog to display patient history
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Medical History: " + patient.getName());
        dialog.setHeaderText("Medical History and Feedback for " + patient.getName());
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        Label medicalHistoryLabel = new Label("Past Consultations:");
        medicalHistoryLabel.setStyle("-fx-font-weight: bold;");
        
        VBox historyContent = new VBox(5);
        
        MedicalHistory medHistory = patient.getMedicalHistory();
        if (medHistory != null && !medHistory.getPastConsultations().isEmpty()) {
            for (Feedback feedback : medHistory.getPastConsultations()) {
                Label doctorLabel = new Label("Doctor: Dr. " + feedback.getDoctor().getName());
                Label commentsLabel = new Label("Feedback: " + feedback.getComments());
                
                VBox feedbackBox = new VBox(2);
                feedbackBox.getChildren().addAll(doctorLabel, commentsLabel);
                
                Prescription prescription = feedback.getPrescription();
                if (prescription != null) {
                    Label medicationLabel = new Label("Medication: " + prescription.getMedicationName());
                    Label dosageLabel = new Label("Dosage: " + prescription.getDosage());
                    Label scheduleLabel = new Label("Schedule: " + prescription.getSchedule());
                    
                    feedbackBox.getChildren().addAll(medicationLabel, dosageLabel, scheduleLabel);
                }
                
                Separator separator = new Separator();
                
                historyContent.getChildren().addAll(feedbackBox, separator);
            }
        } else {
            historyContent.getChildren().add(new Label("No medical history available."));
        }
        
        ScrollPane scrollPane = new ScrollPane(historyContent);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(300);
        
        content.getChildren().addAll(medicalHistoryLabel, scrollPane);
        
        // Add OK button
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void handleViewPatientVitals(Patient patient) {
        // Create a popup dialog to display patient vitals
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Vitals: " + patient.getName());
        dialog.setHeaderText("Vital Signs for " + patient.getName());
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        if (patient.getVitalsDatabase() == null || patient.getVitalsDatabase().getVitals().isEmpty()) {
            content.getChildren().add(new Label("No vital signs data available."));
        } else {
            TableView<VitalSign> vitalsTable = new TableView<>();
            
            // Create columns
            TableColumn<VitalSign, Number> heartRateColumn = new TableColumn<>("Heart Rate (bpm)");
            heartRateColumn.setCellValueFactory(cellData -> {
                return new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getHeartRate());
            });
            
            TableColumn<VitalSign, Number> oxygenColumn = new TableColumn<>("Oxygen Level (%)");
            oxygenColumn.setCellValueFactory(cellData -> {
                return new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getOxygenLevel());
            });
            
            TableColumn<VitalSign, Number> bpColumn = new TableColumn<>("Blood Pressure (mmHg)");
            bpColumn.setCellValueFactory(cellData -> {
                return new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getBloodPressure());
            });
            
            TableColumn<VitalSign, Number> tempColumn = new TableColumn<>("Temperature (°C)");
            tempColumn.setCellValueFactory(cellData -> {
                return new javafx.beans.property.SimpleDoubleProperty(
                        cellData.getValue().getTemperature());
            });
            
            vitalsTable.getColumns().addAll(heartRateColumn, oxygenColumn, bpColumn, tempColumn);
            vitalsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            
            // Add data to table
            ArrayList<VitalSign> vitals = patient.getVitalsDatabase().getVitals();
            ObservableList<VitalSign> vitalsData = FXCollections.observableArrayList(vitals);
            vitalsTable.setItems(vitalsData);
            vitalsTable.setPrefHeight(300);
            
            content.getChildren().add(vitalsTable);
            
            // Add analyze button
            Button analyzeButton = new Button("Analyze Health Trends");
            analyzeButton.setOnAction(e -> {
                handleAnalyzePatientHealth(patient);
            });
            
            content.getChildren().add(analyzeButton);
        }
        
        // Add OK button
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void handleAnalyzePatientHealth(Patient patient) {
        // Create a popup dialog to display health analytics
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Health Analytics: " + patient.getName());
        dialog.setHeaderText("Health Trends Analysis for " + patient.getName());
        
        // Create content
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        
        // Run analytics
        HealthAnalytics analytics = new HealthAnalytics();
        AnalyticsResult result = analytics.analyzePatientData(patient);
        
        if (result.getHeartRateAvg() > 0) {
            Label vitalsLabel = new Label("Vital Signs Analysis:");
            vitalsLabel.setStyle("-fx-font-weight: bold;");
            
            Label heartRateLabel = new Label(String.format("Heart Rate: %.1f bpm (Trend: %+.1f)", 
                    result.getHeartRateAvg(), result.getHeartRateTrend()));
            
            Label oxygenLabel = new Label(String.format("Oxygen Level: %.1f%% (Trend: %+.1f)", 
                    result.getOxygenAvg(), result.getOxygenTrend()));
            
            Label bpLabel = new Label(String.format("Blood Pressure: %.1f mmHg (Trend: %+.1f)", 
                    result.getBpAvg(), result.getBpTrend()));
            
            Label tempLabel = new Label(String.format("Temperature: %.1f°C (Trend: %+.1f)", 
                    result.getTempAvg(), result.getTempTrend()));
            
            content.getChildren().addAll(vitalsLabel, heartRateLabel, oxygenLabel, bpLabel, tempLabel);
            
            // Add separator
            content.getChildren().add(new Separator());
        }
        
        // Display insights
        if (!result.getInsights().isEmpty()) {
            Label insightsLabel = new Label("Insights & Recommendations:");
            insightsLabel.setStyle("-fx-font-weight: bold;");
            
            VBox insightsBox = new VBox(5);
            for (String insight : result.getInsights()) {
                insightsBox.getChildren().add(new Label("• " + insight));
            }
            
            content.getChildren().addAll(insightsLabel, insightsBox);
        } else {
            content.getChildren().add(new Label("No insights available - need more data points."));
        }
        
        // Add OK button
        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(okButton);
        
        dialog.getDialogPane().setContent(content);
        dialog.showAndWait();
    }
    
    private void handleProvideFeedback(Patient patient) {
        // Create a dialog for providing feedback
        Dialog<Feedback> dialog = new Dialog<>();
        dialog.setTitle("Provide Feedback");
        dialog.setHeaderText("Provide feedback and prescription for " + patient.getName());
        
        // Set the button types
        ButtonType submitButtonType = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(submitButtonType, ButtonType.CANCEL);
        
        // Create the form fields
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextArea commentsArea = new TextArea();
        commentsArea.setPromptText("Enter your feedback comments");
        
        TextField medicationField = new TextField();
        medicationField.setPromptText("Medication name (optional)");
        
        TextField dosageField = new TextField();
        dosageField.setPromptText("Dosage instructions (optional)");
        
        TextField scheduleField = new TextField();
        scheduleField.setPromptText("Schedule instructions (optional)");
        
        grid.add(new Label("Comments:"), 0, 0);
        grid.add(commentsArea, 1, 0);
        grid.add(new Label("Medication (optional):"), 0, 1);
        grid.add(medicationField, 1, 1);
        grid.add(new Label("Dosage (optional):"), 0, 2);
        grid.add(dosageField, 1, 2);
        grid.add(new Label("Schedule (optional):"), 0, 3);
        grid.add(scheduleField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        // Request focus on the comments field by default
        commentsArea.requestFocus();
        
        // Convert the result to a Feedback object when the submit button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == submitButtonType) {
                if (commentsArea.getText().trim().isEmpty()) {
                    return null;
                }
                
                // Create prescription if medication is provided
                Prescription prescription = null;
                if (!medicationField.getText().trim().isEmpty()) {
                    prescription = new Prescription(
                        medicationField.getText().trim(),
                        dosageField.getText().trim(),
                        scheduleField.getText().trim()
                    );
                }
                
                // Create and return the feedback
                Feedback feedback = new Feedback(
                    currentDoctor,
                    patient,
                    commentsArea.getText().trim(),
                    prescription
                );
                
                return feedback;
            }
            return null;
        });
        
        Optional<Feedback> result = dialog.showAndWait();
        result.ifPresent(feedback -> {
            // Initialize medical history if needed
            if (patient.getMedicalHistory() == null) {
                MedicalHistory medicalHistory = new MedicalHistory();
                patient.initializeMedicalHistory(medicalHistory);
            }
            
            // Add feedback to the patient's records
            patient.getMedicalHistory().addFeedback(feedback);
            
            showAlert(Alert.AlertType.INFORMATION, "Feedback Submitted", 
                    "Your feedback has been submitted successfully.");
        });
    }
    
    @FXML
    private void handlePrescriptions(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox prescriptionsView = new VBox(15);
        prescriptionsView.setPadding(new Insets(20));
        prescriptionsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Manage Prescriptions");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all patients
        ArrayList<Patient> patients = SessionManager.getPatients();
        
        Label selectPatientLabel = new Label("Select a Patient:");
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        
        // Create custom cell factory to display patient names
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
        
        // Set the button cell to also display the patient name
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
        
        // Add patients to combo box
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(patients);
        patientComboBox.setItems(patientsData);
        
        if (!patientsData.isEmpty()) {
            patientComboBox.setValue(patientsData.get(0));
        }
        
        TableView<Prescription> prescriptionTable = new TableView<>();
        
        // Create columns
        TableColumn<Prescription, String> medicationColumn = new TableColumn<>("Medication");
        medicationColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getMedicationName());
        });
        
        TableColumn<Prescription, String> dosageColumn = new TableColumn<>("Dosage");
        dosageColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getDosage());
        });
        
        TableColumn<Prescription, String> scheduleColumn = new TableColumn<>("Schedule");
        scheduleColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getSchedule());
        });
        
        prescriptionTable.getColumns().addAll(medicationColumn, dosageColumn, scheduleColumn);
        prescriptionTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        prescriptionTable.setPrefHeight(200);
        
        prescriptionTable.setPlaceholder(new Label("No prescriptions found"));
        
        // Update prescription table when patient is selected
        patientComboBox.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                updatePrescriptionTable(prescriptionTable, selectedPatient);
            }
        });
        
        // Initially populate table with first patient
        if (!patientsData.isEmpty()) {
            updatePrescriptionTable(prescriptionTable, patientsData.get(0));
        }
        
        // Button to add new prescription
        Button addPrescriptionButton = new Button("Add New Prescription");
        addPrescriptionButton.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                handleProvideFeedback(selectedPatient);
                // Refresh the prescription table
                updatePrescriptionTable(prescriptionTable, selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Patient Selected", 
                        "Please select a patient to add a prescription.");
            }
        });
        
        prescriptionsView.getChildren().addAll(
                headerLabel, 
                selectPatientLabel, 
                patientComboBox, 
                prescriptionTable, 
                addPrescriptionButton);
        
        contentArea.getChildren().add(prescriptionsView);
    }
    
    private void updatePrescriptionTable(TableView<Prescription> table, Patient patient) {
        ArrayList<Prescription> prescriptions = new ArrayList<>();
        
        // Get prescriptions from medical history
        if (patient.getMedicalHistory() != null) {
            for (Feedback feedback : patient.getMedicalHistory().getPastConsultations()) {
                if (feedback.getPrescription() != null) {
                    prescriptions.add(feedback.getPrescription());
                }
            }
        }
        
        table.setItems(FXCollections.observableArrayList(prescriptions));
    }
    
    @FXML
    private void handleChatWithPatients(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox chatView = new VBox(15);
        chatView.setPadding(new Insets(20));
        chatView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Chat with Patients");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all patients
        ArrayList<Patient> patients = SessionManager.getPatients();
        
        Label selectPatientLabel = new Label("Select a Patient to Chat With:");
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        
        // Create custom cell factory to display patient names
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
        
        // Set the button cell to also display the patient name
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
        
        // Add patients to combo box
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(patients);
        patientComboBox.setItems(patientsData);
        
        if (!patientsData.isEmpty()) {
            patientComboBox.setValue(patientsData.get(0));
        }
        
        // Chat history display
        TextArea chatHistoryArea = new TextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setPrefHeight(300);
        chatHistoryArea.setStyle("-fx-font-family: monospace;");
        
        // Message input and send button
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        
        Button sendButton = new Button("Send");
        sendButton.setDefaultButton(true);
        
        HBox messageBox = new HBox(10);
        messageBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        messageBox.getChildren().addAll(messageField, sendButton);
        
        // Update chat history when patient is selected
        patientComboBox.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                updateChatHistory(chatHistoryArea, selectedPatient);
            }
        });
        
        // Initialize chat history with first patient
        if (!patientsData.isEmpty()) {
            updateChatHistory(chatHistoryArea, patientsData.get(0));
        }
        
        // Send message action
        sendButton.setOnAction(e -> {
            String message = messageField.getText().trim();
            Patient selectedPatient = patientComboBox.getValue();
            
            if (!message.isEmpty() && selectedPatient != null) {
                // Get chat client or create if not exists
                ChatClient chatClient = new ChatClient(currentDoctor, SessionManager.getChatServer());
                
                // Send message
                chatClient.sendMessage(selectedPatient.getName(), message);
                
                // Update chat history
                updateChatHistory(chatHistoryArea, selectedPatient);
                
                // Clear message field
                messageField.clear();
            }
        });
        
        chatView.getChildren().addAll(
                headerLabel, 
                selectPatientLabel, 
                patientComboBox, 
                new Label("Chat History:"), 
                chatHistoryArea, 
                messageBox);
        
        contentArea.getChildren().add(chatView);
    }
    
    private void updateChatHistory(TextArea chatArea, Patient patient) {
        ChatClient chatClient = new ChatClient(currentDoctor, SessionManager.getChatServer());
        
        StringBuilder chatHistory = new StringBuilder();
        for (String message : SessionManager.getChatServer().getChatHistory(currentDoctor.getName(), patient.getName())) {
            chatHistory.append(message).append("\n");
        }
        
        if (chatHistory.length() == 0) {
            chatHistory.append("No messages yet. Start the conversation!");
        }
        
        chatArea.setText(chatHistory.toString());
        chatArea.positionCaret(chatArea.getText().length());
    }
    
    @FXML
    private void handleHealthAnalytics(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox analyticsView = new VBox(15);
        analyticsView.setPadding(new Insets(20));
        analyticsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Patient Health Analytics");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all patients
        ArrayList<Patient> patients = SessionManager.getPatients();
        
        Label selectPatientLabel = new Label("Select Patient for Analysis:");
        selectPatientLabel.setStyle("-fx-font-weight: bold;");
        
        ComboBox<Patient> patientComboBox = new ComboBox<>();
        
        // Create custom cell factory to display patient names
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
        
        // Set the button cell to also display the patient name
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
        
        // Add patients to combo box
        ObservableList<Patient> patientsData = FXCollections.observableArrayList(patients);
        patientComboBox.setItems(patientsData);
        
        // Set initial selection if patients exist
        if (!patientsData.isEmpty()) {
            patientComboBox.setValue(patientsData.get(0));
        }
        
        // Create content area for analytics
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(500);
        
        VBox analyticsContent = new VBox(20);
        analyticsContent.setPadding(new Insets(10));
        
        // Show initial analytics if a patient is selected
        if (!patientsData.isEmpty()) {
            Patient initialPatient = patientsData.get(0);
            HealthAnalytics analytics = new HealthAnalytics();
            AnalyticsResult result = analytics.analyzePatientData(initialPatient);
            
            VBox analyticsResultView = ChartGenerator.visualizeAnalyticsResult(result);
            
            // Add charts
            Label chartsLabel = new Label("Vital Signs Charts");
            chartsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
            
            StackPane chartArea = new StackPane();
            chartArea.setPrefHeight(400);
            
            if (initialPatient.getVitalsDatabase() != null && 
                !initialPatient.getVitalsDatabase().getVitals().isEmpty()) {
                
                ScrollPane chartsScroll = new ScrollPane();
                chartsScroll.setFitToWidth(true);
                chartsScroll.setContent(ChartGenerator.generateMultipleVitalCharts(initialPatient));
                chartArea.getChildren().add(chartsScroll);
            } else {
                Label noDataLabel = new Label("No vital signs data available for this patient.");
                chartArea.getChildren().add(noDataLabel);
            }
            
            analyticsContent.getChildren().addAll(
                analyticsResultView,
                new Separator(),
                chartsLabel,
                chartArea
            );
        } else {
            Label noDataLabel = new Label("No patients available for analysis.");
            analyticsContent.getChildren().add(noDataLabel);
        }
        
        scrollPane.setContent(analyticsContent);
        
        // Update analytics when patient selection changes
        patientComboBox.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            
            if (selectedPatient != null) {
                analyticsContent.getChildren().clear();
                
                HealthAnalytics analytics = new HealthAnalytics();
                AnalyticsResult result = analytics.analyzePatientData(selectedPatient);
                
                VBox analyticsResultView = ChartGenerator.visualizeAnalyticsResult(result);
                
                // Add charts
                Label chartsLabel = new Label("Vital Signs Charts");
                chartsLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
                
                StackPane chartArea = new StackPane();
                chartArea.setPrefHeight(400);
                
                if (selectedPatient.getVitalsDatabase() != null && 
                    !selectedPatient.getVitalsDatabase().getVitals().isEmpty()) {
                    
                    ScrollPane chartsScroll = new ScrollPane();
                    chartsScroll.setFitToWidth(true);
                    chartsScroll.setContent(ChartGenerator.generateMultipleVitalCharts(selectedPatient));
                    chartArea.getChildren().add(chartsScroll);
                } else {
                    Label noDataLabel = new Label("No vital signs data available for this patient.");
                    chartArea.getChildren().add(noDataLabel);
                }
                
                analyticsContent.getChildren().addAll(
                    analyticsResultView,
                    new Separator(),
                    chartsLabel,
                    chartArea
                );
            }
        });
        
        // Add buttons
        Button generateReportButton = new Button("Generate Patient Report");
        generateReportButton.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                handleGeneratePatientReport(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select a patient to generate a report.");
            }
        });
        
        Button emailPatientButton = new Button("Email Results to Patient");
        emailPatientButton.setOnAction(e -> {
            Patient selectedPatient = patientComboBox.getValue();
            if (selectedPatient != null) {
                handleEmailResultsToPatient(selectedPatient);
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select a patient to email results.");
            }
        });
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(generateReportButton, emailPatientButton);
        
        analyticsView.getChildren().addAll(
            headerLabel,
            new HBox(10, selectPatientLabel, patientComboBox),
            scrollPane,
            buttonBox
        );
        
        contentArea.getChildren().add(analyticsView);
    }
    
    private void handleGeneratePatientReport(Patient patient) {
        ReportsGenerator generator = new ReportsGenerator();
        
        try {
            String reportPath = generator.generatePatientReport(patient, true);
            if (reportPath != null) {
                showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                        "Patient health report has been generated successfully at:\n" + reportPath);
            } else {
                showAlert(Alert.AlertType.ERROR, "Report Generation Failed", 
                        "Failed to generate patient health report.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error generating report: " + e.getMessage());
        }
    }
    
    private void handleEmailResultsToPatient(Patient patient) {
        try {
            // Get analytics results
            HealthAnalytics analytics = new HealthAnalytics();
            AnalyticsResult result = analytics.analyzePatientData(patient);
            
            // Create email content
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear ").append(patient.getName()).append(",\n\n");
            emailContent.append("Here is a summary of your recent health analytics:\n\n");
            
            emailContent.append("VITAL SIGNS SUMMARY:\n");
            emailContent.append(String.format("- Heart Rate: %.1f bpm (Trend: %+.1f)\n", 
                    result.getHeartRateAvg(), result.getHeartRateTrend()));
            emailContent.append(String.format("- Oxygen Level: %.1f%% (Trend: %+.1f)\n", 
                    result.getOxygenAvg(), result.getOxygenTrend()));
            emailContent.append(String.format("- Blood Pressure: %.1f mmHg (Trend: %+.1f)\n", 
                    result.getBpAvg(), result.getBpTrend()));
            emailContent.append(String.format("- Temperature: %.1f°C (Trend: %+.1f)\n\n", 
                    result.getTempAvg(), result.getTempTrend()));
            
            emailContent.append("INSIGHTS & RECOMMENDATIONS:\n");
            if (result.getInsights() != null && !result.getInsights().isEmpty()) {
                for (String insight : result.getInsights()) {
                    emailContent.append("- ").append(insight).append("\n");
                }
            } else {
                emailContent.append("No specific insights available with current data.\n");
            }
            emailContent.append("\n");
            
            emailContent.append("CURRENT MEDICATIONS:\n");
            if (result.getMedications() != null && !result.getMedications().isEmpty()) {
                for (var entry : result.getMedications().entrySet()) {
                    emailContent.append("- ").append(entry.getKey())
                               .append(" (").append(entry.getValue()).append(")\n");
                }
            } else {
                emailContent.append("No medications currently prescribed.\n");
            }
            emailContent.append("\n");
            
            emailContent.append("Please contact me if you have any questions about these results.\n\n");
            emailContent.append("Regards,\n");
            emailContent.append("Dr. ").append(currentDoctor.getName());
            
            // Send email
            com.rhms.notifications.EmailNotification emailNotification = new com.rhms.notifications.EmailNotification();
            emailNotification.sendNotification(
                patient.getEmail(),
                "Your Health Analytics Report",
                emailContent.toString()
            );
            
            showAlert(Alert.AlertType.INFORMATION, "Email Sent", 
                    "Health analytics report has been emailed to " + patient.getName() + ".");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Error sending email: " + e.getMessage());
        }
    }
    
    private void handleStartVideoConsultation(Patient patient) {
        String meetingId = VideoCall.generateMeetingId();
        try {
            VideoCall.startVideoCall(meetingId);
            
            showAlert(Alert.AlertType.INFORMATION, "Video Call Started", 
                    "Meeting ID: " + meetingId + "\n\nShare this ID with the patient.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                    "Failed to start video call: " + e.getMessage());
        }
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
            Stage stage = (Stage) userNameLabel.getScene().getWindow();
            
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
}