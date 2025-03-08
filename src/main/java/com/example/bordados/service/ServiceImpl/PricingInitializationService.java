package com.example.bordados.service.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.bordados.model.PricingConfiguration;
import com.example.bordados.repository.PricingConfigurationRepository;

import jakarta.annotation.PostConstruct;

@Service
public class PricingInitializationService {

    @Autowired
    private PricingConfigurationRepository pricingConfigurationRepository;

    @PostConstruct
    public void init() {
        if (pricingConfigurationRepository.count() == 0) {
            PricingConfiguration defaultPricing = new PricingConfiguration();
            defaultPricing.setSecondDesignPrice(10.0);
            defaultPricing.setSleevePrice(3.0);
            defaultPricing.setSmallSizeFirstEmbroideryPrice(0.0);
            defaultPricing.setMediumSizeFirstEmbroideryPrice(3.0);
            defaultPricing.setLargeSizeFirstEmbroideryPrice(7.0);
            defaultPricing.setSmallSizeSecondEmbroideryPrice(0.0);
            defaultPricing.setMediumSizeSecondEmbroideryPrice(3.0);
            defaultPricing.setLargeSizeSecondEmbroideryPrice(7.0);
            pricingConfigurationRepository.save(defaultPricing);
        }
    }
}