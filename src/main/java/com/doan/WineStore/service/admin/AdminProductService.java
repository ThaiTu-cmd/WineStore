package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.request.admin.ProductUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.ProductDetailResponse;
import com.doan.WineStore.dto.response.admin.ProductListItemResponse;

public interface AdminProductService {
    PageResponse<ProductListItemResponse> getProducts(int page, String search, Long categoryId, String stockStatus);

    ProductDetailResponse getProductById(Long id);

    ProductDetailResponse createProduct(ProductUpsertRequest request);

    ProductDetailResponse updateProduct(Long id, ProductUpsertRequest request);

    void deleteProduct(Long id);
}
