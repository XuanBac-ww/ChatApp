package com.example.SpringSecurity.service.Image;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.SpringSecurity.dto.response.api.ApiResponse;
import com.example.SpringSecurity.exception.AppException;
import com.example.SpringSecurity.model.Image;
import com.example.SpringSecurity.model.User;
import com.example.SpringSecurity.repository.IImageRepository;
import com.example.SpringSecurity.service.user.IUserValidationService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ImageService implements IImageService{
    private final IImageRepository imageRepository;
    private final IUserValidationService userValidationService;
    private final ObjectMapper objectMapper;
    private final Cloudinary cloudinary;

    @Transactional
    @Override
    public ApiResponse<Image> uploadImage(MultipartFile file, Long userId) throws IOException {
        User user = userValidationService.findById(userId);

        String newHash = calculateHash(file);

        Image oldImage = user.getProfileImage();

        if (oldImage != null && oldImage.getHash().equals(newHash)) {
            if (oldImage.isDeleted()) {
                imageRepository.restore(oldImage.getId());
            }
            return new ApiResponse<>(200,true,"Upload Image success",oldImage);
        }

        Optional<Image> imageByHashOpt = imageRepository.findByHash(newHash);

        Image imageToLink;

        if (imageByHashOpt.isPresent()) {
            imageToLink = imageByHashOpt.get();

            if (imageToLink.isDeleted()) {
                imageRepository.restore(imageToLink.getId());
            }

            if (imageToLink.getUser() != null && !imageToLink.getUser().getId().equals(userId)) {
                return new ApiResponse<>(200,false,"Image is using",null);
            }
        } else {
            Map uploadResult = uploadToCloudinary(file);


            imageToLink = new Image();
            imageToLink.setUrl((String) uploadResult.get("secure_url"));
            imageToLink.setHash(newHash);
            imageToLink.setData(buildMetadata(file, uploadResult));
        }

        if (oldImage != null) {
            deleteFromCloudinary(oldImage.getData());
            imageRepository.softDelete(oldImage.getId());
        }

        user.setProfileImage(imageToLink);
        imageToLink.setUser(user);
        imageRepository.save(imageToLink);
        return new ApiResponse<>(200,true,"Upload Image success",imageToLink);
    }

    @Transactional
    @Override
    public ApiResponse<Image> updateImage(Long imageId, MultipartFile file) throws IOException {

        Image image = imageRepository.findActiveById(imageId)
                .orElseThrow(() -> new AppException("Image Not Found"));

        String newHash = calculateHash(file);

        if (image.getHash().equals(newHash)) {
            return new ApiResponse<>(200,true,"Update success", image);
        }

        Optional<Image> conflictOpt = imageRepository.findByHash(newHash);
        if (conflictOpt.isPresent() && !conflictOpt.get().getId().equals(imageId)) {
            return new ApiResponse<>(200,false,"Image content exist with else Id",null);
        }

        deleteFromCloudinary(image.getData());



        Map uploadResult = uploadToCloudinary(file);

        image.setUrl((String) uploadResult.get("secure_url"));
        image.setHash(newHash);
        image.setData(buildMetadata(file, uploadResult));

        imageRepository.save(image);

        return new ApiResponse<>(200,true,"Update Success", image);
    }


    // helper
    private String calculateHash(MultipartFile file) throws IOException {
        try (InputStream is = file.getInputStream()) {
            return DigestUtils.sha256Hex(is);
        }
    }

    private Map uploadToCloudinary(MultipartFile file) throws IOException {
        return cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "resource_type", "auto" // tự nhận diện kiểu file
        ));
    }

    // Xóa file khỏi Cloudinary dựa trên public_id lưu trong 'data'
    private void deleteFromCloudinary(String imageDataJson) {
        if (imageDataJson == null || imageDataJson.isEmpty()) {
            return;
        }

        try {
            Map<String, Object> metadata = objectMapper.readValue(imageDataJson,
                    new TypeReference<Map<String, Object>>() {});

            String publicId = (String) metadata.get("public_id");

            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, Map.of("resource_type", "image"));
            }

        } catch (IOException e) {
            throw new AppException("Failed to delete image from Cloudinary");
        }
    }


    private String buildMetadata(MultipartFile file, Map uploadResult) {
        // Lấy public_id từ kết quả upload
        String publicId = (String) uploadResult.get("public_id");

        Map<String, Object> metadata = Map.of(
                "public_id", publicId, //  để xóa file
                "size", file.getSize(),
                "originalName", Objects.requireNonNull(file.getOriginalFilename()),
                "contentType", Objects.requireNonNull(file.getContentType())
        );

        try {
            return objectMapper.writeValueAsString(metadata);
        } catch (JsonProcessingException e) {
            return "{\"error\": \"Failed to generate metadata\"}";
        }
    }
}
