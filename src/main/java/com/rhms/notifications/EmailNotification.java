package com.rhms.notifications;

import java.io.Serializable;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.*;

/**
 * Handles email notifications using JavaMail API
 */
public class EmailNotification implements Notifiable, Serializable {
    private static final long serialVersionUID = 1L;
    
    // SMTP server configuration
    private String host = "smtp.gmail.com";
    private int port = 587;
    private String username = ""; // Will be set through UI
    private String password = ""; // Will be set through UI
    
    // For testing/debug purposes
    private boolean simulateMode = true;
    
    @Override
    public void sendNotification(String recipient, String subject, String message) {
        if (simulateMode) {
            // Simulate the email sending for demonstration purposes
            System.out.println("\n=== Email Notification ===");
            System.out.println("To: " + recipient);
            System.out.println("Subject: " + subject);
            System.out.println("Message: " + message);
            System.out.println("======================\n");
            return;
        }
        
        try {
            // Set up mail server properties
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.trust", host);
            properties.put("mail.debug", "true");
            
            // Create session with authenticator
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create message
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(username));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);
            
            // Send message
            Transport.send(emailMessage);
            
            System.out.println("Email sent successfully to " + recipient);
            
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Send a formatted HTML email
     */
    public void sendHTMLEmail(String recipient, String subject, String htmlContent) {
        if (simulateMode) {
            System.out.println("\n=== HTML Email Notification ===");
            System.out.println("To: " + recipient);
            System.out.println("Subject: " + subject);
            System.out.println("HTML Content Length: " + htmlContent.length() + " characters");
            System.out.println("==============================\n");
            return;
        }
        
        try {
            // Set up mail server properties
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.trust", host);
            
            // Create session with authenticator
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create message
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(username));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);
            
            // Set HTML content
            emailMessage.setContent(htmlContent, "text/html; charset=utf-8");
            
            // Send message
            Transport.send(emailMessage);
            
            System.out.println("HTML Email sent successfully to " + recipient);
            
        } catch (MessagingException e) {
            System.out.println("Failed to send HTML email: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Set email server configuration
     * @param host SMTP server host
     * @param port SMTP server port
     * @param username Email account username
     * @param password Email account password
     */
    public void setEmailConfig(String host, int port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }
    
    /**
     * Toggle simulation mode for testing purposes
     * @param simulateMode true to simulate emails, false to actually send them
     */
    public void setSimulateMode(boolean simulateMode) {
        this.simulateMode = simulateMode;
    }
    
    /**
     * Check if email is in simulation mode
     * @return true if emails are being simulated, false if actually sending
     */
    public boolean isSimulateMode() {
        return simulateMode;
    }
    
    /**
     * Test the email configuration by sending a test message
     * @return true if configuration is working, false otherwise
     */
    public boolean testConfiguration() {
        try {
            // Create a session to test connection
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.ssl.trust", host);
            
            // Try to get a connection
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            Transport transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);
            transport.close();
            
            return true;
        } catch (MessagingException e) {
            System.out.println("Email configuration failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}