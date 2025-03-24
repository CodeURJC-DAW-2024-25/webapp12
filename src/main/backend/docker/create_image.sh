#!/bin/bash
set -e

# Go to the project root directory (where pom.xml is located)
cd "$(dirname "$0")"/..

# Build the Docker image using the Dockerfile located in /docker
docker build -f docker/Dockerfile -t przx27/backend-app:java21 .

