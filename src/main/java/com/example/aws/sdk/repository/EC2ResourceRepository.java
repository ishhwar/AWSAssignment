package com.example.aws.sdk.repository;

import com.example.aws.sdk.model.EC2Resource;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EC2ResourceRepository extends JpaRepository<EC2Resource, Long> {
}
