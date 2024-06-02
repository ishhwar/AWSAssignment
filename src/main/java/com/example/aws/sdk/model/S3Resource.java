package com.example.aws.sdk.model;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class S3Resource {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String jobId;
    private String bucketName;

    @OneToMany(mappedBy = "s3Resource", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<S3Objects> s3Objects;
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

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<S3Objects> getS3Objects() {
        return s3Objects;
    }

    public void setS3Objects(List<S3Objects> s3Objects) {
        this.s3Objects = s3Objects;
    }
}