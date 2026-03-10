package com.rusty.whiskeybackend.controller;

import com.rusty.whiskeybackend.domain.dto.WhiskeyRequestDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyResponseDto;
import com.rusty.whiskeybackend.domain.enums.WhiskeyCategory;
import com.rusty.whiskeybackend.service.WhiskeyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/whiskeys")
@RequiredArgsConstructor
public class WhiskeyController {

    private final WhiskeyService whiskeyService;

    /**
     * 전체 위스키 목록 조회
     */
    @GetMapping
    public ResponseEntity<Page<WhiskeyResponseDto>> getAllWhiskeys(
            @RequestParam(required = false) WhiskeyCategory category,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WhiskeyResponseDto> whiskeys = whiskeyService.findAll(category, search, pageable);
        return ResponseEntity.ok(whiskeys);
    }

    /**
     * 위스키 상세 조회
     */
    @GetMapping("/{id}")
    public ResponseEntity<WhiskeyResponseDto> getWhiskey(@PathVariable String id) {
        WhiskeyResponseDto whiskey = whiskeyService.findById(id);
        return ResponseEntity.ok(whiskey);
    }

    /**
     * 위스키 생성
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhiskeyResponseDto> createWhiskey(
            @Valid @ModelAttribute WhiskeyRequestDto request,
            @RequestParam(required = false) MultipartFile image) {
        WhiskeyResponseDto whiskey = whiskeyService.create(request, image);
        return ResponseEntity.status(HttpStatus.CREATED).body(whiskey);
    }

    /**
     * 위스키 수정
     */
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhiskeyResponseDto> updateWhiskey(
            @PathVariable String id,
            @Valid @ModelAttribute WhiskeyRequestDto request,
            @RequestParam(required = false) MultipartFile image) {
        WhiskeyResponseDto whiskey = whiskeyService.update(id, request, image);
        return ResponseEntity.ok(whiskey);
    }

    /**
     * 위스키 삭제
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWhiskey(@PathVariable String id) {
        whiskeyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 이미지 업로드
     */
    @PostMapping("/{id}/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable String id,
            @RequestParam MultipartFile image) {
        String imageUrl = whiskeyService.uploadImage(id, image);
        return ResponseEntity.ok(Map.of("imageDataUrl", imageUrl));
    }

    /**
     * 이미지 삭제
     */
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteImage(@PathVariable String id) {
        whiskeyService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 카테고리별 조회
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<Page<WhiskeyResponseDto>> getWhiskeysByCategory(
            @PathVariable WhiskeyCategory category,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WhiskeyResponseDto> whiskeys = whiskeyService.findAll(category, search, pageable);
        return ResponseEntity.ok(whiskeys);
    }

    /**
     * 검색
     */
    @GetMapping("/search")
    public ResponseEntity<Page<WhiskeyResponseDto>> searchWhiskeys(
            @RequestParam String q,
            @RequestParam(required = false) WhiskeyCategory category,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<WhiskeyResponseDto> whiskeys = whiskeyService.findAll(category, q, pageable);
        return ResponseEntity.ok(whiskeys);
    }

    @GetMapping("/cafe24/oauth/callback")
    public ResponseEntity<String> callback(
            @RequestParam String code,
            @RequestParam String state
    ) {
        return ResponseEntity.ok("code=" + code + ", state=" + state);
    }

}
