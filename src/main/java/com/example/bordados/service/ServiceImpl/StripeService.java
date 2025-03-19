package com.example.bordados.service.ServiceImpl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentIntentUpdateParams;

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

    public String createPaymentIntent(long amount, String currency, String paymentIntentId) throws StripeException {
        // Validación de parámetros mejorada
        if (amount <= 0)
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        if (currency == null || currency.trim().isEmpty())
            throw new IllegalArgumentException("Moneda requerida");

        try {
            if (paymentIntentId != null && !paymentIntentId.isEmpty()) {
                // Actualizar PaymentIntent existente
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

                PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams.builder()
                        .setAmount(amount)
                        .build();

                PaymentIntent updatedIntent = paymentIntent.update(updateParams);

                if (updatedIntent.getClientSecret() == null) {
                    throw new RuntimeException("Error actualizando PaymentIntent: ClientSecret nulo");
                }

                return updatedIntent.getClientSecret();
            } else {
                // Crear nuevo PaymentIntent
                PaymentIntentCreateParams createParams = PaymentIntentCreateParams.builder()
                        .setAmount(amount)
                        .setCurrency(currency.toLowerCase())
                        .addPaymentMethodType("card")
                        .build();

                PaymentIntent newIntent = PaymentIntent.create(createParams);

                if (newIntent.getClientSecret() == null) {
                    throw new RuntimeException("Error creando PaymentIntent: ClientSecret nulo");
                }

                return newIntent.getClientSecret();
            }
        } catch (StripeException e) {
            System.err.println("Error Stripe: " + e.getCode() + " - " + e.getMessage());
            throw e;
        }
    }

    public void cancelPendingPaymentIntent(String paymentIntentId) throws StripeException {
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        if ("requires_payment_method".equals(paymentIntent.getStatus())) {
            paymentIntent.cancel();
        }
    }
}
