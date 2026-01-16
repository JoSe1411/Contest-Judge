package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;

@Service
public class S3ManagerService {
    private final String bucketName;
    private final Logger logger;
    private final S3Client s3Client;

    public S3ManagerService(@Value("${aws.s3.bucket.name}") String bucketName, S3Client s3Client) {
        this.bucketName = bucketName;
        this.logger = LoggerFactory.getLogger(S3ManagerService.class);
        this.s3Client = s3Client;

        if (this.bucketName == null || this.bucketName.trim().isEmpty()) {
            throw new IllegalStateException("AWS S3 bucket name is required");
        }
    }

    public String getFileExtension(String language) {
        String extension = switch (language.toLowerCase()) {
            case "c++", "cpp" -> "cpp";
            case "python", "py" -> "py";
            case "java" -> "java";
            default -> null;
        };
        if (extension == null) {
            logger.error("Unsupported language: {}", language);
            throw new IllegalArgumentException("Language not supported: " + language);
        }
        return extension;
    }

    public void uploadCode(String s3Key, String code) {
        try {
            if (s3Key == null || s3Key.trim().isEmpty()) {
                throw new IllegalArgumentException("S3 key cannot be null or empty");
            }
            if (code == null || code.trim().isEmpty()) {
                throw new IllegalArgumentException("Code cannot be null or empty");
            }

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType("text/plain")
                    .acl(ObjectCannedACL.PRIVATE)
                    .build();

            s3Client.putObject(request, RequestBody.fromString(code));
            logger.info("Successfully uploaded code to S3: {}", s3Key);

        } catch (Exception e) {
            logger.error("Failed to upload code to S3 for key: {}", s3Key, e);
            throw new RuntimeException("S3 upload failed: " + e.getMessage(), e);
        }
    }
}