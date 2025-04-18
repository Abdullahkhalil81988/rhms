package com.rhms.doctorPatientInteraction;

import java.util.ArrayList;

public class MedicalHistory {
    private ArrayList<Feedback> pastConsultations;

    public MedicalHistory() {
        this.pastConsultations = new ArrayList<>();
    }

    public void addConsultation(Feedback feedback) {
        pastConsultations.add(feedback);
    }

    public ArrayList<Feedback> getPastConsultations() {
        return pastConsultations;
    }

    public void displayMedicalHistory() {
        if (pastConsultations.isEmpty()) {
            System.out.println("No past consultations available.");
        } else {
            for (Feedback feedback : pastConsultations) {
                feedback.displayFeedback();
                System.out.println("-----------------------");
            }
        }
    }
}
