package com.example.aws.sdk.repository;


import com.example.aws.sdk.model.S3Objects;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface S3ObjectRepository extends JpaRepository<S3Objects, Long> {
    int countByS3ResourceId(Long s3ResourceId);
    @Query("SELECT s.fileName FROM S3Objects s WHERE s.s3Resource.id = :s3ResourceId AND s.fileName LIKE CONCAT('%', :pattern, '%')")
    List<String> findFileNamesByResourceIdAndPattern(@Param("s3ResourceId") Long s3ResourceId, @Param("pattern") String pattern);

}
