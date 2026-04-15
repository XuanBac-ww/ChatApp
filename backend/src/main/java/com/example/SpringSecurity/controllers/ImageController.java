package com.example.SpringSecurity.controllers;

import com.example.SpringSecurity.annotation.RateLimit;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.model.Image;
import com.example.SpringSecurity.security.CustomUserDetails;
import com.example.SpringSecurity.service.Image.IImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/image")
@RequiredArgsConstructor
public class ImageController {
    private final IImageService iImageService;

    @PreAuthorize("hasRole('USER')")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    @PostMapping("/upload")
    public ApiResponse<Image> uploadImage(@AuthenticationPrincipal CustomUserDetails currentUser,
                                          @RequestParam("image") MultipartFile file) throws IOException {
        return iImageService.uploadImage(file,currentUser.getUserId());
    }

    @PreAuthorize("hasRole('USER')")
    @RateLimit(limit = 5,timeWindowSeconds = 60)
    @PutMapping("/update/{imageId}")
    public ApiResponse<Image> updateImage(@PathVariable Long imageId,
                                          @RequestParam("image") MultipartFile file) throws IOException {
        return iImageService.updateImage(imageId,file);
    }
}
