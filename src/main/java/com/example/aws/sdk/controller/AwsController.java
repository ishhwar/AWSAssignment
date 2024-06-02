package com.example.aws.sdk.controller;

import com.example.aws.sdk.model.EC2Resource;
import com.example.aws.sdk.model.JobStatus;
import com.example.aws.sdk.model.S3Resource;
import com.example.aws.sdk.repository.EC2ResourceRepository;
import com.example.aws.sdk.repository.JobStatusRepository;
import com.example.aws.sdk.repository.S3ObjectRepository;
import com.example.aws.sdk.repository.S3ResourceRepository;
import com.example.aws.sdk.service.AWSServicesAPI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/awssdk")
public class AwsController {

    @Autowired
    private AWSServicesAPI awsServicesAPI;
    @Autowired
    private JobStatusRepository jobStatusRepository;
    @Autowired
    private EC2ResourceRepository ec2ResourceRepository;
    @Autowired
    private S3ResourceRepository s3ResourceRepository;
    @Autowired
    private S3ObjectRepository s3ObjectRepository;


    @PostMapping("/discoverservices")
    public ResponseEntity<String> discoverServices(@RequestBody List<String> services) {
        try {
            String result = awsServicesAPI.discoverServices(services);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getjobresult/{jobId}")
    public ResponseEntity<String> getJobResult(@PathVariable String jobId) {
        String jobStatus = awsServicesAPI.getJobStatusByJobId(jobId);

        if (jobStatus == null) {
            return new ResponseEntity<>("Job status not found", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(jobStatus, HttpStatus.OK);
    }

    @GetMapping("/getdiscoveryresult/{service}")
    public ResponseEntity<List<String>> getDiscoveryResult(@PathVariable String service) {

        List<String> result = awsServicesAPI.getDiscoveryResult(service);
        if (result == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/gets3bucketobject")
    public String getS3BucketObjects(@RequestParam("bucketName") String bucketName) {
        String jobId = UUID.randomUUID().toString();
        awsServicesAPI.discoverS3BucketObjects(jobId, bucketName);
        return jobId;
    }

    @GetMapping("/gets3bucketobjectcount/{bucketName}")
    public ResponseEntity<Object> getS3BucketObjectCount(@PathVariable String bucketName) {
        try {
            long objectCount = awsServicesAPI.getS3BucketObjectCount(bucketName);
            return new ResponseEntity<>(objectCount, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error retrieving object count for bucket: " + bucketName, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/s3/{bucketName}/objects")
    public List<String> getS3BucketObjectLike(@PathVariable String bucketName, @RequestParam String pattern) {
        // URL-decode the pattern to handle special characters
        String decodedPattern = URLDecoder.decode(pattern, StandardCharsets.UTF_8);
        return awsServicesAPI.getS3BucketObjectLike(bucketName, decodedPattern);
    }
}
