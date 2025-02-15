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
public class SubCategoryDTO {

    private Long idSubCategory;
    
    @NotBlank(message = "El nombre de la subcategoría es obligatorio")
    private String nameSubCategory;

    private Long categoryId;

    private String categoryName;
}
