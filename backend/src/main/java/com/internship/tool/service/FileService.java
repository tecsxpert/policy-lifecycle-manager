package com.internship.tool.service;

import com.internship.tool.entity.FileMetadata;
import com.internship.tool.exception.InvalidRequestException;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.repository.FileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

/**
 * Service for file upload and retrieval operations.
 */
@Service
@RequiredArgsConstructor
public class FileService {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("application/pdf");
    private static final String UPLOAD_DIR = "uploads/files/";

    private final FileMetadataRepository fileMetadataRepository;

    public FileMetadata uploadFile(MultipartFile file) {
        validateFile(file);

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFilename.contains("..")) {
            throw new InvalidRequestException("Invalid file name");
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String storedFilename = UUID.randomUUID() + "_" + originalFilename;
            Path targetLocation = uploadPath.resolve(storedFilename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileMetadata metadata = FileMetadata.builder()
                    .fileName(originalFilename)
                    .fileType(file.getContentType())
                    .filePath(targetLocation.toString())
                    .fileSize(file.getSize())
                    .build();

            return fileMetadataRepository.save(metadata);
        } catch (IOException ex) {
            throw new InvalidRequestException("Failed to store file", ex);
        }
    }

    public FileMetadata getFile(Long id) {
        return fileMetadataRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with id: " + id));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidRequestException("File cannot be empty");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new InvalidRequestException("File size exceeds 10MB limit");
        }

        String type = file.getContentType();
        if (type == null || (!ALLOWED_CONTENT_TYPES.contains(type) && !type.startsWith("image/"))) {
            throw new InvalidRequestException("Invalid file type. Only PDF and image files are allowed");
        }
    }
}
