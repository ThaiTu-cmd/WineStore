package com.doan.WineStore.dto.response.admin;

import org.springframework.data.domain.Page;

import java.util.List;

public class PageResponse<T> {
    private final List<T> content;
    private final long totalElements;
    private final int totalPages;
    private final int page;
    private final int size;
    private final String message;

    public PageResponse(List<T> content, long totalElements, int totalPages, int page, int size, String message) {
        this.content = content;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.page = page;
        this.size = size;
        this.message = message;
    }

    public PageResponse(List<T> content, long totalElements, int totalPages, int page, int size) {
        this(content, totalElements, totalPages, page, size, null);
    }

    public static <T> PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize());
    }

    public List<T> getContent() {
        return content;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public String getMessage() {
        return message;
    }
}
