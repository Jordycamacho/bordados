package com.example.bordados.DTOs;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategorySubCategoryDTO {
    private Long idCategory;
    private String nameCategory;
    private List<SubCategoryDTO> subCategories;
}
