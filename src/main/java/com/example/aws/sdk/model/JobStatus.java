package com.example.aws.sdk.model;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "job_status")
public class JobStatus {

    @Id
    private String jobId;
    private String status;
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    // Getters and Setters

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
