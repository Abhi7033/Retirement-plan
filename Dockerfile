# docker build -t blk-hacking-ind-abhishek-anand .
# Using Eclipse Temurin JDK 17 on Alpine Linux for a lightweight, production-ready image.
# Alpine chosen for its minimal footprint (~5MB base), reduced attack surface,
# and faster container startup - ideal for microservice deployments.
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven wrapper and pom first for dependency caching
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Download dependencies separately to leverage Docker layer caching
RUN ./mvnw dependency:resolve -q 2>/dev/null || true

# Copy source code and build
COPY src ./src
RUN ./mvnw clean package -q -DskipTests

# --- Runtime stage ---
# Using JRE-only Alpine image for smaller final image size
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/retirement-plan-1.0.0.jar app.jar

# Expose the required port
EXPOSE 5477

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
