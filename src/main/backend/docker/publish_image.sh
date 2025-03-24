#!/bin/bash
set -e

IMAGE_NAME=przx27/backend-app:java21


docker login

# push image to docker hub
docker push $IMAGE_NAME
