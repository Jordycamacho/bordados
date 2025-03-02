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

}