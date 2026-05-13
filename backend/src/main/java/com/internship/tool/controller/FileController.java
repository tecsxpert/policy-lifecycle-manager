package com.internship.tool.controller;

import com.internship.tool.entity.FileMetadata;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * Controller for file upload, download and preview endpoints.
 */
@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
@Tag(name = "File Management APIs", description = "File upload, download, and preview operations")
@SecurityRequirement(name = "Bearer JWT")
public class FileController {

    private final FileService fileService;

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload file", description = "Uploads a PDF file and stores metadata")
    @ApiResponse(responseCode = "200", description = "File uploaded successfully")
    @ApiResponse(responseCode = "400", description = "Invalid file type (only PDF allowed) or validation error")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetadata metadata = fileService.uploadFile(file);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Download file", description = "Downloads a file by its ID with attachment header")
    @ApiResponse(responseCode = "200", description = "File downloaded successfully")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws IOException {
        FileMetadata metadata = fileService.getFile(id);

        File file = new File(metadata.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("File not found on server");
        }

        Resource resource = new UrlResource(file.toURI());
        String encodedFilename = URLEncoder.encode(metadata.getFileName(), StandardCharsets.UTF_8);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFilename + "\"")
                .body(resource);
    }

    @GetMapping("/preview/{id}")
    @Operation(summary = "Preview file", description = "Displays file inline for preview (requires JWT token in browser)")
    @ApiResponse(responseCode = "200", description = "File preview loaded successfully")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required")
    public ResponseEntity<Resource> preview(@PathVariable Long id) throws IOException {
        FileMetadata metadata = fileService.getFile(id);

        File file = new File(metadata.getFilePath());
        if (!file.exists()) {
            throw new ResourceNotFoundException("File not found on server");
        }

        Resource resource = new UrlResource(file.toURI());
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(metadata.getFileType()))
                .body(resource);
    }
}
