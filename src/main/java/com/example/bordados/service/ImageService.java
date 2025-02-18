package com.example.bordados.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    
    String saveImage(MultipartFile file);

    void deleteImage(String fileName);

    String getDefaultImage();
}
