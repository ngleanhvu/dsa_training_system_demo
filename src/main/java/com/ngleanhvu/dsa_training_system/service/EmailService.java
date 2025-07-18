package com.ngleanhvu.dsa_training_system.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
}
