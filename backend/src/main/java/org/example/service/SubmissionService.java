package org.example.service;

import java.util.UUID;
import org.example.dto.SubmissionRequest;
import org.example.dto.SubmissionResponse;
import org.example.dto.SubmissionMessage;
import org.example.dto.RunCompareRequest;
import org.example.dto.ExecutionResult;
import org.example.entity.Submission;
import org.example.repository.SubmissionRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class SubmissionService {
    private static final Logger logger = LoggerFactory.getLogger(SubmissionService.class);
    private final S3ManagerService s3Manager;
    private final SubmissionRepository submissionRepository;
    private final RabbitTemplate rabbitTemplate;
    private final DockerService dockerService;

    @Value("${spring.rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${spring.rabbitmq.routing.key}")
    private String routingKey;

    public SubmissionService(
            S3ManagerService s3Manager,
            SubmissionRepository submissionRepository,
            RabbitTemplate rabbitTemplate,
            DockerService dockerService) {
        this.s3Manager = s3Manager;
        this.submissionRepository = submissionRepository;
        this.rabbitTemplate = rabbitTemplate;
        this.dockerService = dockerService;
    }

    @Transactional
    public SubmissionResponse submitCode(SubmissionRequest request) {
        // Extracting information from request
        String userId = request.getUserId();
        String questionId = request.getQuestionId();
        String language = request.getLanguage();
        String code = request.getCode();
        String extension = s3Manager.getFileExtension(language);
        // Creating the submission for the DB.
        Submission submission = new Submission();
        UUID submissionId = UUID.randomUUID();
        String s3Key = String.format("Questions/%s/submissions/%s/Main.%s",
                questionId, submissionId, extension);
        // Uploading the code to S3.
        s3Manager.uploadCode(s3Key, code);
        logger.info("Code uploaded to S3: {}", s3Key);
        submission.setSubmissionId(submissionId);
        submission.setUserId(userId);
        submission.setQuestionId(questionId);
        submission.setLanguage(language);
        submission.setS3Key(s3Key);
        // Saving to DB.
        submission = submissionRepository.save(submission);

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
        response.setCreatedAt(submission.getCreatedAt());

        return response;
    }

    public Submission getSubmission(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Submission not found with ID: " + id));
    }

    public ExecutionResult runCompare(RunCompareRequest request) {
        String userId = request.getUserId();
        String questionId = request.getQuestionId();
        String language = request.getLanguage();
        String code = request.getCode();
        String input = request.getInput();
        String extension = s3Manager.getFileExtension(language);

        UUID runId = UUID.randomUUID();
        String s3Key = String.format("Runs/%s/%s/Main.%s", userId, runId, extension);

        s3Manager.uploadCode(s3Key, code);
        logger.info("Run code uploaded to S3: {}", s3Key);

        return dockerService.createAndRunJudgeContainer(userId, questionId, language, s3Key, input);
    }
}