package com.internship.tool.repository;

import com.internship.tool.entity.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for uploaded file metadata.
 */
@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {
}
