package com.doan.WineStore.service.admin.impl;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.ReviewListItemResponse;
import com.doan.WineStore.repository.ReviewRepository;
import com.doan.WineStore.service.admin.AdminReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class AdminReviewServiceImpl implements AdminReviewService {
    private static final int PAGE_SIZE = 10;

    @Autowired
    private ReviewRepository reviewRepository;

    @Override
    public PageResponse<ReviewListItemResponse> getReviews(int page) {
        int safePage = Math.max(0, page);
        Page<ReviewListItemResponse> dtoPage = reviewRepository
                .findAdminReviews(PageRequest.of(safePage, PAGE_SIZE))
                .map(item -> new ReviewListItemResponse(
                        item.getId(),
                        item.getUser(),
                        item.getProduct(),
                        item.getRating() == null ? 0 : item.getRating(),
                        item.getComment()));
        return PageResponse.fromPage(dtoPage);
    }

    @Override
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new IllegalArgumentException("Review not found");
        }
        reviewRepository.deleteById(id);
    }
}
