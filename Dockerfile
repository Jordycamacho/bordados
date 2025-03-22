FROM openjdk:17-jdk-slim
ARG JAR_FILE=target/bordados-0.0.1.jar
COPY ${JAR_FILE} app_gast.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app_gast.jar"]