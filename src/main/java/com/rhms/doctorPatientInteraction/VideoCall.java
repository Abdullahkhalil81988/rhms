package com.rhms.doctorPatientInteraction;

import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.util.Random;

/**
 * Manages video call functionality using Google Meet integration
 */
public class VideoCall {
    // Base URL for Google Meet video calls
    private static final String MEET_BASE_URL = "https://meet.google.com/";
    
    /**
     * Starts a video call by opening the default browser with Google Meet link
     * @param meetingId unique identifier for the meeting
     */
    public static void startVideoCall(String meetingId) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                URI meetingUri = new URI(MEET_BASE_URL + meetingId);
                desktop.browse(meetingUri);
            } else {
                System.out.println("Desktop browsing not supported. Meeting link: " 
                    + MEET_BASE_URL + meetingId);
            }
        } catch (Exception e) {
            System.out.println("Error starting video call: " + e.getMessage());
            System.out.println("Please manually open: " + MEET_BASE_URL + meetingId);
        }
    }
    
    /**
     * Joins an existing video call by opening the default browser with Google Meet link
     * @param meetingId unique identifier for the meeting to join
     * @throws Exception if there's an error joining the meeting
     */
    public static void joinVideoCall(String meetingId) throws Exception {
        if (meetingId == null || meetingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Meeting ID cannot be empty");
        }
        
        if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            URI meetingUri = new URI(MEET_BASE_URL + meetingId);
            desktop.browse(meetingUri);
        } else {
            throw new IOException("Desktop browsing not supported on this platform");
        }
    }
    
    /**
     * Generates a random meeting ID in format: xxx-xxxx-xxx
     * @return formatted meeting ID string
     */
    public static String generateMeetingId() {
        String chars = "abcdefghijkmnopqrstuvwxyz";
        StringBuilder meetingId = new StringBuilder();
        Random random = new Random();
        
        // First part (3 characters)
        for (int i = 0; i < 3; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        meetingId.append("-");
        
        // Second part (4 characters)
        for (int i = 0; i < 4; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        meetingId.append("-");
        
        // Third part (3 characters)
        for (int i = 0; i < 3; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return meetingId.toString();
    }
}