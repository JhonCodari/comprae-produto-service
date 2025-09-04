#!/bin/bash
# Script simplificado para executar o Produto Service via Docker
set -e
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Verificar Docker
if ! command -v docker &>/dev/null; then
    echo "Docker não encontrado!"; exit 1
fi

# Parar containers existentes
echo "Parando containers existentes..."
docker-compose -f ../docker-compose.yml down || true

# Iniciar containers
echo "Iniciando containers..."
docker-compose -f ../docker-compose.yml up -d

# Aguardar aplicação
echo "Aguardando aplicação..."
TIMEOUT=120
ELAPSED=0
until curl -s http://localhost:8080/actuator/health | grep 'UP' >/dev/null; do
    sleep 3
    ELAPSED=$((ELAPSED+3))
    if [ $ELAPSED -ge $TIMEOUT ]; then
        echo "Aplicação demorou para responder. Verificando logs..."
        docker-compose -f ../docker-compose.yml logs comprae-produto-service
        exit 1
    fi
    echo -n "."
done

echo "\nSucesso! Produto Service funcionando!"
echo "Health: http://localhost:8080/actuator/health"
echo "Swagger: http://localhost:8080/swagger-ui.html"
docker-compose -f ../docker-compose.yml ps
