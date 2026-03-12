package com.rusty.whiskeybackend.controller;

import com.rusty.whiskeybackend.domain.dto.WhiskeyRequestDto;
import com.rusty.whiskeybackend.domain.dto.WhiskeyResponseDto;
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

    @GetMapping
    public ResponseEntity<Page<WhiskeyResponseDto>> getAllWhiskeys(
            @RequestParam(required = false) String style,
            @RequestParam(required = false) String cask,
            @RequestParam(required = false) String nation,
            @RequestParam(required = false) String search,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(whiskeyService.findAll(style, cask, nation, search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WhiskeyResponseDto> getWhiskey(@PathVariable Long id) {
        return ResponseEntity.ok(whiskeyService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhiskeyResponseDto> createWhiskey(
            @Valid @ModelAttribute WhiskeyRequestDto request,
            @RequestParam(required = false) MultipartFile image) {
        return ResponseEntity.status(HttpStatus.CREATED).body(whiskeyService.create(request, image));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<WhiskeyResponseDto> updateWhiskey(
            @PathVariable Long id,
            @Valid @ModelAttribute WhiskeyRequestDto request,
            @RequestParam(required = false) MultipartFile image) {
        return ResponseEntity.ok(whiskeyService.update(id, request, image));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteWhiskey(@PathVariable Long id) {
        whiskeyService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    public ResponseEntity<Map<String, String>> uploadImage(
            @PathVariable Long id,
            @RequestParam MultipartFile image) {
        return ResponseEntity.ok(Map.of("imageDataUrl", whiskeyService.uploadImage(id, image)));
    }

    @DeleteMapping("/{id}/image")
    public ResponseEntity<Void> deleteImage(@PathVariable Long id) {
        whiskeyService.deleteImage(id);
        return ResponseEntity.noContent().build();
    }
}
