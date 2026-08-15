# Multi-stage build: build with JDK and Gradle Wrapper, run with JRE
FROM eclipse-temurin:21-jdk as builder
WORKDIR /home/app
COPY . /home/app
# Ensure wrapper is executable and build jar (skip tests for faster image build)
RUN chmod +x ./gradlew || true
RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /home/app/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
