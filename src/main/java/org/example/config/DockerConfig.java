package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class DockerConfig{
    private final Logger logger;
    public DockerConfig(){
        this.logger = LoggerFactory.getLogger(DockerConfig.class);
    }

    @Bean
    public DockerClient createDockerClient() {
        
        // Docker Desktop daemon being used for better visibility.
        DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
            .withDockerHost("unix:///home/loaspoas/.docker/desktop/docker.sock")
            .build();
        logger.info("Docker host - "+config.getDockerHost());
        ApacheDockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
            .dockerHost(config.getDockerHost())
            .build();

        return DockerClientImpl.getInstance(config, httpClient);
    }

}
