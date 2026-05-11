package com.internship.tool.controller;

import com.internship.tool.entity.FileMetadata;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.service.FileService;
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
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<FileMetadata> uploadFile(@RequestParam("file") MultipartFile file) {
        FileMetadata metadata = fileService.uploadFile(file);
        return ResponseEntity.ok(metadata);
    }

    @GetMapping("/{id}")
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
