package com.rusty.whiskeybackend.domain.dto;

import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCharacteristic;
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

    private List<WhiskeyCharacteristic> characteristics;  // 특성 (셰리, 피트, 버번)

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
}
