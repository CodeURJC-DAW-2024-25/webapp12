#!/bin/bash
set -e

# Nombre de la imagen
IMAGE_NAME=przx27/backend-app:java21

# Navegar a la carpeta del script (docker)
cd "$(dirname "$0")"

# Construir la imagen usando el Dockerfile
docker build -t $IMAGE_NAME .
