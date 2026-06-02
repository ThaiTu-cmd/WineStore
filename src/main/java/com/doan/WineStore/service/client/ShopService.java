package com.doan.WineStore.service.client;

import com.doan.WineStore.dto.response.client.ShopProductResponse;
import org.springframework.data.domain.Page;

public interface ShopService {
    Page<ShopProductResponse> getProducts(String categorySlug, String sort, String search,
                                          String priceRange, int page, int size);
}
