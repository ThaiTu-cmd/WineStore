package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.request.admin.DiscountUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;

public interface AdminDiscountService {
    PageResponse<DiscountListItemResponse> getDiscounts(int page);

    DiscountListItemResponse createDiscount(DiscountUpsertRequest request);

    DiscountListItemResponse updateDiscount(Long id, DiscountUpsertRequest request);

    void deleteDiscount(Long id);
}
