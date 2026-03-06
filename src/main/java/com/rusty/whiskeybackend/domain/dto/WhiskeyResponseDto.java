package com.rusty.whiskeybackend.domain.dto;

import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.enums.WhiskeySubCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiskeyResponseDto {
    private String id;
    private String name;
    private String englishName;
    private String brand;
    private WhiskeyCategory category;
    private List<WhiskeySubCategory> subCategories;
    private Double abv;
    private Double volume;
    private String nation;
    private String region;
    private String imageDataUrl;
    private String notes;
    private String nose;
    private String palate;
    private String finish;
    private String personalNote;
    private Double starPoint;
    private List<PairingDto> pairings;
    private List<String> flavorTags;
    private Long createdAt;
    private Long updatedAt;
}
