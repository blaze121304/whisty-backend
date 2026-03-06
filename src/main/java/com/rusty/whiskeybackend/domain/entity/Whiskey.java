package com.rusty.whiskeybackend.domain.entity;

import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.enums.WhiskeySubCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "whiskey")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Whiskey {

    @Column(nullable = false)
    private String name;                    // 위스키명 (필수)

    @Id
    @Column(nullable = false)
    private String englishName;             // 영문명 (PK)

    @Column(nullable = false)
    private String brand;                   // 브랜드 (필수)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WhiskeyCategory category;       // 종류 (필수)

    @ElementCollection
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "whiskey_sub_categories", joinColumns = @JoinColumn(name = "whiskey_id"))
    @Column(name = "sub_category")
    @Builder.Default
    private List<WhiskeySubCategory> subCategories = new ArrayList<>();  // 특성 (셰리, 피트, 버번)

    private Double abv;                     // 알코올 도수 (Alcohol By Volume, %)

    private Double volume;                  // 용량 (ml)

    private String nation;                  // 국가

    private String region;                  // 생산지역

    @Column(length = 1000)
    private String imageDataUrl;            // 이미지 URL (Base64 또는 파일 저장소 경로)

    @Column(length = 2000)
    private String notes;                   // 테이스팅 노트

    @Column(length = 1000)
    private String nose;                    // 노즈

    @Column(length = 1000)
    private String palate;                  // 팔레트

    @Column(length = 1000)
    private String finish;                  // 피니시

    @Column(length = 2000)
    private String personalNote;            // 개인 소감

    private Double starPoint;               // 별점

    @ElementCollection
    @CollectionTable(name = "whiskey_pairings", joinColumns = @JoinColumn(name = "whiskey_id"))
    @Builder.Default
    private List<Pairing> pairings = new ArrayList<>();  // 페어링 추천

    @ElementCollection
    @CollectionTable(name = "whiskey_flavor_tags", joinColumns = @JoinColumn(name = "whiskey_id"))
    @Column(name = "flavor_tag")
    @Builder.Default
    private List<String> flavorTags = new ArrayList<>();  // 테이스팅 프로파일 태그

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Long createdAt;                 // 생성일시 (timestamp)

    @LastModifiedDate
    @Column(nullable = false)
    private Long updatedAt;                 // 수정일시 (timestamp)
}
