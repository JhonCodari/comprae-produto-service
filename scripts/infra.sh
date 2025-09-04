#!/bin/bash
# Script auxiliar para infraestrutura do Compraê Produto Service (Linux/Mac)
ACTION="${1:-up}"

function check_docker() {
    if ! command -v docker &>/dev/null || ! command -v docker-compose &>/dev/null; then
        echo "[ERROR] Docker ou Docker Compose não encontrados"; exit 1
    fi
    echo "[SUCCESS] Docker e Docker Compose encontrados"
}

function infra_up() {
    echo "[INFO] Subindo infraestrutura (PostgreSQL, Redis, Kafka, Zookeeper)..."
    docker-compose -f ../docker-compose.yml up -d postgres redis kafka zookeeper
    echo "[INFO] Aguardando infraestrutura ficar pronta..."
    sleep 15
    echo "[SUCCESS] Infraestrutura pronta!"
}

function infra_down() {
    echo "[INFO] Parando containers de infraestrutura..."
    docker-compose -f ../docker-compose.yml down
    echo "[SUCCESS] Infraestrutura parada."
}

function infra_status() {
    echo "[INFO] Status da infraestrutura:"
    docker-compose -f ../docker-compose.yml ps
}

check_docker

case "$ACTION" in
    up) infra_up ;;
    down) infra_down ;;
    status) infra_status ;;
    *) echo "Ação inválida. Use: up, down ou status."; exit 1 ;;
esac
