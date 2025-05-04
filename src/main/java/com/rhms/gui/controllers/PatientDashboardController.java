package com.rhms.gui.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import com.rhms.analytics.AnalyticsResult;
import com.rhms.analytics.HealthAnalytics;
import com.rhms.analytics.HealthDataVisualizer;
import com.rhms.appointmentScheduling.Appointment;
import com.rhms.doctorPatientInteraction.ChatClient;
import com.rhms.doctorPatientInteraction.Feedback;
import com.rhms.doctorPatientInteraction.MedicalHistory;
import com.rhms.doctorPatientInteraction.VideoCall;
import com.rhms.emergencyAlert.PanicButton;
import com.rhms.gui.SessionManager;
import com.rhms.healthDataHandling.CSVVitalsProcessor;
import com.rhms.healthDataHandling.VitalSign;
import com.rhms.healthDataHandling.VitalsDatabase;
import com.rhms.notifications.EmailNotification;
import com.rhms.reporting.ReportsGenerator;
import com.rhms.userManagement.Doctor;
import com.rhms.userManagement.Patient;
import com.rhms.persistence.DataPersistenceManager;

public class PatientDashboardController {
    @FXML private Label userNameLabel;
    @FXML private StackPane contentArea;
    @FXML private VBox navigationVBox;
    
    private Patient currentPatient;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    
    @FXML
    public void initialize() {
        // Get current patient from session
        currentPatient = (Patient) SessionManager.getCurrentUser();
        
        if (currentPatient != null) {
            userNameLabel.setText("Welcome, " + currentPatient.getName());
            
            // Initialize vitals database if needed
            if (currentPatient.getVitalsDatabase() == null) {
                System.out.println("Creating new vitals database for patient");
                currentPatient.setVitalsDatabase(new VitalsDatabase(currentPatient));
            } else {
                VitalsDatabase db = currentPatient.getVitalsDatabase();
                int vitalCount = db.getVitals().size();
                System.out.println("Using existing vitals database with " + vitalCount + " records");
                
                // Print some sample vitals if available
                if (vitalCount > 0) {
                    System.out.println("Sample vital record: " + 
                        db.getVitals().get(0).getHeartRate() + "bpm, " +
                        db.getVitals().get(0).getOxygenLevel() + "%, " +
                        db.getVitals().get(0).getBloodPressure() + "mmHg, " +
                        db.getVitals().get(0).getTemperature() + "°C");
                }
            }
            
            // Initialize medical history if needed
            if (currentPatient.getMedicalHistory() == null) {
                currentPatient.setMedicalHistory(new MedicalHistory());
            }
            
            // Initialize panic button if needed
            if (currentPatient.getPanicButton() == null) {
                currentPatient.setPanicButton(new PanicButton(currentPatient));
            }
        }
    }
    
    @FXML
    private void handleViewAppointments(ActionEvent event) {
        contentArea.getChildren().clear();
        
        // Create UI for viewing appointments
        VBox appointmentsView = new VBox(15);
        appointmentsView.setPadding(new Insets(20));
        appointmentsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("My Appointments");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get appointments for the current patient
        ArrayList<Appointment> myAppointments = SessionManager.getAppointmentManager()
                .getPatientAppointments(currentPatient);
        
        TableView<Appointment> appointmentTable = new TableView<>();
        
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
        
        TableColumn<Appointment, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(cellData -> {
            return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getStatus());
        });
        statusColumn.setPrefWidth(100);
        
        // Add columns individually to avoid type safety warning
        appointmentTable.getColumns().add(dateColumn);
        appointmentTable.getColumns().add(doctorColumn);
        appointmentTable.getColumns().add(statusColumn);
        appointmentTable.setPrefHeight(300);
        
        // Add data to table
        ObservableList<Appointment> appointmentsData = FXCollections.observableArrayList(myAppointments);
        appointmentTable.setItems(appointmentsData);
        
