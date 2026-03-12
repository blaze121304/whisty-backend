package com.rusty.whiskeybackend.service;

import com.rusty.whiskeybackend.domain.entity.Pairing;
import com.rusty.whiskeybackend.domain.entity.Whiskey;
import com.rusty.whiskeybackend.domain.dto.PairingDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyRequestDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyResponseDto;
import com.rusty.whiskeybackend.common.exception.ResourceNotFoundException;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.domain.specification.WhiskeySpecification;
import com.rusty.whiskeybackend.repository.WhiskeyRepository;
import org.springframework.data.jpa.domain.Specification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WhiskeyService {

    private final WhiskeyRepository whiskeyRepository;
    private static final String UPLOAD_DIR = "src/main/resources/uploads/images";

    public Page<WhiskeyResponseDto> findAll(String style, String cask, String nation, String search, Pageable pageable) {
        Specification<Whiskey> spec = Specification
                .where(WhiskeySpecification.hasStyle(style))
                .and(WhiskeySpecification.hasCask(cask))
                .and(WhiskeySpecification.hasNation(nation))
                .and(WhiskeySpecification.hasSearch(search));
        return whiskeyRepository.findAll(spec, pageable).map(this::convertToResponseDto);
    }

    public WhiskeyResponseDto findById(Long id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));
        return convertToResponseDto(whiskey);
    }

    @Transactional
    public WhiskeyResponseDto create(WhiskeyRequestDto requestDto, MultipartFile image) {
        Whiskey whiskey = convertToEntity(requestDto);

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            whiskey.setImageDataUrl(imageUrl);
        }

        return convertToResponseDto(whiskeyRepository.save(whiskey));
    }

    @Transactional
    public WhiskeyResponseDto update(Long id, WhiskeyRequestDto requestDto, MultipartFile image) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        String existingImageUrl = whiskey.getImageDataUrl();
        updateEntityFromDto(whiskey, requestDto);

        if (image != null && !image.isEmpty()) {
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                deleteImageFile(existingImageUrl);
            }
            whiskey.setImageDataUrl(saveImage(image));
        }

        return convertToResponseDto(whiskeyRepository.save(whiskey));
    }

    @Transactional
    public void delete(Long id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImageFile(whiskey.getImageDataUrl());
        }

        whiskeyRepository.delete(whiskey);
    }

    @Transactional
    public String uploadImage(Long id, MultipartFile image) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImageFile(whiskey.getImageDataUrl());
        }

        String imageUrl = saveImage(image);
        whiskey.setImageDataUrl(imageUrl);
        whiskeyRepository.save(whiskey);

        return imageUrl;
    }

    @Transactional
    public void deleteImage(Long id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImageFile(whiskey.getImageDataUrl());
            whiskey.setImageDataUrl(null);
            whiskeyRepository.save(whiskey);
        }
    }

    // === Private Helper Methods ===

    private Whiskey convertToEntity(WhiskeyRequestDto dto) {
        List<Pairing> pairings = dto.getPairings() != null ?
                dto.getPairings().stream()
                        .map(p -> new Pairing(p.getIcon(), p.getName()))
                        .collect(Collectors.toList()) : List.of();

        return Whiskey.builder()
                .name(dto.getName())
                .englishName(dto.getEnglishName())
                .brand(dto.getBrand())
                .category(dto.getCategory())
                .characteristics(dto.getCharacteristics() != null ? dto.getCharacteristics() : List.of())
                .abv(dto.getAbv())
                .volume(dto.getVolume())
                .nation(dto.getNation())
                .region(dto.getRegion())
                .imageDataUrl(dto.getImageDataUrl())
                .notes(dto.getNotes())
                .nose(dto.getNose())
                .palate(dto.getPalate())
                .finish(dto.getFinish())
                .personalNote(dto.getPersonalNote())
                .starPoint(dto.getStarPoint() != null ? dto.getStarPoint() : 0.0)
                .pairings(pairings)
                .flavorTags(dto.getFlavorTags() != null ? dto.getFlavorTags() : List.of())
                .build();
    }

    private void updateEntityFromDto(Whiskey whiskey, WhiskeyRequestDto dto) {
        whiskey.setName(dto.getName());
        whiskey.setEnglishName(dto.getEnglishName());
        whiskey.setBrand(dto.getBrand());
        whiskey.setCategory(dto.getCategory());
        whiskey.setCharacteristics(dto.getCharacteristics() != null ? dto.getCharacteristics() : List.of());
        whiskey.setAbv(dto.getAbv());
        whiskey.setVolume(dto.getVolume());
        whiskey.setNation(dto.getNation());
        whiskey.setRegion(dto.getRegion());
        whiskey.setNotes(dto.getNotes());
        whiskey.setNose(dto.getNose());
        whiskey.setPalate(dto.getPalate());
        whiskey.setFinish(dto.getFinish());
        whiskey.setPersonalNote(dto.getPersonalNote());
        whiskey.setStarPoint(dto.getStarPoint() != null ? dto.getStarPoint() : 0.0);

        if (dto.getPairings() != null) {
            whiskey.setPairings(dto.getPairings().stream()
                    .map(p -> new Pairing(p.getIcon(), p.getName()))
                    .collect(Collectors.toList()));
        }

        whiskey.setFlavorTags(dto.getFlavorTags() != null ? dto.getFlavorTags() : List.of());
    }

    private WhiskeyResponseDto convertToResponseDto(Whiskey whiskey) {
        List<PairingDto> pairingDtos = whiskey.getPairings() != null ?
                whiskey.getPairings().stream()
                        .map(p -> new PairingDto(p.getIcon(), p.getName()))
                        .collect(Collectors.toList()) : List.of();

        return WhiskeyResponseDto.builder()
                .id(whiskey.getId())
                .name(whiskey.getName())
                .englishName(whiskey.getEnglishName())
                .brand(whiskey.getBrand())
                .category(whiskey.getCategory())
                .characteristics(whiskey.getCharacteristics())
                .abv(whiskey.getAbv())
                .volume(whiskey.getVolume())
                .nation(whiskey.getNation())
                .region(whiskey.getRegion())
                .imageDataUrl(whiskey.getImageDataUrl())
                .notes(whiskey.getNotes())
                .nose(whiskey.getNose())
                .palate(whiskey.getPalate())
                .finish(whiskey.getFinish())
                .personalNote(whiskey.getPersonalNote())
                .starPoint(whiskey.getStarPoint())
                .pairings(pairingDtos)
                .flavorTags(whiskey.getFlavorTags())
                .createdAt(whiskey.getCreatedAt())
                .updatedAt(whiskey.getUpdatedAt())
                .build();
    }

    private String saveImage(MultipartFile image) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID() + extension;

            Files.copy(image.getInputStream(), uploadPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    private void deleteImageFile(String imageUrl) {
        try {
            if (imageUrl.startsWith("/images/")) {
                String filename = imageUrl.substring("/images/".length());
                Files.deleteIfExists(Paths.get(UPLOAD_DIR).resolve(filename));
            }
        } catch (IOException e) {
            System.err.println("이미지 삭제 중 오류: " + e.getMessage());
        }
    }
}
