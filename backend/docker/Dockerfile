
FROM ubuntu:22.04


ENV DEBIAN_FRONTEND=noninteractive


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


RUN curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" \
    && unzip awscliv2.zip \
    && ./aws/install \
    && rm -rf awscliv2.zip aws/


RUN pip3 install \
    boto3 \
    pytest


RUN useradd -m -s /bin/bash judgeuser \
    && mkdir -p /home/judgeuser/workspace \
    && chown -R judgeuser:judgeuser /home/judgeuser


WORKDIR /home/judgeuser/workspace
RUN chmod 755 /home/judgeuser/workspace


RUN touch output.txt input.txt && chmod 666 output.txt input.txt


COPY contest_judge_downloader.py /home/judgeuser/workspace/
RUN chmod +x /home/judgeuser/workspace/contest_judge_downloader.py


USER judgeuser


ENV JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin:/usr/local/aws-cli/v2/current/bin
ENV AWS_DEFAULT_REGION=ap-south-1

CMD ["python3", "/home/judgeuser/workspace/contest_judge_downloader.py"]
