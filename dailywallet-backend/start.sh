#!/bin/bash

echo "========================================"
echo "DailyWallet Backend - Starting..."
echo "========================================"
echo ""

# Check if .env exists
if [ ! -f .env ]; then
    echo "ERROR: .env file not found!"
    echo "Please create .env file with your configuration."
    exit 1
fi

echo "Loading environment variables from .env..."
echo ""

# Start with Docker
echo "Starting backend with Docker..."
docker-compose up -d

echo ""
echo "========================================"
echo "Backend started successfully!"
echo "========================================"
echo ""
echo "API URL: http://localhost:8080"
echo "Health Check: http://localhost:8080/actuator/health"
echo ""
echo "View logs: docker-compose logs -f backend"
echo "Stop backend: docker-compose down"
echo ""
