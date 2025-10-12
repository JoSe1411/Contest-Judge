# Multi-stage build for code execution environment
FROM ubuntu:22.04

# Avoid prompts from apt
ENV DEBIAN_FRONTEND=noninteractive

# Install essential build tools and compilers for Java, C++, and Python
RUN apt-get update && apt-get install -y \
    # Java Development Kit and Runtime
    openjdk-17-jdk \
    openjdk-17-jre \
    # C/C++ compilers
    gcc \
    g++ \
    # Python
    python3 \
    python3-pip \
    # Development utilities
    curl \
    wget \
    unzip \
    # Text processing tools
    jq \
    # AWS CLI for S3 integration
    && rm -rf /var/lib/apt/lists/*

# Install AWS CLI v2 for S3 operations
RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
    && unzip awscliv2.zip \
    && ./aws/install \
    && rm -rf awscliv2.zip aws/

# Install Python packages for code analysis and testing
RUN pip3 install \
    boto3 \
    pytest

# Create a non-root user for security
RUN useradd -m -s /bin/bash judgeuser \
    && mkdir -p /home/judgeuser/workspace \
    && chown -R judgeuser:judgeuser /home/judgeuser

# Set up workspace directory with proper permissions
WORKDIR /home/judgeuser/workspace
RUN chmod 755 /home/judgeuser/workspace

# Create necessary files beforehand as specified
RUN touch output.txt input.txt && chmod 666 output.txt input.txt

# Copy execution script
COPY docker/contest_judge_downloader.py /home/judgeuser/workspace/
RUN chmod +x /home/judgeuser/workspace/contest_judge_downloader.py

# Switch to non-root user
USER judgeuser

# Set environment variables
ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin:/usr/local/aws-cli/v2/current/bin
ENV AWS_DEFAULT_REGION=ap-south-1

# Set the default command to run the judge script
CMD ["python3", "/home/judgeuser/workspace/contest_judge_downloader.py"]
