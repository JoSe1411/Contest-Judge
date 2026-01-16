package org.example.service;

import java.util.UUID;
import org.example.dto.SubmissionRequest;
import org.example.dto.SubmissionResponse;
import org.example.dto.SubmissionMessage;
import org.example.entity.Submission;
import org.example.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);

    private final S3ManagerService s3Manager;
    private final SubmissionRepository submissionRepository;
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    public SubmissionService(
            S3ManagerService s3Manager,
            SubmissionRepository submissionRepository,
            RabbitTemplate rabbitTemplate) {
        this.s3Manager = s3Manager;
        this.submissionRepository = submissionRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    public SubmissionResponse submitCode(SubmissionRequest request) {
        // Extracting information from request
        String userId = request.getUserId();
        String questionId = request.getQuestionId();
        String language = request.getLanguage();
        String code = request.getCode();
        String extension = s3Manager.getFileExtension(language);
        String s3Key = String.format("Questions/%s/%s/%s-(Main.%s)",
                questionId, language, userId, extension);
        s3Manager.uploadCode(s3Key, code);
        logger.info("Code uploaded to S3: {}", s3Key);
        // Creating the submission for the DB.
        Submission submission = new Submission();
        submission.setUserId(userId);
        submission.setQuestionId(questionId);
        submission.setLanguage(language);
        submission.setS3Key(s3Key);
        // Saving to DB.
        Submission savedSubmission = submissionRepository.save(submission);
        UUID submissionId = savedSubmission.getSubmissionId();
        logger.info("Submission saved to DB with ID: {}", submissionId);
        // Creating the message to publish in RabbitMQ.
        SubmissionMessage message = new SubmissionMessage();
        message.setSubmissionId(submissionId);
        message.setUserId(userId);
        message.setQuestionId(questionId);
        message.setLanguage(language);
        message.setS3Key(s3Key);
        rabbitTemplate.convertAndSend(exchangeName, routingKey, message);
        logger.info("Message published to RabbitMQ for submission: {}", submissionId);
        // Creating the HTTP response.
        SubmissionResponse response = new SubmissionResponse();
        response.setSubmissionId(submissionId);
        response.setStatus("QUEUED");
        response.setCreatedAt(savedSubmission.getCreatedAt());

        return response;
    }
}