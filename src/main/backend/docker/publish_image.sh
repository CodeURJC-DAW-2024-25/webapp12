#!/bin/bash
set -e

IMAGE_NAME=przx27/backend-app:java21

# Login si es necesario
docker login

# Subir imagen
docker push $IMAGE_NAME
