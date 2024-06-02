package com.example.aws.sdk.service.impl;

import com.example.aws.sdk.model.EC2Resource;
import com.example.aws.sdk.model.JobStatus;
import com.example.aws.sdk.model.S3Objects;
import com.example.aws.sdk.model.S3Resource;
import com.example.aws.sdk.repository.EC2ResourceRepository;
import com.example.aws.sdk.repository.JobStatusRepository;
import com.example.aws.sdk.repository.S3ObjectRepository;
import com.example.aws.sdk.repository.S3ResourceRepository;
import com.example.aws.sdk.service.AWSServicesAPI;
import com.example.aws.sdk.util.AwsSdkUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesRequest;
import software.amazon.awssdk.services.ec2.model.DescribeInstancesResponse;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AWSServicesAPIImpl implements AWSServicesAPI {

    private final Ec2Client ec2Client;
    private final S3Client s3Client;
    private final EC2ResourceRepository ec2ResourceRepository;
    private final S3ResourceRepository s3ResourceRepository;
    private final JobStatusRepository jobStatusRepository;
    private final S3ObjectRepository s3ObjectRepository;

    @Autowired
    public AWSServicesAPIImpl(Ec2Client ec2Client, S3Client s3Client, EC2ResourceRepository ec2ResourceRepository, S3ResourceRepository s3ResourceRepository, JobStatusRepository jobStatusRepository, S3ObjectRepository s3ObjectRepository) {
        this.ec2Client = ec2Client;
        this.s3Client = s3Client;
        this.ec2ResourceRepository = ec2ResourceRepository;
        this.s3ResourceRepository = s3ResourceRepository;
        this.jobStatusRepository = jobStatusRepository;
        this.s3ObjectRepository = s3ObjectRepository;
    }

    @Override
    public String discoverServices(List<String> services) {
        String jobId = UUID.randomUUID().toString();
        if (services.contains("EC2")) {
            discoverEc2Instances(jobId);
        }
        if (services.contains("S3")) {
            discoverS3Buckets(jobId);
        }
        return jobId;
    }

    @Override
    @Async
    public void discoverEc2Instances(String jobId) {
        updateJobStatus(jobId, AwsSdkUtil.IN_PROGRESS);
        try {
            DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
            DescribeInstancesResponse response = ec2Client.describeInstances(request);

            List<Instance> instances = response.reservations().stream()
                    .flatMap(reservation -> reservation.instances().stream())
                    .collect(Collectors.toList());

            for (Instance instance : instances) {
                EC2Resource resource = new EC2Resource();
                resource.setJobId(jobId);
                resource.setInstanceId(instance.instanceId());
                resource.setCreatedAt(AwsSdkUtil.getCurrentLocalDateTime());
                ec2ResourceRepository.save(resource);
            }
            updateJobStatus(jobId, AwsSdkUtil.SUCCESS);
        } catch (Exception e) {
            updateJobStatus(jobId, AwsSdkUtil.FAILURE);
        }
    }


    @Override
    @Async
    public void discoverS3Buckets(String jobId) {
        updateJobStatus(jobId, AwsSdkUtil.IN_PROGRESS);
        try {
            ListBucketsResponse response = s3Client.listBuckets();
            response.buckets().forEach(bucket -> {
                S3Resource resource = new S3Resource();
                resource.setJobId(jobId);
                resource.setBucketName(bucket.name());
                resource.setCreatedAt(AwsSdkUtil.getCurrentLocalDateTime());
                s3ResourceRepository.save(resource);
            });
            updateJobStatus(jobId, AwsSdkUtil.SUCCESS);
        } catch (Exception e) {
            updateJobStatus(jobId, AwsSdkUtil.FAILURE);
        }
    }

    //    @Override
//    @Async
//    public void discoverS3BucketObjects(String jobId, String bucketName) {
//        updateJobStatus(jobId, "In Progress");
//        try {
//            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();
//            ListObjectsV2Response result;
//            do {
//                result = s3Client.listObjectsV2(request);
//                for (S3Object s3Object : result.contents()) {
//                    AwsComponents resource = new AwsComponents();
//                    resource.setJobId(jobId);
//                    resource.setResourceType("S3Object");
//                    resource.setAwsComponentId(s3Object.key());
//                    awscomponentsrepository.save(resource);
//                }
//                request = request.toBuilder().continuationToken(result.nextContinuationToken()).build();
//            } while (result.isTruncated());
//            //updateJobStatus(jobId, "Success");
//        } catch (Exception e) {
//            //updateJobStatus(jobId, "Failed");
//            System.out.println(e);
//        }
//    }
    @Override
    @Async
    public void discoverS3BucketObjects(String jobId, String bucketName) {
        updateJobStatus(jobId, AwsSdkUtil.IN_PROGRESS);
        try {
            S3Resource resource = s3ResourceRepository.findByBucketName(bucketName);
            if (resource == null) {
                resource = new S3Resource();
                resource.setJobId(jobId);
                resource.setBucketName(bucketName);
                resource.setCreatedAt(AwsSdkUtil.getCurrentLocalDateTime());
                resource = s3ResourceRepository.save(resource);
            }

            ListObjectsV2Request request = ListObjectsV2Request.builder().bucket(bucketName).build();
            ListObjectsV2Response result;
            do {
                result = s3Client.listObjectsV2(request);
                for (S3Object s3Object : result.contents()) {
                    S3Objects newS3Object = new S3Objects();
                    newS3Object.setS3Resource(resource);
                    newS3Object.setFileName(s3Object.key());
                    newS3Object.setCreatedAt(AwsSdkUtil.getCurrentLocalDateTime());
                    s3ObjectRepository.save(newS3Object);
                }
                request = request.toBuilder().continuationToken(result.nextContinuationToken()).build();
            } while (result.isTruncated());

            updateJobStatus(jobId, AwsSdkUtil.SUCCESS);
        } catch (Exception e) {
            updateJobStatus(jobId, AwsSdkUtil.FAILURE);
        }
    }

    @Override
    public int getS3BucketObjectCount(String bucketName) {
        S3Resource s3Resource = s3ResourceRepository.findByBucketName(bucketName);
        if (s3Resource == null) {
            return 0;
        }
        return s3ObjectRepository.countByS3ResourceId(s3Resource.getId());
    }

    @Override
    public List<String> getS3BucketObjectLike(String bucketName, String pattern) {
        S3Resource s3Resource = s3ResourceRepository.findByBucketName(bucketName);
        if (s3Resource == null) {
            return List.of();
        }
        List<String> resultlist = s3ObjectRepository.findFileNamesByResourceIdAndPattern(s3Resource.getId(), pattern);
        return resultlist;
    }

    @Override
    public String getJobStatusByJobId(String jobId) {
        Optional<JobStatus> jobStatus = jobStatusRepository.findById(jobId);

        if (jobStatus.isPresent()) {
            String status = jobStatus.get().getStatus();
            return status;
        } else {
            return null;
        }
    }

    @Override
    public List<String> getDiscoveryResult(String service) {
        List<String> result;
        switch (service.toUpperCase()) {
            case "EC2":
                result = ec2ResourceRepository.findAll().stream()
                        .map(EC2Resource::getInstanceId)
                        .collect(Collectors.toList());
                break;

            case "S3":
                result = s3ResourceRepository.findAll().stream()
                        .map(S3Resource::getBucketName)
                        .collect(Collectors.toList());
                break;

            default:
                return null;
        }
        return result;
    }

    private void updateJobStatus(String jobId, String status) {
        JobStatus jobStatus = new JobStatus();
        jobStatus.setJobId(jobId);
        jobStatus.setStatus(status);
        jobStatus.setCreatedAt(AwsSdkUtil.getCurrentLocalDateTime());
        jobStatusRepository.save(jobStatus);
    }

}
