#!/bin/bash
set -e

# Ir al directorio donde está este script
cd "$(dirname "$0")"

# Desde /docker, subir tres niveles hasta la raíz del proyecto
cd ../../..

# Confirmamos que estamos en la raíz correcta
if [[ ! -f "pom.xml" ]] || [[ ! -d "frontend" ]] || [[ ! -d "src" ]]; then
  echo "🚨 ERROR: No se encontró pom.xml, frontend/ o src/ en el directorio actual. ¿Estás en la raíz correcta?"
  exit 1
fi

echo "➡️ Construyendo imagen Docker przx27/backend-app:java21..."

# Lanzar el build
docker build -f src/main/backend/docker/Dockerfile -t przx27/backend-app:java21 .

echo "✅ Imagen creada correctamente: przx27/backend-app:java21"
