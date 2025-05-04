package com.rhms.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class RHMSApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Load FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
            Parent root = loader.load();
            
            // Set up stage
            primaryStage.setTitle("Remote Hospital Management System");
            primaryStage.setScene(new Scene(root, 800, 600));
            primaryStage.setResizable(false);
            primaryStage.show();
            
            // Add a shutdown hook to save data when application closes
            primaryStage.setOnCloseRequest(event -> {
                SessionManager.saveData();
                Platform.exit();
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        // Save data when application stops
        SessionManager.saveData();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}