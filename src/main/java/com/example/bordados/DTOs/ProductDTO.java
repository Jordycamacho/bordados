package com.example.bordados.DTOs;

import java.util.HashSet;
import java.util.Set;

import com.example.bordados.model.Enums.Color;
import com.example.bordados.model.Enums.Size;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;
    
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Builder.Default
    private String image = "default.jpg";
    
    @NotNull(message = "La cantidad es obligatoria")
    private int quantity;

    @NotNull(message = "El precio es obligatorio")
    private double price;

    @Builder.Default
    @NotNull
    private double discount = 0.0;

    @Builder.Default
    @NotNull(message = "La talla es obligatoria")
    private Set<Size> sizes = new HashSet<>();

    @Builder.Default
    @NotNull(message = "El color es obligatorio")
    private Set<Color> colors = new HashSet<>();

    @NotNull(message = "La categoría es obligatoria")
    private Long categoryId;

    @Builder.Default
    private Long subCategoryId = null;

}
