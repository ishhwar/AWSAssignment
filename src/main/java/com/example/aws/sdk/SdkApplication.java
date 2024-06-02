package com.example.aws.sdk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;

@SpringBootApplication
public class SdkApplication {


	public static void main(String[] args) {
		SpringApplication.run(SdkApplication.class, args);
		Ec2Client ec2 = Ec2Client.builder()
				.credentialsProvider(StaticCredentialsProvider.create(
				AwsBasicCredentials.create("AKIAX5XSI5UT4M674AFL", "SEUYFy4LLDLvDG184lP2qM+PE/NFmi5MUCH3Wf7I")))
				.region(software.amazon.awssdk.regions.Region.AP_SOUTH_1)
				.build();
		S3Client s3Client = S3Client.builder()
				.region(Region.of(String.valueOf(Region.AP_SOUTH_1)))
				.credentialsProvider(StaticCredentialsProvider.create(
						AwsBasicCredentials.create("AKIAX5XSI5UT4M674AFL", "SEUYFy4LLDLvDG184lP2qM+PE/NFmi5MUCH3Wf7I")))
				.build();

//		ListObjectsV2Request request2 = ListObjectsV2Request.builder().bucket("nimesaassignmentbucket1").build();
//
//		ListObjectsV2Response result;
//		do {
//			result = s3Client.listObjectsV2(request2);
//			for (S3Object s3Object : result.contents()) {
////				AwsComponents resource = new AwsComponents();
////				resource.setJobId(jobId);
////				resource.setResourceType("S3Object");
////				resource.setDetails(s3Object.key());
////				awscomponentsrepository.save(resource);
//			}
//			request2 = request2.toBuilder().continuationToken(result.nextContinuationToken()).build();
//		} while (result.isTruncated());

	}

}


