#!/bin/bash
set -e

IMAGE_NAME=przx27/backend-app:java21

# Login (si no estás ya logueado)
docker login 

# Subir la imagen
docker push $IMAGE_NAME
