package com.example.bordados.service;

import com.example.bordados.DTOs.CustomizedProductDTO;

public interface CustomizedProductDetailsService  {

    CustomizedProductDTO saveCustomization(CustomizedProductDTO dto);
    CustomizedProductDTO getCustomizationById(Long id);
}
