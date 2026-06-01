package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.CategoryListItemResponse;
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
}
