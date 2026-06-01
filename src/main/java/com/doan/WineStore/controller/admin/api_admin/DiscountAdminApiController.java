package com.doan.WineStore.controller.admin.api_admin;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;
import com.doan.WineStore.service.admin.AdminDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
}
