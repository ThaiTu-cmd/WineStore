package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.request.admin.DiscountUpsertRequest;
import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.DiscountListItemResponse;
import com.doan.WineStore.entity.DiscountCodeEntity;
import com.doan.WineStore.repository.DiscountCodeRepository;
import com.doan.WineStore.service.admin.AdminDiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

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

    @Override
    public DiscountListItemResponse createDiscount(DiscountUpsertRequest request) {
        if (request.getCode() == null || request.getCode().isBlank()) {
            throw new IllegalArgumentException("Discount code is required");
        }
        if (request.getDiscount() == null || request.getDiscount() <= 0) {
            throw new IllegalArgumentException("Discount must be greater than 0");
        }
        if (request.getDiscount() > 100) {
            throw new IllegalArgumentException("Discount cannot exceed 100%");
        }

        DiscountCodeEntity entity = new DiscountCodeEntity();
        entity.setCode(request.getCode().trim().toUpperCase());
        entity.setDiscount(BigDecimal.valueOf(request.getDiscount()));
        entity.setIsValid(request.getIsValid() == null ? true : request.getIsValid());
        entity.setTimesUsed(0);
        DiscountCodeEntity saved = discountCodeRepository.save(entity);
        return new DiscountListItemResponse(
                saved.getId(), saved.getCode(), saved.getDiscount(),
                saved.getIsValid() != null && saved.getIsValid(),
                saved.getTimesUsed() == null ? 0 : saved.getTimesUsed());
    }

    @Override
    public DiscountListItemResponse updateDiscount(Long id, DiscountUpsertRequest request) {
        DiscountCodeEntity entity = discountCodeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Discount not found"));

        if (request.getCode() != null && !request.getCode().isBlank()) {
            entity.setCode(request.getCode().trim().toUpperCase());
        }
        if (request.getDiscount() != null) {
            if (request.getDiscount() <= 0 || request.getDiscount() > 100) {
                throw new IllegalArgumentException("Discount must be between 1 and 100");
            }
            entity.setDiscount(BigDecimal.valueOf(request.getDiscount()));
        }
        if (request.getIsValid() != null) {
            entity.setIsValid(request.getIsValid());
        }

        DiscountCodeEntity saved = discountCodeRepository.save(entity);
        return new DiscountListItemResponse(
                saved.getId(), saved.getCode(), saved.getDiscount(),
                saved.getIsValid() != null && saved.getIsValid(),
                saved.getTimesUsed() == null ? 0 : saved.getTimesUsed());
    }

    @Override
    public void deleteDiscount(Long id) {
        if (!discountCodeRepository.existsById(id)) {
            throw new IllegalArgumentException("Discount not found");
        }
        discountCodeRepository.deleteById(id);
    }
}
