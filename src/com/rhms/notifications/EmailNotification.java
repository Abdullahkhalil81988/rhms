package com.rhms.notifications;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

public class EmailNotification implements Notifiable {
    private final String senderEmail = "abdullahkhalil8198@gmail.com"; // Your Gmail
    private final String senderPassword = "visk zzic wbmm ewgz"; // Your Gmail App Password

    @Override
    public void sendNotification(String recipient, String subject, String message) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(senderEmail));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(message);

            Transport.send(mimeMessage);
            System.out.println("Email sent successfully to: " + recipient);
        } catch (MessagingException e) {
            System.out.println("Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}