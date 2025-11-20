package com.sprint.mission.discodeit;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

class AWSS3Test {

    private S3Client s3Client;
    private S3Presigner s3Presigner;
    private String bucketName;
    private Region region;

}