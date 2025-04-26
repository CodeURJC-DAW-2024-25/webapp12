#!/bin/bash
set -e

# Ir al directorio raíz del proyecto (donde están frontend/, src/, pom.xml)
cd "$(dirname "$0")/../../.."

echo "➡️ Construyendo imagen Docker przx27/backend-app:java21..."

# Lanzar el build usando el Dockerfile que sigue estando en src/main/backend/docker
docker build -f src/main/backend/docker/Dockerfile -t przx27/backend-app:java21 .

echo "✅ Imagen creada correctamente: przx27/backend-app:java21"
