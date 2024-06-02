package com.example.aws.sdk.repository;


import com.example.aws.sdk.model.JobStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobStatusRepository extends JpaRepository<JobStatus, String> {
}
