package com.example.bordados.DTOs;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {

    @NotEmpty(message = "Debe proporcionar al menos un correo electrónico")
    private List<String> emails;

    @NotBlank(message = "El asunto es obligatorio")
    private String subject; 

    @NotBlank(message = "El mensaje es obligatorio")
    private String message; 
}