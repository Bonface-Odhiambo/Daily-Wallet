# Frontend Dockerfile
FROM node:18-alpine as frontend-builder

WORKDIR /app/frontend
COPY package*.json ./
RUN npm ci --only=production

COPY . .
RUN npm run build

# Backend Dockerfile
FROM openjdk:17-jdk-slim as backend-builder

WORKDIR /app/backend
COPY dailywallet-backend/pom.xml .
COPY dailywallet-backend/src ./src
COPY dailywallet-backend/.env ./

# Build the backend
RUN apt-get update && apt-get install -y maven && \
    mvn clean package -DskipTests && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Production Runtime
FROM openjdk:17-jre-slim

WORKDIR /app

# Install necessary packages
RUN apt-get update && apt-get install -y curl && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

# Copy backend jar
COPY --from=backend-builder /app/backend/target/*.jar app.jar

# Copy frontend build
COPY --from=frontend-builder /app/frontend/dist ./static

# Create logs directory
RUN mkdir -p logs

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/api/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
