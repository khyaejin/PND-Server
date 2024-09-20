FROM amazoncorretto:17

# Node.js 설치
RUN yum install -y gcc-c++ make \
    && curl -fsSL https://rpm.nodesource.com/setup_16.x | bash - \
    && yum install -y nodejs \
    && yum clean all

# 작업 디렉토리 설정
WORKDIR /PND-Server

# Gradle에서 빌드된 JAR 파일 복사
COPY build/libs/pnd-0.0.1-SNAPSHOT.jar PND-Server.jar

# resources 디렉토리 전체 복사
COPY src/main/resources /PND-Server/src/main/resources

# Node.js 의존성 설치
WORKDIR /PND-Server/src/main/resources/scripts/3d-contrib
RUN npm install

# ts-node 설치
RUN npm install -g ts-node

# 로컬 시간대 설정
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime

# 컨테이너 시작 시 JAR 파일 실행
ENTRYPOINT ["java", "-jar", "/PND-Server/PND-Server.jar"]

EXPOSE 8080
