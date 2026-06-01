package com.doan.WineStore.service.admin;

import com.doan.WineStore.dto.response.admin.PageResponse;
import com.doan.WineStore.dto.response.admin.ReviewListItemResponse;

public interface AdminReviewService {
    PageResponse<ReviewListItemResponse> getReviews(int page);

    void deleteReview(Long id);
}
