package com.rhms.doctorPatientInteraction;

import java.awt.Desktop;
import java.net.URI;
import java.io.IOException;
import java.util.Random;

public class VideoCall {
    private static final String MEET_BASE_URL = "https://meet.google.com/";
    
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
    
    public static String generateMeetingId() {
        // Generate a random meeting ID format: xxx-xxxx-xxx
        String chars = "abcdefghijkmnopqrstuvwxyz";
        StringBuilder meetingId = new StringBuilder();
        Random random = new Random();
        
        // First part
        for (int i = 0; i < 3; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        meetingId.append("-");
        
        // Second part
        for (int i = 0; i < 4; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        meetingId.append("-");
        
        // Third part
        for (int i = 0; i < 3; i++) {
            meetingId.append(chars.charAt(random.nextInt(chars.length())));
        }
        
        return meetingId.toString();
    }
}