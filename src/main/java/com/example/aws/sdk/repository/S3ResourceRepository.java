package com.example.aws.sdk.repository;

import com.example.aws.sdk.model.S3Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface S3ResourceRepository extends JpaRepository<S3Resource, Long> {
    S3Resource findByBucketName(String name);
}
