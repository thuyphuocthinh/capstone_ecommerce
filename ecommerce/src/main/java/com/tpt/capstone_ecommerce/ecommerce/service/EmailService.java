package com.tpt.capstone_ecommerce.ecommerce.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailWithHtml(String to, String subject, String body) throws MessagingException;
    void sendEmailWithAttachment(String to, String subject, String body) throws MessagingException;
}
