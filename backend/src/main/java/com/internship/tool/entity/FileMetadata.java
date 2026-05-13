package com.internship.tool.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Metadata for uploaded files.
 */
@Entity
@Table(name = "file_metadata")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1", description = "Unique file identifier")
    private Long id;

    @Column(name = "file_name", nullable = false)
    @Schema(example = "policy-document.pdf", description = "Original file name")
    private String fileName;

    @Column(name = "file_type", nullable = false)
    @Schema(example = "application/pdf", description = "MIME type of the file")
    private String fileType;

    @Column(name = "file_path", nullable = false)
    @Schema(example = "/app/uploads/files/policy-document.pdf", description = "Server file path")
    private String filePath;

    @Column(name = "file_size", nullable = false)
    @Schema(example = "2048576", description = "File size in bytes")
    private long fileSize;

    @CreationTimestamp
    @Column(name = "uploaded_at", nullable = false, updatable = false)
    @Schema(example = "2024-01-15T10:30:00", description = "Upload timestamp")
    private LocalDateTime uploadedAt;
}
