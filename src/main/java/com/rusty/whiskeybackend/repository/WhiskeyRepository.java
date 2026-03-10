package com.rusty.whiskeybackend.repository;

import com.rusty.whiskeybackend.domain.entity.Whiskey;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WhiskeyRepository extends JpaRepository<Whiskey, Long> {

    // 카테고리별 조회
    Page<Whiskey> findByCategory(WhiskeyCategory category, Pageable pageable);

    // 이름 또는 브랜드로 검색
    @Query("SELECT w FROM Whiskey w WHERE " +
           "LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.brand) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Whiskey> searchByNameOrBrand(@Param("search") String search, Pageable pageable);

    // 카테고리 + 검색
    @Query("SELECT w FROM Whiskey w WHERE w.category = :category AND " +
           "(LOWER(w.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.brand) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<Whiskey> findByCategoryAndSearch(@Param("category") WhiskeyCategory category,
                                          @Param("search") String search, 
                                          Pageable pageable);

    // 카테고리별 개수 조회
    long countByCategory(WhiskeyCategory category);
}
