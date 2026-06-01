package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.request.admin.DiscountUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;
import com.doan.WineStore.service.admin.AdminDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/admin/api/discounts")
public class DiscountAdminApiController {
    @Autowired
    private AdminDiscountService adminDiscountService;

    @GetMapping
    public PageResponse<DiscountListItemResponse> getDiscounts(@RequestParam(defaultValue = "0") int page) {
        return adminDiscountService.getDiscounts(page);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DiscountListItemResponse createDiscount(@RequestBody DiscountUpsertRequest request) {
        return adminDiscountService.createDiscount(request);
    }

    @PutMapping("/{id}")
    public DiscountListItemResponse updateDiscount(@PathVariable Long id, @RequestBody DiscountUpsertRequest request) {
        return adminDiscountService.updateDiscount(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiscount(@PathVariable Long id) {
        adminDiscountService.deleteDiscount(id);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        return ex.getMessage();
    }
}
