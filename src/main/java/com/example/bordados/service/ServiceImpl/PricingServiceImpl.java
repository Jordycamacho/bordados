package com.example.bordados.service.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.repository.PricingConfigurationRepository;

@Service
public class PricingServiceImpl{

    @Autowired
    private PricingConfigurationRepository pricingConfigurationRepository;

    public PricingConfiguration getPricingConfiguration() {
        return pricingConfigurationRepository.findById(1L) 
                .orElseThrow(() -> new RuntimeException("Configuración de precios no encontrada"));
    }
    
}
