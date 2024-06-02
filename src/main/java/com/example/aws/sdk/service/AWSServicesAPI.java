package com.example.aws.sdk.service;

import java.util.List;

public interface AWSServicesAPI {
    String discoverServices(List<String> services);

    void discoverEc2Instances(String jobId);

    void discoverS3Buckets(String jobId);

    void discoverS3BucketObjects(String jobId, String bucketName);

    int getS3BucketObjectCount(String bucketName);

    public List<String> getS3BucketObjectLike(String bucketName, String pattern);

    public String getJobStatusByJobId(String jobId);

    public List<String> getDiscoveryResult(String service);
}
