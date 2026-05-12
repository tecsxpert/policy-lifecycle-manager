# Stage 1: Build stage
FROM eclipse-temurin:17-jdk as builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the application
RUN --mount=type=cache,target=/root/.m2 mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built JAR from builder stage
COPY --from=builder /app/target/*.jar app.jar

# Create uploads directory for file uploads
RUN mkdir -p /app/uploads/files

# Expose port 8080 for the Spring Boot application
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=5s --retries=3 \
    CMD java -version

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
