#!/bin/bash
set -e

# Ir a la carpeta raíz del proyecto (donde está pom.xml)
cd "$(dirname "$0")"/..

# Construir la imagen usando el Dockerfile ubicado en /docker
docker build -f docker/Dockerfile -t przx27/backend-app:java21 .
