package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;
import com.doan.WineStore.repository.DiscountCodeRepository;
import com.doan.WineStore.service.admin.AdminDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminDiscountServiceImpl implements AdminDiscountService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private DiscountCodeRepository discountCodeRepository;

    @Override
    public PageResponse<DiscountListItemResponse> getDiscounts(int page) {
        int safePage = Math.max(0, page);
        Page<DiscountListItemResponse> dtoPage = discountCodeRepository
                .findAllByOrderByIdDesc(PageRequest.of(safePage, PAGE_SIZE))
                .map(item -> new DiscountListItemResponse(
                        item.getId(),
                        item.getCode(),
                        item.getDiscount(),
                        item.getIsValid() != null && item.getIsValid(),
                        item.getTimesUsed() == null ? 0 : item.getTimesUsed()));
        return PageResponse.fromPage(dtoPage);
    }
}
