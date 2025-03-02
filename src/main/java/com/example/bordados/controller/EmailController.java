package com.example.bordados.controller;

import com.example.bordados.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/contact")
    public String handleContactForm(
            @RequestParam String nombre,
            @RequestParam String email,
            @RequestParam(required = false) String numero,
            @RequestParam String mensaje,
            Model model) {
        
        // Enviar el correo de contacto
        emailService.sendContactEmail(email, nombre, numero, mensaje);

        // Mostrar un mensaje de éxito en la vista
        model.addAttribute("mensajeEnviado", true);
        return "user/contact";
    }

    @PostMapping("/welcome")
    public String sendWelcomeEmail(@RequestParam String email, @RequestParam String name) {
        try {
            emailService.sendWelcomeEmail(email, name);
            return "Correo de bienvenida enviado";
        } catch (Exception e) {
            return "Error al enviar el correo de bienvenida: " + e.getMessage();
        }
    }

    @PostMapping("/orderConfirmation")
    public String sendOrderConfirmationEmail(
            @RequestParam String email,
            @RequestParam String orderNumber,
            @RequestParam String details) {
        try {
            emailService.sendOrderConfirmationEmail(email, orderNumber, details);
            return "Correo de confirmación de orden enviado";
        } catch (Exception e) {
            return "Error al enviar el correo de confirmación: " + e.getMessage();
        }
    }

    @PostMapping("/orderShipped")
    public String sendOrderShippedEmail(
            @RequestParam String email,
            @RequestParam String orderNumber,
            @RequestParam String tracking) {
        try {
            emailService.sendOrderShippedEmail(email, orderNumber, tracking);
            return "Correo de pedido enviado";
        } catch (Exception e) {
            return "Error al enviar el correo de pedido enviado: " + e.getMessage();
        }
    }
}