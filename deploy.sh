#!/bin/bash

# Jimudu Wallet Deployment Script
# This script automates the deployment process for the Jimudu Wallet application

set -e  # Exit on any error

echo "🚀 Starting Jimudu Wallet Deployment..."

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "❌ Docker is not installed. Please install Docker first."
    exit 1
fi

# Check if Docker Compose is installed
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose is not installed. Please install Docker Compose first."
    exit 1
fi

# Check if .env file exists
if [ ! -f .env ]; then
    echo "📝 Creating .env file from template..."
    cp .env.example .env
    echo "⚠️  Please edit .env file with your production values before continuing."
    echo "   Required variables: DATABASE_URL, DB_USER, DB_PASSWORD, JWT_SECRET, ALLOWED_ORIGINS"
    read -p "Press Enter after editing .env file..."
fi

# Build and start the application
echo "🔨 Building Docker images..."
docker-compose build

echo "🚀 Starting application..."
docker-compose up -d

# Wait for the application to start
echo "⏳ Waiting for application to start..."
sleep 30

# Health check
echo "🔍 Performing health check..."
if curl -f http://localhost:8080/api/health > /dev/null 2>&1; then
    echo "✅ Application is running successfully!"
    echo "🌐 Frontend: http://localhost:8080"
    echo "🔧 Backend API: http://localhost:8080/api"
    echo "📊 Health Check: http://localhost:8080/api/health"
else
    echo "❌ Health check failed. Please check the logs:"
    docker-compose logs jimudu-app
    exit 1
fi

echo "🎉 Deployment completed successfully!"
echo "📋 Useful commands:"
echo "   View logs: docker-compose logs -f jimudu-app"
echo "   Stop app: docker-compose down"
echo "   Restart app: docker-compose restart jimudu-app"
