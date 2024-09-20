FROM amazoncorretto:17

# Node.js 설치
RUN yum install -y gcc-c++ make \
    && curl -fsSL https://rpm.nodesource.com/setup_16.x | bash - \
    && yum install -y nodejs \
    && yum clean all

WORKDIR /PND-Server/build/libs

# Gradle에서 빌드된 JAR 파일 복사
COPY build/libs/pnd-0.0.1-SNAPSHOT.jar PND-Server.jar

ENTRYPOINT ["java", "-jar", "/PND-Server/build/libs/PND-Server.jar"]
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# ts-node 설치
RUN npm install -g ts-node

EXPOSE 8080
