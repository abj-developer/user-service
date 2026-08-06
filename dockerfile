# ==========================
# Stage 1 - Build
# ==========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy Maven wrapper
COPY .mvn .mvn
COPY mvnw .
COPY pom.xml .

# Download dependencies
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# Copy source
COPY src src

# Build
RUN ./mvnw clean package -DskipTests

# ===========================
# Stage 2 - Runtime
# ===========================
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 9002

ENTRYPOINT ["java","-jar","app.jar"]