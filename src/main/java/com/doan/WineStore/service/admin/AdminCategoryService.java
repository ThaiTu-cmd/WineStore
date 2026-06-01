package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.request.admin.CategoryUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.CategoryListItemResponse;

public interface AdminCategoryService {
    PageResponse<CategoryListItemResponse> getCategories(int page);

    CategoryListItemResponse createCategory(CategoryUpsertRequest request);

    CategoryListItemResponse updateCategory(Long id, CategoryUpsertRequest request);

    void deleteCategory(Long id);
}
