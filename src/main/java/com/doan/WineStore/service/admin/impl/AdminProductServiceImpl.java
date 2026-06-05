package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.request.admin.ProductUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.ProductDetailResponse;
import com.doan.WineStore.dto.response.admin.ProductListItemResponse;
import com.doan.WineStore.entity.ProductEntity;
import com.doan.WineStore.entity.ProductImageEntity;
import com.doan.WineStore.repository.ProductImageRepository;
import com.doan.WineStore.repository.ProductRepository;
import com.doan.WineStore.service.admin.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class AdminProductServiceImpl implements AdminProductService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Override
    public PageResponse<ProductListItemResponse> getProducts(int page, String search, Long categoryId, String stockStatus) {
        int safePage = Math.max(0, page);
        String safeSearch = (search == null || search.isBlank()) ? null : search.trim();
        String safeStock = (stockStatus == null || stockStatus.isBlank()) ? null : stockStatus.trim();
        Page<ProductListItemResponse> dtoPage = productRepository
                .findAdminProducts(safeSearch, categoryId, safeStock, PageRequest.of(safePage, PAGE_SIZE))
                .map(this::toListResponse);
        return PageResponse.fromPage(dtoPage);
    }

    @Override
    public ProductDetailResponse getProductById(Long id) {
        ProductEntity entity = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        return toDetailResponse(entity);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ProductDetailResponse createProduct(ProductUpsertRequest request) {
        validateRequest(request);
        ProductEntity entity = new ProductEntity();
        applyRequest(entity, request);
        if (entity.getOldPrice() == null) {
            entity.setOldPrice(entity.getPrice());
        }
        entity.setRatingAvg(0.0);
        entity.setRatingCount(0);
        entity.setDeletedAt(null);
        ProductEntity saved = productRepository.save(entity);
        saveImages(saved.getId(), request);
        return toDetailResponse(saved);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public ProductDetailResponse updateProduct(Long id, ProductUpsertRequest request) {
        validateRequest(request);
        ProductEntity entity = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        applyRequest(entity, request);
        if (entity.getOldPrice() == null) {
            entity.setOldPrice(entity.getPrice());
        }

        ProductEntity saved = productRepository.save(entity);
        saveImages(saved.getId(), request);
        return toDetailResponse(saved);
    }

    private void saveImages(Long productId, ProductUpsertRequest request) {
        List<String> images = request.getImages();
        if (images != null && !images.isEmpty()) {
            productImageRepository.deleteByProductId(productId);
            List<ProductImageEntity> entities = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                String url = images.get(i);
                if (url == null || url.isBlank()) continue;
                entities.add(new ProductImageEntity(productId, url.trim(), i == 0, i));
            }
            productImageRepository.saveAll(entities);
        } else if (request.getImageUrl() != null) {
            productImageRepository.deleteByProductId(productId);
            ProductImageEntity img = new ProductImageEntity(productId, request.getImageUrl(), true, 0);
            productImageRepository.save(img);
        }
    }

    @Override
    public void deleteProduct(Long id) {
        ProductEntity entity = productRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        entity.setDeletedAt(LocalDateTime.now());
        entity.setIsActive(false);
        productRepository.save(entity);
    }

    private void validateRequest(ProductUpsertRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Product request is required");
        }
        if (request.getCategoryId() == null) {
            throw new IllegalArgumentException("Category is required");
        }
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }
        if (request.getPrice() == null) {
            throw new IllegalArgumentException("Price is required");
        }
    }

    private void applyRequest(ProductEntity entity, ProductUpsertRequest request) {
        entity.setCategoryId(request.getCategoryId());
        entity.setName(request.getName().trim());
        String existingSku = entity.getSku();
        String existingSlug = entity.getSlug();
        entity.setSku(normalizeOrGenerate(request.getSku(), request.getName(), "SKU", existingSku));
        entity.setSlug(normalizeOrGenerate(request.getSlug(), request.getName(), "product", existingSlug));
        entity.setImageUrl(emptyToNull(request.getImageUrl()));
        entity.setBrand(emptyToNull(request.getBrand()));
        entity.setShortDescription(emptyToNull(request.getShortDescription()));
        entity.setDescription(emptyToNull(request.getDescription()));
        entity.setPrice(request.getPrice());
        entity.setOldPrice(request.getOldPrice());
        entity.setStockQuantity(request.getStockQuantity() == null ? 0 : request.getStockQuantity());
        entity.setIsActive(request.getIsActive() == null || request.getIsActive());
        if (entity.getRatingAvg() == null) {
            entity.setRatingAvg(0.0);
        }
        if (entity.getRatingCount() == null) {
            entity.setRatingCount(0);
        }
    }

    private ProductListItemResponse toListResponse(ProductEntity entity) {
        return new ProductListItemResponse(
                entity.getId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getBrand(),
                entity.getPrice(),
                entity.getRatingAvg() == null ? 0.0 : entity.getRatingAvg(),
                entity.getRatingCount() == null ? 0 : entity.getRatingCount(),
                entity.getStockQuantity() == null ? 0 : entity.getStockQuantity());
    }

    private ProductDetailResponse toDetailResponse(ProductEntity entity) {
        List<String> images = productImageRepository
                .findByProductIdOrderBySortOrderAsc(entity.getId())
                .stream()
                .map(ProductImageEntity::getImageUrl)
                .toList();
        return new ProductDetailResponse(
                entity.getId(),
                entity.getCategoryId(),
                entity.getSku(),
                entity.getSlug(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getBrand(),
                entity.getShortDescription(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getOldPrice(),
                entity.getRatingAvg() == null ? 0.0 : entity.getRatingAvg(),
                entity.getRatingCount() == null ? 0 : entity.getRatingCount(),
                entity.getStockQuantity() == null ? 0 : entity.getStockQuantity(),
                entity.getIsActive(),
                images);
    }

    private String normalizeOrGenerate(String value, String name, String prefix) {
        return normalizeOrGenerate(value, name, prefix, null);
    }

    private String normalizeOrGenerate(String value, String name, String prefix, String fallbackValue) {
        if (value != null && !value.isBlank()) {
            return value.trim();
        }
        if (fallbackValue != null && !fallbackValue.isBlank()) {
            return fallbackValue;
        }
        String base = slugify(name);
        return (prefix + "-" + base + "-" + System.currentTimeMillis()).toUpperCase(Locale.ROOT);
    }

    private String slugify(String input) {
        if (input == null) {
            return "item";
        }
        return input.trim()
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-");
    }

    private String emptyToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
