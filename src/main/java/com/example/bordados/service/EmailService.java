package com.example.bordados.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.example.bordados.DTOs.CartDTO;
import com.example.bordados.model.CustomizedOrderDetail;
import com.example.bordados.model.User;
@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendContactEmail(String email, String subject, String phone, String message) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("jordycamacho225@gmail.com"); // Correo de destino
        mailMessage.setSubject("Contacto: " + subject);
        mailMessage.setText("Correo: " + email + "\nTeléfono: " + phone + "\nMensaje: " + message);
        mailSender.send(mailMessage);
    }

    public void sendWelcomeEmail(String email, String name) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("discountCode", "WELCOME10");

        String htmlContent = templateEngine.process("emails/welcome", context);

        helper.setTo(email);
        helper.setSubject("¡Bienvenido a Bordados!");
        helper.setText(htmlContent, true); // true indica que es HTML

        mailSender.send(mimeMessage);
    }

    public void sendOrderConfirmationEmail(String email, String orderNumber, User user, List<CartDTO> cartItems, double total) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        Context context = new Context();
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("user", user);
        context.setVariable("cartItems", cartItems);
        context.setVariable("total", total);

        String htmlContent = templateEngine.process("emails/orderConfirmation", context);

        helper.setTo(email);
        helper.setSubject("Confirmación de Orden #" + orderNumber);
        helper.setText(htmlContent, true); // true indica que es HTML

        mailSender.send(mimeMessage);
    }
    
    public void sendCustomOrderConfirmationEmail(String email, String orderNumber, User user, CustomizedOrderDetail detail, double total) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        // Crear el contexto para la plantilla Thymeleaf
        Context context = new Context();
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("user", user);
        context.setVariable("detail", detail); // Detalles del producto personalizado
        context.setVariable("total", total);

        // Procesar la plantilla HTML
        String htmlContent = templateEngine.process("emails/customOrderConfirmation", context);

        // Configurar el correo
        helper.setTo(email);
        helper.setSubject("Confirmación de Orden Personalizada #" + orderNumber);
        helper.setText(htmlContent, true); // true indica que es HTML

        // Enviar el correo
        mailSender.send(mimeMessage);
    }

    public void sendOrderShippedEmail(String email, String orderNumber, String tracking) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        Context context = new Context();
        context.setVariable("orderNumber", orderNumber);
        context.setVariable("tracking", tracking);

        String htmlContent = templateEngine.process("emails/orderShipped", context);

        helper.setTo(email);
        helper.setSubject("Tu pedido #" + orderNumber + " ha sido enviado");
        helper.setText(htmlContent, true);

        mailSender.send(mimeMessage);
    }
}