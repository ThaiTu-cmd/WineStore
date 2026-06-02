package com.doan.WineStore.service.client;

import com.doan.WineStore.dto.response.client.ShopProductResponse;
import com.doan.WineStore.entity.CategoryEntity;
import com.doan.WineStore.entity.ProductEntity;
import com.doan.WineStore.repository.CategoryRepository;
import com.doan.WineStore.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
public class ShopServiceImpl implements ShopService {

    private static final Map<String, String> PRICE_RANGES = Map.of(
            "under500k", "0-500000",
            "500k-1500k", "500000-1500000",
            "1500k-3000k", "1500000-3000000",
            "over3000k", "3000000-999999999"
    );

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Page<ShopProductResponse> getProducts(String categorySlug, String sort, String search,
                                                  String priceRange, int page, int size) {
        Sort sorting = resolveSort(sort);
        Pageable pageable = PageRequest.of(page, size, sorting);

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        if (priceRange != null && PRICE_RANGES.containsKey(priceRange)) {
            String[] parts = PRICE_RANGES.get(priceRange).split("-");
            minPrice = new BigDecimal(parts[0]);
            maxPrice = new BigDecimal(parts[1]);
        }

        Page<ProductEntity> entities = productRepository.findShopProducts(
                categorySlug, minPrice, maxPrice, pageable);

        return entities.map(this::toResponse);
    }

    private ShopProductResponse toResponse(ProductEntity entity) {
        String categoryName = "";
        String categorySlug = "";
        if (entity.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(entity.getCategoryId()).orElse(null);
            if (category != null) {
                categoryName = category.getName();
                categorySlug = category.getSlug();
            }
        }

        return new ShopProductResponse(
                entity.getId(),
                entity.getCategoryId(),
                categoryName,
                categorySlug,
                entity.getName(),
                entity.getImageUrl(),
                entity.getBrand(),
                entity.getPrice(),
                entity.getOldPrice(),
                entity.getRatingAvg() == null ? 0.0 : entity.getRatingAvg(),
                entity.getRatingCount() == null ? 0 : entity.getRatingCount(),
                entity.getCreatedAt()
        );
    }

    private Sort resolveSort(String sort) {
        if (sort == null) return Sort.by(Sort.Direction.DESC, "id");
        return switch (sort) {
            case "name-asc" -> Sort.by(Sort.Direction.ASC, "name");
            case "name-desc" -> Sort.by(Sort.Direction.DESC, "name");
            case "price-asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price-desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "newest" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "popular" -> Sort.by(Sort.Direction.DESC, "ratingAvg");
            default -> Sort.by(Sort.Direction.DESC, "id");
        };
    }
}
