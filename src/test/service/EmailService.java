package com.example.bordados.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.bordados.DTOs.EmailDTO;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(emailDTO.getEmails().toArray(new String[0]));
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getMessage());

        mailSender.send(message);
    }
}
