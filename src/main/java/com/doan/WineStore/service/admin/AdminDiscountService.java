package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;

public interface AdminDiscountService {
    PageResponse<DiscountListItemResponse> getDiscounts(int page);
}
