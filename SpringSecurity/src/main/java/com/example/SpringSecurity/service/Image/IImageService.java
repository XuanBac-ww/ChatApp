package com.example.SpringSecurity.service.Image;

import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface IImageService {
    ApiResponse<Image> uploadImage(MultipartFile file, Long userId) throws IOException;

    ApiResponse<Image> updateImage(Long imageId, MultipartFile file) throws IOException;
}
