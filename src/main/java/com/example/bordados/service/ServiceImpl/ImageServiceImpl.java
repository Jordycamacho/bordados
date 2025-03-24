package com.example.bordados.service.ServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.example.bordados.service.ImageService;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final String  uploadDir = Paths.get("").toAbsolutePath().toString() + "/images/";
    
    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
            log.info("Directorio de imágenes creado en: {}", uploadDir);
        } catch (IOException e) {
            log.error("No se pudo crear el directorio de imágenes", e);
        }
    }
    
    @Override
    public String saveImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            log.info("No se subió imagen, se usará la imagen por defecto.");
            return getDefaultImage();
        }

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            String filePath = uploadDir + fileName;
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(filePath));
            log.info("Imagen guardada en: {}", filePath);
            return fileName;
        } catch (IOException e) {
            log.error("Error al guardar la imagen: {}", e.getMessage());
            throw new RuntimeException("Error al guardar la imagen", e);
        }
    }

    @Override
    public void deleteImage(String fileName) {
        try {
            String filePath = uploadDir + fileName;
            File file = new File(filePath);
            if (file.exists() && !fileName.equals(getDefaultImage())) {
                if (file.delete()) {
                    log.info("Imagen eliminada: {}", fileName);
                } else {
                    log.warn("No se pudo eliminar la imagen: {}", fileName);
                }
            }
        } catch (Exception e) {
            log.error("Error al eliminar la imagen: {}", e.getMessage());
        }
    }

    @Override
    public String getDefaultImage() {
        return "default.jpg";
    }
    
}
