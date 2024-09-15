FROM amazoncorretto:17
# FROM openjdk:17-jdk

WORKDIR /PND-Server/build/libs
ARG JAR_FILE=*.jar
COPY ${JAR_FILE} PND-Server.jar
# COPY build/libs/*.jar PND-Server.jar

ENTRYPOINT ["java", "-jar", "/PND-Server/build/libs/PND-Server.jar"]
RUN ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
