package com.rusty.whiskeybackend.domain.dto;

import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.enums.WhiskeySubCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiskeyRequestDto {

    @NotBlank(message = "위스키명은 필수입니다.")
    private String name;

    private String englishName;

    @NotBlank(message = "브랜드는 필수입니다.")
    private String brand;

    @NotNull(message = "종류는 필수입니다.")
    private WhiskeyCategory category;

    private List<WhiskeySubCategory> subCategories;  // 특성 (셰리, 피트, 버번)

    private Double abv;              // 알코올 도수 (%)
    private Double volume;           // 용량 (ml)

    private String nation;           // 국가
    private String region;           // 생산지역

    private String imageDataUrl;     // 이미지 URL (Base64 또는 파일 경로)
    private String notes;            // 테이스팅 노트

    private String nose;             // 노즈
    private String palate;           // 팔레트
    private String finish;           // 피니시

    private String personalNote;     // 개인 소감
    private Double starPoint;               // 별점

    private List<PairingDto> pairings;   // 페어링
    private List<String> flavorTags;     // 테이스팅 프로파일 태그
}
