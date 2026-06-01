package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.request.admin.CategoryUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.CategoryListItemResponse;
import com.doan.WineStore.service.admin.AdminCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryListItemResponse createCategory(@RequestBody CategoryUpsertRequest request) {
        return adminCategoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    public CategoryListItemResponse updateCategory(@PathVariable Long id, @RequestBody CategoryUpsertRequest request) {
        return adminCategoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable Long id) {
        adminCategoryService.deleteCategory(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
