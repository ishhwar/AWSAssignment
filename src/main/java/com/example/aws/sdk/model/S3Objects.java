package com.example.aws.sdk.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
public class S3Objects {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "s3_resource_id", nullable = false)
    private S3Resource s3Resource;

    private String fileName;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public S3Resource getS3Resource() {
        return s3Resource;
    }

    public void setS3Resource(S3Resource s3Resource) {
        this.s3Resource = s3Resource;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}