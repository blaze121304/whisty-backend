package com.rusty.whiskeybackend.service;

import com.rusty.whiskeybackend.domain.entity.Pairing;
import com.rusty.whiskeybackend.domain.entity.Whiskey;
import com.rusty.whiskeybackend.domain.dto.PairingDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyRequestDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyResponseDto;
import com.rusty.whiskeybackend.common.exception.ResourceNotFoundException;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.repository.WhiskeyRepository;
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
    private static final String UPLOAD_DIR = "uploads/images";

    /**
     * 전체 위스키 목록 조회 (필터링 및 검색 지원)
     */
    public Page<WhiskeyResponseDto> findAll(WhiskeyCategory category, String search, Pageable pageable) {
        Page<Whiskey> whiskeys;

        if (category != null && search != null && !search.isBlank()) {
            whiskeys = whiskeyRepository.findByCategoryAndSearch(category, search, pageable);
        } else if (category != null) {
            whiskeys = whiskeyRepository.findByCategory(category, pageable);
        } else if (search != null && !search.isBlank()) {
            whiskeys = whiskeyRepository.searchByNameOrBrand(search, pageable);
        } else {
            whiskeys = whiskeyRepository.findAll(pageable);
        }

        return whiskeys.map(this::convertToResponseDto);
    }

    /**
     * ID로 위스키 조회
     */
    public WhiskeyResponseDto findById(String id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));
        return convertToResponseDto(whiskey);
    }

    /**
     * 위스키 생성
     */
    @Transactional
    public WhiskeyResponseDto create(WhiskeyRequestDto requestDto, MultipartFile image) {
        Whiskey whiskey = convertToEntity(requestDto);

        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            String imageUrl = saveImage(image);
            whiskey.setImageDataUrl(imageUrl);
        }

        Whiskey savedWhiskey = whiskeyRepository.save(whiskey);
        return convertToResponseDto(savedWhiskey);
    }

    /**
     * 위스키 수정
     */
    @Transactional
    public WhiskeyResponseDto update(String id, WhiskeyRequestDto requestDto, MultipartFile image) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        // 기존 이미지 저장
        String existingImageUrl = whiskey.getImageDataUrl();

        // 엔티티 업데이트
        updateEntityFromDto(whiskey, requestDto);

        // 이미지 처리
        if (image != null && !image.isEmpty()) {
            // 기존 이미지 삭제
            if (existingImageUrl != null && !existingImageUrl.isEmpty()) {
                deleteImage(existingImageUrl);
            }
            // 새 이미지 저장
            String imageUrl = saveImage(image);
            whiskey.setImageDataUrl(imageUrl);
        }

        Whiskey updatedWhiskey = whiskeyRepository.save(whiskey);
        return convertToResponseDto(updatedWhiskey);
    }

    /**
     * 위스키 삭제
     */
    @Transactional
    public void delete(String id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        // 이미지 삭제
        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImage(whiskey.getImageDataUrl());
        }

        whiskeyRepository.delete(whiskey);
    }

    /**
     * 이미지 업로드
     */
    @Transactional
    public String uploadImage(String id, MultipartFile image) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        // 기존 이미지 삭제
        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImage(whiskey.getImageDataUrl());
        }

        // 새 이미지 저장
        String imageUrl = saveImage(image);
        whiskey.setImageDataUrl(imageUrl);
        whiskeyRepository.save(whiskey);

        return imageUrl;
    }

    /**
     * 이미지 삭제
     */
    @Transactional
    public void deleteImage(String id) {
        Whiskey whiskey = whiskeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("위스키를 찾을 수 없습니다. ID: " + id));

        if (whiskey.getImageDataUrl() != null && !whiskey.getImageDataUrl().isEmpty()) {
            deleteImage(whiskey.getImageDataUrl());
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
                .subCategories(dto.getSubCategories() != null ? dto.getSubCategories() : List.of())
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
        whiskey.setSubCategories(dto.getSubCategories() != null ? dto.getSubCategories() : List.of());
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
            List<Pairing> pairings = dto.getPairings().stream()
                    .map(p -> new Pairing(p.getIcon(), p.getName()))
                    .collect(Collectors.toList());
            whiskey.setPairings(pairings);
        }

        whiskey.setFlavorTags(dto.getFlavorTags() != null ? dto.getFlavorTags() : List.of());
    }

    private WhiskeyResponseDto convertToResponseDto(Whiskey whiskey) {
        List<PairingDto> pairingDtos = whiskey.getPairings() != null ?
                whiskey.getPairings().stream()
                        .map(p -> new PairingDto(p.getIcon(), p.getName()))
                        .collect(Collectors.toList()) : List.of();

        return WhiskeyResponseDto.builder()
                .id(whiskey.getEnglishName())
                .name(whiskey.getName())
                .englishName(whiskey.getEnglishName())
                .brand(whiskey.getBrand())
                .category(whiskey.getCategory())
                .subCategories(whiskey.getSubCategories())
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
            // 업로드 디렉토리 생성
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 고유한 파일명 생성
            String originalFilename = image.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".") ?
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;

            // 파일 저장
            Path filePath = uploadPath.resolve(filename);
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "/images/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 중 오류가 발생했습니다.", e);
        }
    }

    private void deleteImage(String imageUrl) {
        try {
            if (imageUrl.startsWith("/images/")) {
                String filename = imageUrl.substring("/images/".length());
                Path filePath = Paths.get(UPLOAD_DIR).resolve(filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            // 로그 기록하고 계속 진행
            System.err.println("이미지 삭제 중 오류: " + e.getMessage());
        }
    }
}
