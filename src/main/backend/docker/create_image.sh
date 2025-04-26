#!/bin/bash
set -e

cd "$(dirname "$0")"

echo "➡️ Construyendo imagen Docker via docker-compose..."

docker-compose build

echo "✅ Imagen construida correctamente."

