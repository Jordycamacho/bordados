package com.example.bordados.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bordados.DTOs.EmailDTO;
import com.example.bordados.service.EmailService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody @Valid EmailDTO emailDTO) {
        emailService.sendEmail(emailDTO);
        return ResponseEntity.ok("Correo enviado exitosamente");
    }
}