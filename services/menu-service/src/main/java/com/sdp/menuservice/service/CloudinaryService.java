package com.sdp.menuservice.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

    private final Cloudinary cloudinary;

    private final String MENU_ITEMS_FOLDER = "menu_items";

    /**
     * Uploads an image to Cloudinary
     *
     * @param file The image file to upload
     * @return Map containing the upload result with URLs and metadata
     */
    public Map<?, ?> uploadImage(MultipartFile file) {
        try {
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", MENU_ITEMS_FOLDER,
                            "resource_type", "auto"
                    )
            );

            log.info("Image uploaded successfully to Cloudinary: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Failed to upload image to Cloudinary", e);
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    /**
     * Uploads a base64-encoded image to Cloudinary
     *
     * @param base64Data The base64-encoded image data (without data URL prefix)
     * @return Map containing the upload result with URLs and metadata
     */
    public Map<?, ?> uploadBase64Image(String base64Data) {
        try {
            // Create data URI for Cloudinary (assuming PNG, adjust if needed)
            String dataUri = "data:image/png;base64," + base64Data;
            Map<?, ?> uploadResult = cloudinary.uploader().upload(
                    dataUri,
                    ObjectUtils.asMap(
                            "folder", MENU_ITEMS_FOLDER,
                            "resource_type", "auto"
                    )
            );

            log.info("Base64 image uploaded successfully to Cloudinary: {}", uploadResult.get("public_id"));
            return uploadResult;

        } catch (IOException e) {
            log.error("Failed to upload base64 image to Cloudinary", e);
            throw new RuntimeException("Failed to upload base64 image", e);
        }
    }

    /**
     * Deletes an image from Cloudinary
     *
     * @param publicId The public ID of the image to delete
     * @return Map containing the deletion result
     */
    public Map<?, ?> deleteImage(String publicId) {
        try {
            Map<?, ?> deleteResult = cloudinary.uploader().destroy(
                    publicId,
                    ObjectUtils.emptyMap()
            );

            log.info("Image deleted successfully from Cloudinary: {}", publicId);
            return deleteResult;

        } catch (IOException e) {
            log.error("Failed to delete image from Cloudinary", e);
            throw new RuntimeException("Failed to delete image", e);
        }
    }
}
