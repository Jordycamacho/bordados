package com.example.bordados.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/bordados/contacto")
public class ContactController {
    
    @GetMapping("")
    public String showContact(Model model) {
        model.addAttribute("mensajeEnviado", false);
        return "user/contact";
    }
    
}
