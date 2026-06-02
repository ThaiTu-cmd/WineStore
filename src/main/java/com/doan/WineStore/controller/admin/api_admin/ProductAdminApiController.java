package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.request.admin.ProductUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.ProductDetailResponse;
import com.doan.WineStore.dto.response.admin.ProductListItemResponse;
import com.doan.WineStore.service.admin.AdminProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/api/products")
public class ProductAdminApiController {
    @Autowired
    private AdminProductService adminProductService;

    @GetMapping
    public PageResponse<ProductListItemResponse> getProducts(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(required = false) String search,
                                                              @RequestParam(required = false) Long categoryId,
                                                              @RequestParam(required = false) String stockStatus) {
        return adminProductService.getProducts(page, search, categoryId, stockStatus);
    }

    @GetMapping("/{id}")
    public ProductDetailResponse getProductById(@PathVariable Long id) {
        return adminProductService.getProductById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductDetailResponse createProduct(@RequestBody ProductUpsertRequest request) {
        return adminProductService.createProduct(request);
    }

    @PutMapping("/{id}")
    public ProductDetailResponse updateProduct(@PathVariable Long id, @RequestBody ProductUpsertRequest request) {
        return adminProductService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable Long id) {
        adminProductService.deleteProduct(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
