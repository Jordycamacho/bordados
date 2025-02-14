package com.example.bordados.DTOs;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDTO {
    private Long idCategory;

    @NotBlank(message = "El nombre de la categoría es obligatorio")
    private String nameCategory;
}

