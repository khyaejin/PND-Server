FROM amazoncorretto:17
# FROM openjdk:17-jdk

WORKDIR /PND-Server/build/libs
# Gradle에서 빌드된 JAR 파일 복사
COPY build/libs/pnd-0.0.1-SNAPSHOT.jar PND-Server.jar

# ARG JAR_FILE=*.jar
# 기존 : COPY ${JAR_FILE} PND-Server.jar
# COPY build/libs/*.jar PND-Server.jar

ENTRYPOINT ["java", "-jar", "/PND-Server/build/libs/PND-Server.jar"]
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime