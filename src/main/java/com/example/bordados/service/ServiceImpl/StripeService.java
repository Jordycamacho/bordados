package com.example.bordados.service.ServiceImpl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Value;

@Service
public class StripeService {

    @Value("${stripe.key.secret}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    public String createPaymentIntent(long amount, String currency, Long orderId) throws StripeException {
        // Validación de parámetros
        if (amount <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }
        if (currency == null || currency.trim().isEmpty()) {
            throw new IllegalArgumentException("La moneda no puede estar vacía.");
        }
        

        try {
            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amount)
                    .setCurrency(currency)
                    
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Verificar que el clientSecret no sea nulo
            if (paymentIntent.getClientSecret() == null) {
                throw new RuntimeException("No se pudo generar el ClientSecret.");
            }

            return paymentIntent.getClientSecret(); // Retorna el Client Secret
        } catch (StripeException e) {
            // Log del error (puedes usar un logger aquí)
            System.err.println("Error al crear el PaymentIntent: " + e.getMessage());
            throw e; // Relanzar la excepción para manejarla en el controlador
        }
    }

    public void cancelPendingPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        if ("requires_payment_method".equals(paymentIntent.getStatus())) {
            paymentIntent.cancel();
        }
    }
}
