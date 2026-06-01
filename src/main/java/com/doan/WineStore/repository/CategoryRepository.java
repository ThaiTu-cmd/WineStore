package com.doan.WineStore.repository;

import com.doan.WineStore.entity.CategoryEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<CategoryEntity, Long> {

    @Query(value = """
            SELECT c.id AS id,
                   c.name AS name,
                   c.slug AS slug,
                   COUNT(p.id) AS products
            FROM categories c
            LEFT JOIN products p ON p.category_id = c.id
            GROUP BY c.id, c.name, c.slug
            ORDER BY c.id DESC
            """, countQuery = "SELECT COUNT(*) FROM categories", nativeQuery = true)
    Page<CategoryListProjection> findAdminCategories(Pageable pageable);

    interface CategoryListProjection {
        Long getId();

        String getName();

        String getSlug();

        Long getProducts();
    }
}