        // Add placeholder text if no appointments
        appointmentTable.setPlaceholder(new Label("No appointments scheduled"));
        
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
                            "Your appointment has been cancelled successfully.");
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "No Selection", 
                        "Please select an appointment to cancel.");
            }
        });
        
        appointmentsView.getChildren().addAll(headerLabel, appointmentTable, cancelButton);
        contentArea.getChildren().add(appointmentsView);
    }
    
    @FXML
    private void handleBookAppointment(ActionEvent event) {
        contentArea.getChildren().clear();
        
        // Create UI for booking appointments
        VBox bookingView = new VBox(15);
        bookingView.setPadding(new Insets(20));
        bookingView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Book a New Appointment");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get all available doctors
        ArrayList<Doctor> availableDoctors = SessionManager.getDoctors();
        
        // Create form fields
        Label doctorLabel = new Label("Select Doctor:");
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        
        // Create custom cell factory to display doctor names
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
        
        // Set the button cell to also display the doctor name
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
        
        // Add doctors to combo box
        ObservableList<Doctor> doctorsData = FXCollections.observableArrayList(availableDoctors);
        doctorComboBox.setItems(doctorsData);
        
        if (!doctorsData.isEmpty()) {
            doctorComboBox.setValue(doctorsData.get(0));
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
        
        // Reason for appointment
        Label reasonLabel = new Label("Reason for Appointment:");
        TextArea reasonTextArea = new TextArea();
        reasonTextArea.setPrefRowCount(3);
        
        // Book appointment button
        Button bookButton = new Button("Book Appointment");
        bookButton.setOnAction(e -> {
            if (doctorComboBox.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", 
                        "Please select a doctor.");
                return;
            }
            
            if (datePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", 
                        "Please select a date.");
                return;
            }
            
            // Convert LocalDate to Date
            Date appointmentDate = java.sql.Date.valueOf(datePicker.getValue());
            
            // Request the appointment
            Doctor selectedDoctor = doctorComboBox.getValue();
            
            Appointment newAppointment = SessionManager.getAppointmentManager()
                    .requestAppointment(appointmentDate, selectedDoctor, currentPatient);
            
            // Set reason as notes
            if (!reasonTextArea.getText().isEmpty()) {
                newAppointment.setNotes(reasonTextArea.getText());
            }
            
            showAlert(Alert.AlertType.INFORMATION, "Appointment Requested", 
                    "Your appointment has been requested and is pending approval by the doctor.");
            
            // Clear fields
            datePicker.setValue(java.time.LocalDate.now());
            reasonTextArea.clear();
        });
        
        // Create form layout with proper spacing
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setPadding(new Insets(10));
        
        formGrid.add(doctorLabel, 0, 0);
        formGrid.add(doctorComboBox, 1, 0);
        formGrid.add(dateLabel, 0, 1);
        formGrid.add(datePicker, 1, 1);
        formGrid.add(timeLabel, 0, 2);
        formGrid.add(timeComboBox, 1, 2);
        formGrid.add(reasonLabel, 0, 3);
        formGrid.add(reasonTextArea, 1, 3);
        
        bookingView.getChildren().addAll(headerLabel, formGrid, bookButton);
        contentArea.getChildren().add(bookingView);
    }
    
    @FXML
    private void handleViewHealthData(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox vitalsView = new VBox(15);
        vitalsView.setPadding(new Insets(20));
        vitalsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("My Health Data");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get vitals data
        final VitalsDatabase vitalsDB;
        if (currentPatient.getVitalsDatabase() == null) {
            vitalsDB = new VitalsDatabase(currentPatient);
            currentPatient.setVitalsDatabase(vitalsDB);
        } else {
            vitalsDB = currentPatient.getVitalsDatabase();
        }
        
        ArrayList<VitalSign> vitals = vitalsDB.getVitals();
        System.out.println("Vitals size in handleViewHealthData: " + vitals.size());
        
        // Create table for vitals
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
        
        // Add columns individually to avoid the type safety warning
        vitalsTable.getColumns().add(heartRateColumn);
        vitalsTable.getColumns().add(oxygenColumn);
        vitalsTable.getColumns().add(bpColumn);
        vitalsTable.getColumns().add(tempColumn);
        
        // Set equal width for columns using non-deprecated method
        vitalsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        vitalsTable.setMinWidth(Region.USE_COMPUTED_SIZE);
        Platform.runLater(() -> {
            double tableWidth = vitalsTable.getWidth();
            double columnWidth = tableWidth / vitalsTable.getColumns().size();
            for (TableColumn<VitalSign, ?> column : vitalsTable.getColumns()) {
                column.setPrefWidth(columnWidth);
            }
        });
        
        // Add data to table
        ObservableList<VitalSign> vitalsData = FXCollections.observableArrayList(vitals);
        vitalsTable.setItems(vitalsData);
        vitalsTable.setPrefHeight(300);
        
        // Add placeholder text if no vitals
        vitalsTable.setPlaceholder(new Label("No health data records available"));
        
        // Add buttons for managing vitals
        Button addVitalButton = new Button("Add New Vital Signs");
        addVitalButton.setOnAction(e -> {
            Dialog<VitalSign> dialog = new Dialog<>();
            dialog.setTitle("Add Vital Signs");
            dialog.setHeaderText("Enter new vital signs measurement");
            
            // Set the button types
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            
            // Create input fields
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            TextField heartRateField = new TextField();
            heartRateField.setPromptText("e.g., 75");
            TextField oxygenField = new TextField();
            oxygenField.setPromptText("e.g., 98");
            TextField bpField = new TextField();
            bpField.setPromptText("e.g., 120");
            TextField tempField = new TextField();
            tempField.setPromptText("e.g., 37.0");
            
            grid.add(new Label("Heart Rate (bpm):"), 0, 0);
            grid.add(heartRateField, 1, 0);
            grid.add(new Label("Oxygen Level (%):"), 0, 1);
            grid.add(oxygenField, 1, 1);
            grid.add(new Label("Blood Pressure (mmHg):"), 0, 2);
            grid.add(bpField, 1, 2);
            grid.add(new Label("Temperature (°C):"), 0, 3);
            grid.add(tempField, 1, 3);
            
            dialog.getDialogPane().setContent(grid);
            
            // Request focus on heart rate field
            heartRateField.requestFocus();
            
            // Convert the result to vital sign when the add button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    try {
                        double heartRate = Double.parseDouble(heartRateField.getText());
                        double oxygen = Double.parseDouble(oxygenField.getText());
                        double bp = Double.parseDouble(bpField.getText());
                        double temp = Double.parseDouble(tempField.getText());
                        
                        return new VitalSign(heartRate, oxygen, bp, temp);
                    } catch (NumberFormatException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Input", 
                                "Please enter valid numbers for all fields.");
                        return null;
                    } catch (IllegalArgumentException ex) {
                        showAlert(Alert.AlertType.ERROR, "Invalid Values", ex.getMessage());
                        return null;
                    }
                }
                return null;
            });
            
            Optional<VitalSign> result = dialog.showAndWait();
            
            result.ifPresent(vitalSign -> {
                vitalsDB.addVitalRecord(vitalSign);
                vitalsData.add(vitalSign);
                showAlert(Alert.AlertType.INFORMATION, "Success", 
                        "Vital signs recorded successfully.");
            });
        });
        
        Button uploadCSVButton = new Button("Upload Vitals from CSV");
        uploadCSVButton.setOnAction(e -> handleUploadVitalsFromCSV());
        
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(addVitalButton, uploadCSVButton);
        
        vitalsView.getChildren().addAll(headerLabel, vitalsTable, buttonBox);
        contentArea.getChildren().add(vitalsView);
    }
    
    private void handleUploadVitalsFromCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select CSV File");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        
        File selectedFile = fileChooser.showOpenDialog(contentArea.getScene().getWindow());
        
        if (selectedFile != null) {
            try {
                System.out.println("Processing CSV file: " + selectedFile.getAbsolutePath());
                
                // Get vitals database
                final VitalsDatabase vitalsDB;
                if (currentPatient.getVitalsDatabase() == null) {
                    System.out.println("Creating new vitals database for patient");
                    vitalsDB = new VitalsDatabase(currentPatient);
                    currentPatient.setVitalsDatabase(vitalsDB);
                } else {
                    vitalsDB = currentPatient.getVitalsDatabase();
                }
                
                // Process CSV file using a try-with-resources approach for safe file handling
                System.out.println("Starting CSV processing...");
                CSVVitalsProcessor processor = new CSVVitalsProcessor();
                ArrayList<VitalSign> importedVitals = processor.processCSVFile(selectedFile.getAbsolutePath());
                
                if (importedVitals != null && !importedVitals.isEmpty()) {
                    System.out.println("Successfully imported " + importedVitals.size() + " vitals");
                    
                    // Add each vital sign to the database
                    for (VitalSign vital : importedVitals) {
                        vitalsDB.addVitalRecord(vital);
                    }
                    
                    // Update patient's vitals database
                    currentPatient.setVitalsDatabase(vitalsDB);
                    
                    // Find the patient in the patients list and update it
                    ArrayList<Patient> patients = SessionManager.getPatients();
                    for (int i = 0; i < patients.size(); i++) {
                        if (patients.get(i).getUserID() == currentPatient.getUserID()) {
                            System.out.println("Updating patient in session list with ID: " + currentPatient.getUserID());
                            
                            // Critical: ensure the patient's vitals database is properly set
                            VitalsDatabase db = currentPatient.getVitalsDatabase();
                            if (db != null) {
                                db.setPatient(currentPatient);
                                System.out.println("Verified vitals database has proper patient reference");
                            }
                            
                            patients.set(i, currentPatient);
                            break;
                        }
                    }
                    
                    // Call SessionManager's saveData method for reliable saving
                    try {
                        System.out.println("Attempting to save data after vitals update...");
                        SessionManager.saveData();
                        System.out.println("Data saved successfully through SessionManager");
                    } catch (Exception ex) {
                        System.err.println("Error during save: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                    
                    // Force refresh UI on JavaFX thread
                    Platform.runLater(() -> {
                        // Clear content area first
                        contentArea.getChildren().clear();
                        
                        // Then rebuild the health data view with refreshed data
                        VBox vitalsView = new VBox(15);
                        vitalsView.setPadding(new Insets(20));
                        vitalsView.setAlignment(Pos.TOP_CENTER);
                        
                        Label headerLabel = new Label("My Health Data");
                        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
                        
                        // Get fresh vitals data
                        ArrayList<VitalSign> refreshedVitals = vitalsDB.getVitals();
                        System.out.println("Display refreshed vitals: " + refreshedVitals.size());
                        
                        // Create table and populate it
                        TableView<VitalSign> vitalsTable = createVitalsTable(refreshedVitals);
                        
                        Button addVitalButton = new Button("Add New Vital Signs");
                        addVitalButton.setOnAction(e -> {
                            // Your existing code for adding vitals
                            Dialog<VitalSign> dialog = new Dialog<>();
                            dialog.setTitle("Add Vital Signs");
                            // ... existing code ...
                        });
                        
                        Button uploadCSVButton = new Button("Upload Vitals from CSV");
                        uploadCSVButton.setOnAction(evt -> handleUploadVitalsFromCSV());
                        
                        HBox buttonBox = new HBox(10);
                        buttonBox.setAlignment(Pos.CENTER);
                        buttonBox.getChildren().addAll(addVitalButton, uploadCSVButton);
                        
                        vitalsView.getChildren().addAll(headerLabel, vitalsTable, buttonBox);
                        contentArea.getChildren().add(vitalsView);
                    });
                    
                    showAlert(Alert.AlertType.INFORMATION, "Upload Successful", 
                             "Successfully imported " + importedVitals.size() + " vital records.");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Empty Data", 
                             "No valid vital signs data found in the selected CSV file.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Upload Failed", 
                         "Failed to process CSV file: " + e.getMessage());
            }
        }
    }
    
    // Helper method to create a vitals table
    private TableView<VitalSign> createVitalsTable(ArrayList<VitalSign> vitals) {
        TableView<VitalSign> vitalsTable = new TableView<>();
        
        // Create columns
        TableColumn<VitalSign, Number> heartRateColumn = new TableColumn<>("Heart Rate (bpm)");
        heartRateColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getHeartRate()));
        
        TableColumn<VitalSign, Number> oxygenColumn = new TableColumn<>("Oxygen Level (%)");
        oxygenColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getOxygenLevel()));
        
        TableColumn<VitalSign, Number> bpColumn = new TableColumn<>("Blood Pressure (mmHg)");
        bpColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getBloodPressure()));
        
        TableColumn<VitalSign, Number> tempColumn = new TableColumn<>("Temperature (°C)");
        tempColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getTemperature()));
        
        // Add columns
        vitalsTable.getColumns().add(heartRateColumn);
        vitalsTable.getColumns().add(oxygenColumn);
        vitalsTable.getColumns().add(bpColumn);
        vitalsTable.getColumns().add(tempColumn);
        
        // Set data
        ObservableList<VitalSign> vitalsData = FXCollections.observableArrayList(vitals);
        vitalsTable.setItems(vitalsData);
        vitalsTable.setPrefHeight(300);
        
        // Add placeholder
        vitalsTable.setPlaceholder(new Label("No health data records available"));
        
        return vitalsTable;
    }
    
    @FXML
    private void handleViewMedicalHistory(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox historyView = new VBox(15);
        historyView.setPadding(new Insets(20));
        historyView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Medical History");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get medical history
        MedicalHistory medHistory = currentPatient.getMedicalHistory();
        if (medHistory == null) {
            medHistory = new MedicalHistory();
            currentPatient.setMedicalHistory(medHistory);
        }
        
        ArrayList<Feedback> consultations = medHistory.getPastConsultations();
        
        // Create list view for feedback and prescriptions
        ListView<Feedback> historyListView = new ListView<>();
        historyListView.setPrefHeight(400);
        
        // Create cell factory for custom display
        historyListView.setCellFactory(param -> new ListCell<Feedback>() {
            @Override
            protected void updateItem(Feedback item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    VBox cell = new VBox(5);
                    cell.setPadding(new Insets(10));
                    
                    Label doctorLabel = new Label("Doctor: " + item.getDoctor().getName());
                    doctorLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label commentsLabel = new Label("Comments: " + item.getComments());
                    commentsLabel.setWrapText(true);
                    
                    cell.getChildren().addAll(doctorLabel, commentsLabel);
                    
                    if (item.getPrescription() != null) {
                        Label prescLabel = new Label("Prescription: " + 
                                item.getPrescription().getMedicationName() + " - " + 
                                item.getPrescription().getDosage() + " - " + 
                                item.getPrescription().getSchedule());
                        prescLabel.setStyle("-fx-font-style: italic;");
                        prescLabel.setWrapText(true);
                        cell.getChildren().add(prescLabel);
                    }
                    
                    setGraphic(cell);
                }
            }
        });
        
        // Add data to list view
        ObservableList<Feedback> historyData = FXCollections.observableArrayList(consultations);
        historyListView.setItems(historyData);
        
        // Add placeholder text if no history
        if (historyData.isEmpty()) {
            Label placeholderLabel = new Label("No medical history records available");
            placeholderLabel.setStyle("-fx-font-style: italic;");
            historyView.getChildren().addAll(headerLabel, placeholderLabel);
        } else {
            historyView.getChildren().addAll(headerLabel, historyListView);
        }
        
        contentArea.getChildren().add(historyView);
    }
    
    @FXML
    private void handleChatWithDoctor(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox chatView = new VBox(15);
        chatView.setPadding(new Insets(20));
        chatView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Chat with Doctor");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get list of doctors
        ArrayList<Doctor> doctors = SessionManager.getDoctors();
        
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        
        // Custom cell factory
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
        
        // Set the button cell
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
        
        ObservableList<Doctor> doctorsData = FXCollections.observableArrayList(doctors);
        doctorComboBox.setItems(doctorsData);
        
        if (!doctorsData.isEmpty()) {
            doctorComboBox.setValue(doctorsData.get(0));
        }
        
        // Chat display area
        TextArea chatHistoryArea = new TextArea();
        chatHistoryArea.setEditable(false);
        chatHistoryArea.setPrefHeight(300);
        chatHistoryArea.setWrapText(true);
        
        // Message input area
        TextField messageField = new TextField();
        messageField.setPromptText("Type your message here...");
        
        Button sendButton = new Button("Send");
        sendButton.setOnAction(e -> {
            Doctor selectedDoctor = doctorComboBox.getValue();
            String message = messageField.getText().trim();
            
            if (selectedDoctor != null && !message.isEmpty()) {
                // Send the message
                ChatClient chatClient = new ChatClient(currentPatient, SessionManager.getChatServer());
                chatClient.sendMessage(selectedDoctor.getName(), message);
                
                // Refresh chat history
                String chatHistory = String.join("\n", 
                    SessionManager.getChatServer().getChatHistory(
                        currentPatient.getName(), selectedDoctor.getName()));
                
                chatHistoryArea.setText(chatHistory);
                
                // Clear message field
                messageField.clear();
            }
        });
        
        // View history button
        Button viewHistoryButton = new Button("View Chat History");
        viewHistoryButton.setOnAction(e -> {
            Doctor selectedDoctor = doctorComboBox.getValue();
            
            if (selectedDoctor != null) {
                String chatHistory = String.join("\n", 
                    SessionManager.getChatServer().getChatHistory(
                        currentPatient.getName(), selectedDoctor.getName()));
                
                if (chatHistory.isEmpty()) {
                    chatHistoryArea.setText("No chat history with this doctor yet.");
                } else {
                    chatHistoryArea.setText(chatHistory);
                }
            }
        });
        
        // Layout
        HBox doctorSelectionBox = new HBox(10, new Label("Select Doctor:"), doctorComboBox);
        doctorSelectionBox.setAlignment(Pos.CENTER_LEFT);
        
        HBox messageBox = new HBox(10, messageField, sendButton);
        messageBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(messageField, Priority.ALWAYS);
        
        HBox buttonBox = new HBox(10, viewHistoryButton);
        buttonBox.setAlignment(Pos.CENTER);
        
        chatView.getChildren().addAll(
            headerLabel, 
            doctorSelectionBox, 
            new Label("Chat History:"),
            chatHistoryArea, 
            new Label("Message:"),
            messageBox,
            buttonBox
        );
        
        contentArea.getChildren().add(chatView);
    }
    
    @FXML
    private void handleEmergencyAssistance(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox emergencyView = new VBox(15);
        emergencyView.setPadding(new Insets(20));
        emergencyView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Emergency Assistance");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #cc0000;");
        
        // Get panic button status
        PanicButton panicButton = currentPatient.getPanicButton();
        boolean isPanicEnabled = panicButton != null && panicButton.isActive();
        
        // Panic button section
        VBox panicButtonBox = new VBox(10);
        panicButtonBox.setPadding(new Insets(20));
        panicButtonBox.setStyle("-fx-border-color: #cc0000; -fx-border-radius: 5px; -fx-background-color: #fff8f8;");
        panicButtonBox.setAlignment(Pos.CENTER);
        
        Label panicStatusLabel = new Label("Emergency Alert " + (isPanicEnabled ? "ENABLED" : "DISABLED"));
        panicStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: " + 
                                 (isPanicEnabled ? "#009900" : "#cc0000"));
        
        // Toggle button for enabling/disabling panic
        ToggleButton togglePanicButton = new ToggleButton(isPanicEnabled ? "Disable Alert System" : "Enable Alert System");
        togglePanicButton.setSelected(isPanicEnabled);
        togglePanicButton.setOnAction(e -> {
            if (togglePanicButton.isSelected()) {
                panicButton.enable();
                panicStatusLabel.setText("Emergency Alert ENABLED");
                panicStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #009900");
                togglePanicButton.setText("Disable Alert System");
                showAlert(Alert.AlertType.INFORMATION, "Alert System Enabled", 
                         "Emergency alert system has been enabled.");
            } else {
                panicButton.disable();
                panicStatusLabel.setText("Emergency Alert DISABLED");
                panicStatusLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #cc0000");
                togglePanicButton.setText("Enable Alert System");
                showAlert(Alert.AlertType.INFORMATION, "Alert System Disabled", 
                         "Emergency alert system has been disabled.");
            }
        });
        
        // Big emergency button
        Button triggerEmergencyButton = new Button("TRIGGER EMERGENCY ALERT");
        triggerEmergencyButton.setStyle("-fx-background-color: #cc0000; -fx-text-fill: white; " + 
                                       "-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 15 30;");
        triggerEmergencyButton.setDisable(!isPanicEnabled);
        
        triggerEmergencyButton.setOnAction(e -> {
            // Dialog for emergency reason
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Emergency Alert");
            dialog.setHeaderText("You are about to trigger an emergency alert");
            dialog.setContentText("Please describe the emergency situation:");
            
            Optional<String> result = dialog.showAndWait();
            
            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String reason = result.get();
                
                // Trigger panic button
                panicButton.triggerAlert(reason);
                
                // Show confirmation
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Emergency Alert Sent");
                alert.setHeaderText("Emergency services have been notified");
                alert.setContentText("Stay calm. Help is on the way. Your doctor will be notified immediately.");
                alert.showAndWait();
            }
        });
        
        // Update trigger button when toggle changes
        togglePanicButton.selectedProperty().addListener((obs, oldVal, newVal) -> {
            triggerEmergencyButton.setDisable(!newVal);
        });
        
        panicButtonBox.getChildren().addAll(panicStatusLabel, togglePanicButton, 
                                           new Separator(), triggerEmergencyButton);
        
        // Emergency contacts section
        VBox contactsBox = new VBox(10);
        contactsBox.setPadding(new Insets(15));
        contactsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label contactsLabel = new Label("Emergency Contacts");
        contactsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TableView<EmergencyContact> contactsTable = new TableView<>();
        
        TableColumn<EmergencyContact, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getName()));
        nameColumn.setPrefWidth(150);
        
        TableColumn<EmergencyContact, String> relationColumn = new TableColumn<>("Relation");
        relationColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getRelation()));
        relationColumn.setPrefWidth(100);
        
        TableColumn<EmergencyContact, String> phoneColumn = new TableColumn<>("Phone");
        phoneColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPhone()));
        phoneColumn.setPrefWidth(120);
        
        // Add columns individually to avoid type safety warning
        contactsTable.getColumns().add(nameColumn);
        contactsTable.getColumns().add(relationColumn);
        contactsTable.getColumns().add(phoneColumn);
        contactsTable.setPrefHeight(150);
        
        // Add sample data
        ObservableList<EmergencyContact> contactsData = FXCollections.observableArrayList(
            new EmergencyContact("Local Emergency", "Service", "911"),
            new EmergencyContact("Dr. Abdullah", "Primary Doctor", "987654321")
        );
        contactsTable.setItems(contactsData);
        
        Button addContactButton = new Button("Add Emergency Contact");
        addContactButton.setOnAction(e -> {
            Dialog<EmergencyContact> contactDialog = new Dialog<>();
            contactDialog.setTitle("Add Emergency Contact");
            contactDialog.setHeaderText("Enter emergency contact details");
            
            ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
            contactDialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            TextField nameField = new TextField();
            nameField.setPromptText("Contact Name");
            
            TextField relationField = new TextField();
            relationField.setPromptText("Relation");
            
            TextField phoneField = new TextField();
            phoneField.setPromptText("Phone Number");
            
            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Relation:"), 0, 1);
            grid.add(relationField, 1, 1);
            grid.add(new Label("Phone:"), 0, 2);
            grid.add(phoneField, 1, 2);
            
            contactDialog.getDialogPane().setContent(grid);
            
            contactDialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return new EmergencyContact(
                        nameField.getText(),
                        relationField.getText(),
                        phoneField.getText()
                    );
                }
                return null;
            });
            
            Optional<EmergencyContact> result = contactDialog.showAndWait();
            
            result.ifPresent(contact -> {
                contactsData.add(contact);
                showAlert(Alert.AlertType.INFORMATION, "Contact Added", 
                         "Emergency contact has been added successfully.");
            });
        });
        
        contactsBox.getChildren().addAll(contactsLabel, contactsTable, addContactButton);
        
        // Emergency instructions
        VBox instructionsBox = new VBox(10);
        instructionsBox.setPadding(new Insets(15));
        instructionsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label instructionsLabel = new Label("Emergency Instructions");
        instructionsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        TextArea instructionsArea = new TextArea(
            "1. For immediate assistance, click the TRIGGER EMERGENCY ALERT button above.\n\n" +
            "2. If possible, provide a clear description of your emergency.\n\n" +
            "3. The system will automatically notify:\n" +
            "   - Your primary doctor\n" +
            "   - Your emergency contacts\n" +
            "   - Emergency services if needed\n\n" +
            "4. Stay on the line if possible and wait for assistance."
        );
        instructionsArea.setEditable(false);
        instructionsArea.setWrapText(true);
        instructionsArea.setPrefHeight(150);
        
        instructionsBox.getChildren().addAll(instructionsLabel, instructionsArea);
        
        emergencyView.getChildren().addAll(headerLabel, panicButtonBox, contactsBox, instructionsBox);
        contentArea.getChildren().add(emergencyView);
    }
    
    // Helper class for emergency contacts
    private class EmergencyContact {
        private String name;
        private String relation;
        private String phone;
        
        public EmergencyContact(String name, String relation, String phone) {
            this.name = name;
            this.relation = relation;
            this.phone = phone;
        }
        
        public String getName() {
            return name;
        }
        
        public String getRelation() {
            return relation;
        }
        
        public String getPhone() {
            return phone;
        }
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // Make sure to save data before logging out
            SessionManager.saveData();
            
            // Load the login view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) contentArea.getScene().getWindow();
            
            // Set the new scene
            stage.setScene(new Scene(root, 800, 600));
            stage.setTitle("RHMS - Login");
            stage.setResizable(false);
            stage.centerOnScreen();
            
            // Now logout the user after UI is updated
            SessionManager.logout();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not log out: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewHealthTrends(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox trendsView = new VBox(15);
        trendsView.setPadding(new Insets(20));
        trendsView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("My Health Trends");
        headerLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Get vitals data
        VitalsDatabase vitalsDB = currentPatient.getVitalsDatabase();
        
        if (vitalsDB == null || vitalsDB.getVitals().isEmpty()) {
            Label noDataLabel = new Label("No health data available for analysis. Please upload vital signs data first.");
            Button uploadButton = new Button("Upload Vitals Data");
            uploadButton.setOnAction(e -> handleViewHealthData(null));
            
            trendsView.getChildren().addAll(headerLabel, noDataLabel, uploadButton);
            contentArea.getChildren().add(trendsView);
            return;
        }
        
        // Create analytics result
        HealthAnalytics analytics = new HealthAnalytics();
        AnalyticsResult result = analytics.analyzePatientData(currentPatient);
        
        // Summary section
        VBox summaryBox = new VBox(10);
        summaryBox.setPadding(new Insets(15));
        summaryBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label summaryLabel = new Label("Health Summary");
        summaryLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        GridPane summaryGrid = new GridPane();
        summaryGrid.setHgap(15);
        summaryGrid.setVgap(10);
        
        summaryGrid.add(new Label("Heart Rate (avg):"), 0, 0);
        summaryGrid.add(new Label(String.format("%.1f bpm", result.getHeartRateAvg())), 1, 0);
        Label hrTrendLabel = new Label(String.format("%+.1f", result.getHeartRateTrend()));
        hrTrendLabel.setStyle(result.getHeartRateTrend() > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        summaryGrid.add(hrTrendLabel, 2, 0);
        
        summaryGrid.add(new Label("Oxygen Level (avg):"), 0, 1);
        summaryGrid.add(new Label(String.format("%.1f%%", result.getOxygenAvg())), 1, 1);
        Label o2TrendLabel = new Label(String.format("%+.1f", result.getOxygenTrend()));
        o2TrendLabel.setStyle(result.getOxygenTrend() > 0 ? "-fx-text-fill: green;" : "-fx-text-fill: red;");
        summaryGrid.add(o2TrendLabel, 2, 1);
        
        summaryGrid.add(new Label("Blood Pressure (avg):"), 0, 2);
        summaryGrid.add(new Label(String.format("%.1f mmHg", result.getBpAvg())), 1, 2);
        Label bpTrendLabel = new Label(String.format("%+.1f", result.getBpTrend()));
        bpTrendLabel.setStyle(result.getBpTrend() > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        summaryGrid.add(bpTrendLabel, 2, 2);
        
        summaryGrid.add(new Label("Temperature (avg):"), 0, 3);
        summaryGrid.add(new Label(String.format("%.1f°C", result.getTempAvg())), 1, 3);
        Label tempTrendLabel = new Label(String.format("%+.1f", result.getTempTrend()));
        tempTrendLabel.setStyle(result.getTempTrend() > 0 ? "-fx-text-fill: red;" : "-fx-text-fill: green;");
        summaryGrid.add(tempTrendLabel, 2, 3);
        
        summaryBox.getChildren().addAll(summaryLabel, summaryGrid);
        
        // Insights section
        VBox insightsBox = new VBox(10);
        insightsBox.setPadding(new Insets(15));
        insightsBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label insightsLabel = new Label("Health Insights");
        insightsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        VBox insightsList = new VBox(5);
        
        if (result.getInsights().isEmpty()) {
            insightsList.getChildren().add(new Label("No specific insights available with current data."));
        } else {
            for (String insight : result.getInsights()) {
                Label insightItem = new Label("• " + insight);
                insightsList.getChildren().add(insightItem);
            }
        }
        
        insightsBox.getChildren().addAll(insightsLabel, insightsList);
        
        // Chart section
        VBox chartBox = new VBox(10);
        chartBox.setPadding(new Insets(15));
        chartBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5px;");
        
        Label chartLabel = new Label("Vital Signs Charts");
        chartLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        // Create tab pane for different charts
        TabPane chartTabPane = new TabPane();
        chartTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Heart rate tab
        Tab heartRateTab = new Tab("Heart Rate");
        LineChart<Number, Number> heartRateChart = com.rhms.gui.util.ChartGenerator.generateVitalLineChart(
                vitalsDB.getVitals(), "heartRate");
        heartRateTab.setContent(heartRateChart);
        
        // Oxygen level tab
        Tab oxygenTab = new Tab("Oxygen Level");
        LineChart<Number, Number> oxygenChart = com.rhms.analytics.ChartGenerator.generateVitalLineChart(
                vitalsDB.getVitals(), "oxygenLevel");
        oxygenTab.setContent(oxygenChart);
        
        // Blood pressure tab
        Tab bpTab = new Tab("Blood Pressure");
        LineChart<Number, Number> bpChart = com.rhms.analytics.ChartGenerator.generateVitalLineChart(
                vitalsDB.getVitals(), "bloodPressure");
        bpTab.setContent(bpChart);
        
        // Temperature tab
        Tab tempTab = new Tab("Temperature");
        LineChart<Number, Number> tempChart = com.rhms.analytics.ChartGenerator.generateVitalLineChart(
                vitalsDB.getVitals(), "temperature");
        tempTab.setContent(tempChart);
        
        chartTabPane.getTabs().addAll(heartRateTab, oxygenTab, bpTab, tempTab);
        chartTabPane.setPrefHeight(400);
        
        chartBox.getChildren().addAll(chartLabel, chartTabPane);
        
        // Actions section
        HBox actionsBox = new HBox(15);
        actionsBox.setAlignment(Pos.CENTER);
        
        Button generateReportButton = new Button("Generate Health Report");
        generateReportButton.setOnAction(e -> handleGenerateReport());
        
        Button emailDoctorButton = new Button("Email Results to Doctor");
        emailDoctorButton.setOnAction(e -> handleEmailDoctor());
        
        actionsBox.getChildren().addAll(generateReportButton, emailDoctorButton);
        
        trendsView.getChildren().addAll(headerLabel, summaryBox, insightsBox, chartBox, actionsBox);
        contentArea.getChildren().add(trendsView);
    }
    
    private void handleGenerateReport() {
        ReportsGenerator generator = new ReportsGenerator();
        
        try {
            String reportPath = generator.generatePatientReport(currentPatient, true);
            
            if (reportPath != null) {
                showAlert(Alert.AlertType.INFORMATION, "Report Generated", 
                         "Health report has been generated successfully at:\n" + reportPath);
            } else {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Failed to generate health report.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error", 
                     "Error generating report: " + e.getMessage());
        }
    }
    
    private void handleEmailDoctor() {
        // Create dialog to choose doctor
        Dialog<Doctor> dialog = new Dialog<>();
        dialog.setTitle("Email Health Results");
        dialog.setHeaderText("Choose a doctor to email your health results");
        
        ButtonType sendButtonType = new ButtonType("Send", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sendButtonType, ButtonType.CANCEL);
        
        // Create doctor selection
        ComboBox<Doctor> doctorComboBox = new ComboBox<>();
        
        // Create custom cell factory
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
        
        // Set button cell
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
        
        // Fill dropdown with doctors
        ArrayList<Doctor> doctors = SessionManager.getDoctors();
        doctorComboBox.setItems(FXCollections.observableArrayList(doctors));
        if (!doctors.isEmpty()) {
            doctorComboBox.setValue(doctors.get(0));
        }
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        grid.add(new Label("Select Doctor:"), 0, 0);
        grid.add(doctorComboBox, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert result
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sendButtonType) {
                return doctorComboBox.getValue();
            }
            return null;
        });
        
        Optional<Doctor> result = dialog.showAndWait();
        
        result.ifPresent(doctor -> {
            // Get analytics results
            HealthAnalytics analytics = new HealthAnalytics();
            AnalyticsResult analyticsResult = analytics.analyzePatientData(currentPatient);
            
            // Create email content
            StringBuilder emailContent = new StringBuilder();
            emailContent.append("Dear Dr. ").append(doctor.getName()).append(",\n\n");
            emailContent.append("Please find my recent health metrics below:\n\n");
            
            emailContent.append("VITAL SIGNS SUMMARY:\n");
            emailContent.append(String.format("- Heart Rate: %.1f bpm (Trend: %+.1f)\n", 
                    analyticsResult.getHeartRateAvg(), analyticsResult.getHeartRateTrend()));
            emailContent.append(String.format("- Oxygen Level: %.1f%% (Trend: %+.1f)\n", 
                    analyticsResult.getOxygenAvg(), analyticsResult.getOxygenTrend()));
            emailContent.append(String.format("- Blood Pressure: %.1f mmHg (Trend: %+.1f)\n", 
                    analyticsResult.getBpAvg(), analyticsResult.getBpTrend()));
            emailContent.append(String.format("- Temperature: %.1f°C (Trend: %+.1f)\n\n", 
                    analyticsResult.getTempAvg(), analyticsResult.getTempTrend()));
            
            emailContent.append("I would appreciate your feedback on these results.\n\n");
            emailContent.append("Regards,\n");
            emailContent.append(currentPatient.getName());
            
            // Send email
            EmailNotification emailNotification = SessionManager.getEmailNotification();
            emailNotification.sendNotification(
                    doctor.getEmail(),
                    "Health Data from " + currentPatient.getName(),
                    emailContent.toString()
            );
            
            showAlert(Alert.AlertType.INFORMATION, "Email Sent", 
                     "Your health data has been emailed to Dr. " + doctor.getName());
        });
    }
    
    @FXML
    private void handleVideoConsultation(ActionEvent event) {
        contentArea.getChildren().clear();
        
        VBox videoConsultView = new VBox(15);
        videoConsultView.setPadding(new Insets(20));
        videoConsultView.setAlignment(Pos.TOP_CENTER);
        
        Label headerLabel = new Label("Video Consultation");
        headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Create a card-like container for the video call section
        VBox videoCallCard = new VBox(15);
        videoCallCard.setStyle("-fx-background-color: white; -fx-padding: 20px; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3); " +
                              "-fx-background-radius: 5px;");
        videoCallCard.setAlignment(Pos.CENTER);
        
        Label instructionsLabel = new Label("Enter the meeting ID provided by your doctor to join a video consultation");
        instructionsLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #6c757d;");
        
        HBox meetingIdBox = new HBox(10);
        meetingIdBox.setAlignment(Pos.CENTER);
        
        TextField meetingIdField = new TextField();
        meetingIdField.setPromptText("Enter meeting ID");
        meetingIdField.setPrefWidth(250);
        
        Button joinButton = new Button("Join Consultation");
        joinButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold; " +
                           "-fx-padding: 10px 20px; -fx-border-radius: 5px;");
        
        meetingIdBox.getChildren().addAll(meetingIdField, joinButton);
        
        // List of scheduled video consultations from approved appointments
        VBox scheduledConsultationsBox = new VBox(10);
        
        Label scheduledLabel = new Label("Scheduled Video Consultations");
        scheduledLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        
        ListView<String> consultationListView = new ListView<>();
        consultationListView.setPrefHeight(200);
        
        // Get approved appointments
        ArrayList<Appointment> approvedAppointments = new ArrayList<>();
        for (Appointment appointment : SessionManager.getAppointmentManager().getPatientAppointments(currentPatient)) {
            if (appointment.getStatus().equals("Approved")) {
                approvedAppointments.add(appointment);
            }
        }
        
        // Add appointments to list view
        if (approvedAppointments.isEmpty()) {
            consultationListView.setPlaceholder(new Label("No scheduled video consultations"));
        } else {
            ObservableList<String> appointmentItems = FXCollections.observableArrayList();
            for (Appointment appointment : approvedAppointments) {
                appointmentItems.add(String.format("Dr. %s - %s", 
                                                  appointment.getDoctor().getName(),
                                                  dateFormat.format(appointment.getAppointmentDate())));
            }
            consultationListView.setItems(appointmentItems);
        }
        
        scheduledConsultationsBox.getChildren().addAll(scheduledLabel, consultationListView);
        
        // Add tips for good video experience
        TitledPane tipsTitledPane = new TitledPane();
        tipsTitledPane.setText("Tips for a Good Video Consultation Experience");
        
        VBox tipsBox = new VBox(5);
        tipsBox.setPadding(new Insets(10));
        
        tipsBox.getChildren().addAll(
            new Label("• Ensure you have a stable internet connection"),
            new Label("• Use a quiet, well-lit space for your consultation"),
            new Label("• Test your camera and microphone before joining"),
            new Label("• Have any relevant medical documents ready"),
            new Label("• Prepare a list of questions or concerns to discuss")
        );
        
        tipsTitledPane.setContent(tipsBox);
        
        // Join button action
        joinButton.setOnAction(e -> {
            String meetingId = meetingIdField.getText().trim();
            if (meetingId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Missing Information", 
                         "Please enter a meeting ID to join the consultation.");
                return;
            }
            
            try {
                VideoCall.joinVideoCall(meetingId);
                showAlert(Alert.AlertType.INFORMATION, "Joining Video Call", 
                         "Connecting to video consultation with ID: " + meetingId);
            } catch (Exception ex) {
                showAlert(Alert.AlertType.ERROR, "Error", 
                         "Failed to join video call: " + ex.getMessage());
            }
        });
        
        videoCallCard.getChildren().addAll(instructionsLabel, meetingIdBox);
        
        videoConsultView.getChildren().addAll(headerLabel, videoCallCard, scheduledConsultationsBox, tipsTitledPane);
        contentArea.getChildren().add(videoConsultView);
    }
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}