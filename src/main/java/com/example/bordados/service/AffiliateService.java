package com.example.bordados.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

@Service
public class AffiliateService {
    public String generateAffiliateCode() {
        return UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
