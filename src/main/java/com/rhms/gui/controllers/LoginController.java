package com.rhms.gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import com.rhms.gui.SessionManager;
import com.rhms.userManagement.*;

public class LoginController {
    @FXML
    private ComboBox<String> userTypeCombo;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    public void initialize() {
        // Initialize the combo box with user types
        userTypeCombo.getItems().addAll("Patient", "Doctor", "Administrator");
        userTypeCombo.setValue("Patient");
    }
    
    @FXML
    private void handleLogin(ActionEvent event) {
        String userType = userTypeCombo.getValue();
        String email = emailField.getText();
        String password = passwordField.getText();
        
        // Validate inputs
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        // Attempt login
        boolean loginSuccess = false;
        
        try {
            switch (userType) {
                case "Patient":
                    loginSuccess = SessionManager.loginPatient(email, password);
                    break;
                case "Doctor":
                    loginSuccess = SessionManager.loginDoctor(email, password);
                    break;
                case "Administrator":
                    loginSuccess = SessionManager.loginAdministrator(email, password);
                    break;
            }
            
            if (loginSuccess) {
                // Login successful, navigate to the appropriate dashboard
                String dashboardFXML = "";
                
                switch (userType) {
                    case "Patient":
                        dashboardFXML = "/views/patientDashboard.fxml";
                        break;
                    case "Doctor":
                        dashboardFXML = "/views/doctorDashboard.fxml";
                        break;
                    case "Administrator":
                        dashboardFXML = "/views/adminDashboard.fxml";
                        break;
                }
                
                // Load the appropriate dashboard
                FXMLLoader loader = new FXMLLoader(getClass().getResource(dashboardFXML));
                Parent root = loader.load();
                
                // Get current stage
                Stage stage = (Stage) emailField.getScene().getWindow();
                
                // Set the new scene
                stage.setScene(new Scene(root, 1200, 800));
                stage.setTitle("RHMS - " + userType + " Dashboard");
                stage.setResizable(true);
                stage.centerOnScreen();
                
            } else {
                showError("Invalid credentials");
            }
        } catch (Exception e) {
            showError("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
