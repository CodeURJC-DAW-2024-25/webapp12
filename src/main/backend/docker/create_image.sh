#!/bin/bash
set -e

# Ir al directorio donde está el script
cd "$(dirname "$0")"

# Subimos 1 nivel: desde /docker --> /backend
cd ..

# Confirmamos que estamos en el lugar correcto
if [[ ! -f "pom.xml" ]] || [[ ! -d "src" ]]; then
  echo "🚨 ERROR: No se encontró pom.xml o src/ en el directorio actual. ¿Estás en src/main/backend?"
  exit 1
fi

echo "➡️ Construyendo imagen Docker przx27/backend-app:java21..."

# Lanzar el build (Dockerfile sigue en docker/Dockerfile)
docker build -f docker/Dockerfile -t przx27/backend-app:java21 .

echo "✅ Imagen creada correctamente: przx27/backend-app:java21"
