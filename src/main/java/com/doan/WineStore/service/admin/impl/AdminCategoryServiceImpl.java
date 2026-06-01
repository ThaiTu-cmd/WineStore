package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.request.admin.CategoryUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.CategoryListItemResponse;
import com.doan.WineStore.entity.CategoryEntity;
import com.doan.WineStore.repository.CategoryRepository;
import com.doan.WineStore.service.admin.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public PageResponse<CategoryListItemResponse> getCategories(int page) {
        int safePage = Math.max(0, page);
        Page<CategoryListItemResponse> dtoPage = categoryRepository
                .findAdminCategories(PageRequest.of(safePage, PAGE_SIZE))
                .map(item -> new CategoryListItemResponse(
                        item.getId(),
                        item.getName(),
                        item.getSlug(),
                        item.getProducts() == null ? 0L : item.getProducts()));
        return PageResponse.fromPage(dtoPage);
    }

    @Override
    public CategoryListItemResponse createCategory(CategoryUpsertRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Category name is required");
        }
        if (request.getSlug() == null || request.getSlug().isBlank()) {
            throw new IllegalArgumentException("Category slug is required");
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setName(request.getName().trim());
        entity.setSlug(request.getSlug().trim().toLowerCase());
        CategoryEntity saved = categoryRepository.save(entity);
        return new CategoryListItemResponse(saved.getId(), saved.getName(), saved.getSlug(), 0L);
    }

    @Override
    public CategoryListItemResponse updateCategory(Long id, CategoryUpsertRequest request) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));

        if (request.getName() != null && !request.getName().isBlank()) {
            entity.setName(request.getName().trim());
        }
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            entity.setSlug(request.getSlug().trim().toLowerCase());
        }

        CategoryEntity saved = categoryRepository.save(entity);
        return new CategoryListItemResponse(saved.getId(), saved.getName(), saved.getSlug(), 0L);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new IllegalArgumentException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
