# =========================
# Build stage
# =========================
FROM gradle:8.14.3-jdk21 AS builder

WORKDIR /app

# Copy Gradle files first for better layer caching
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle gradle

# Download dependencies
RUN ./gradlew dependencies --no-daemon || true

# Copy source
COPY src src

# Build the application
RUN ./gradlew clean bootJar --no-daemon

# =========================
# Runtime stage
# =========================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Create non-root user
RUN useradd -ms /bin/bash spring

USER spring

# Copy jar from builder
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]