package com.example.bordados.service.ServiceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.example.bordados.DTOs.CustomizedProductDTO;
import com.example.bordados.model.CustomizedProductDetails;
import com.example.bordados.model.Product;
import com.example.bordados.repository.CustomizedProductDetailsRepository;
import com.example.bordados.repository.ProductRepository;
import com.example.bordados.service.CustomizedProductDetailsService;
import com.example.bordados.service.ImageService;

@Service
public class CustomizedProductDetailsServiceImpl implements CustomizedProductDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomizedProductDetailsServiceImpl.class);
    private final CustomizedProductDetailsRepository customizedProductDetailsRepository;
    private final ProductRepository productRepository;
    private final ImageService imageService;

    public CustomizedProductDetailsServiceImpl(CustomizedProductDetailsRepository customizedProductDetailsRepository,
            ProductRepository productRepository, ImageService imageService) {
        this.customizedProductDetailsRepository = customizedProductDetailsRepository;
        this.productRepository = productRepository;
        this.imageService = imageService;
    }

    @Override
    public CustomizedProductDTO saveCustomization(CustomizedProductDTO dto) {
        try {
            Product product = productRepository.findById(dto.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

            CustomizedProductDetails details = new CustomizedProductDetails();
            details.setProduct(product);
            details.setQuantity(dto.getQuantity());
            details.setSize(dto.getSize());
            details.setColor(dto.getColor());
            details.setEmbroideryType(dto.getEmbroideryType());
            details.setFirstEmbroideryPlacement(dto.getFirstEmbroideryPlacement());
            details.setObservations(dto.getObservations());

            if (dto.getFirstEmbroideryFile() != null && !dto.getFirstEmbroideryFile().isEmpty()) {
                String firstEmbroideryPath = imageService.saveImage(dto.getFirstEmbroideryFile());
                details.setFirstEmbroideryFile(firstEmbroideryPath);
            }

            details.setHasSecondEmbroidery(dto.isHasSecondEmbroidery());
            if (dto.isHasSecondEmbroidery()) {
                details.setSecondEmbroideryPlacement(dto.getSecondEmbroideryPlacement());
                details.setSecondEmbroideryType(dto.getSecondEmbroideryType());
                details.setObservationsSecondEmbroidery(dto.getObservationsSecondEmbroidery());

                if (dto.getSecondEmbroideryFile() != null && !dto.getSecondEmbroideryFile().isEmpty()) {
                    String secondEmbroideryPath = imageService.saveImage(dto.getSecondEmbroideryFile());
                    details.setSecondEmbroideryFile(secondEmbroideryPath);
                }
            }

            details.setHasSleeveEmbroidery(dto.isHasSleeveEmbroidery());
            if (dto.isHasSleeveEmbroidery()) {
                details.setSleeveSide(dto.getSleeveSide());
                details.setSleeveDesign(dto.getSleeveDesign());
                details.setSleeveThreadColor(dto.getSleeveThreadColor());
            }

            double extraCost = details.calculateAdditionalCost();
            CustomizedProductDetails savedDetails = customizedProductDetailsRepository.save(details);

            return new CustomizedProductDTO(savedDetails, extraCost);
        } catch (Exception e) {
            log.error("Error al guardar el producto personalizado: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al procesar la solicitud");
        }
    }

    @Override
    public CustomizedProductDTO getCustomizationById(Long id) {
        CustomizedProductDetails details = customizedProductDetailsRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalles no encontrados"));

        return new CustomizedProductDTO(details, details.calculateAdditionalCost());
    }

    public void deleteCustomizedProduct(Long id) {
        try {
            CustomizedProductDetails details = customizedProductDetailsRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Detalles no encontrados"));

            imageService.deleteImage(details.getFirstEmbroideryFile());
            if (details.isHasSecondEmbroidery()) {
                imageService.deleteImage(details.getSecondEmbroideryFile());
            }

            customizedProductDetailsRepository.deleteById(id);
            log.info("Producto personalizado eliminado: ID {}", id);
        } catch (Exception e) {
            log.error("Error al eliminar el producto personalizado: {}", e.getMessage(), e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error al eliminar el producto");
        }
    }

}
