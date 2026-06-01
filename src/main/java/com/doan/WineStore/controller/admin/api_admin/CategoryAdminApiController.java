package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.CategoryListItemResponse;
import com.doan.WineStore.service.admin.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/categories")
public class CategoryAdminApiController {
    @Autowired
    private AdminCategoryService adminCategoryService;

    @GetMapping
    public PageResponse<CategoryListItemResponse> getCategories(@RequestParam(defaultValue = "0") int page) {
        return adminCategoryService.getCategories(page);
    }
}
