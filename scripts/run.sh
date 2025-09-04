#!/bin/bash

# Script para executar o Compraê Produto Service

set -e

echo "🛍️  Compraê Produto Service - Script de Execução"
echo "================================================="

# Função para mostrar ajuda
mostrar_ajuda() {
    echo "Uso: $0 [COMANDO]"
    echo ""
    echo "Comandos disponíveis:"
    echo "  dev       - Executar em modo de desenvolvimento (H2)"
    echo "  docker    - Executar com Docker Compose (PostgreSQL)"
    echo "  test      - Executar testes"
    echo "  build     - Compilar a aplicação"
    echo "  clean     - Limpar arquivos compilados"
    echo "  help      - Mostrar esta ajuda"
    echo ""
    echo "Exemplos:"
    echo "  $0 dev     # Executa em modo desenvolvimento"
    echo "  $0 docker  # Executa com Docker"
    echo "  $0 test    # Executa testes"
}

# Função para executar em desenvolvimento
executar_dev() {
    echo "🚀 Executando em modo de desenvolvimento..."
    echo "   - Banco H2 em memória"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo "   - H2 Console: http://localhost:8080/h2-console"
    echo ""
    mvn spring-boot:run -Dspring-boot.run.profiles=dev
}

# Função para executar com Docker
executar_docker() {
    echo "🐳 Executando com Docker Compose..."
    echo "   - PostgreSQL em container"
    echo "   - API: http://localhost:8080/api/v1/produtos"
    echo "   - Swagger UI: http://localhost:8080/swagger-ui.html"
    echo ""
    
    # Compilar se necessário
    if [ ! -f "target/comprae-produto-service-1.0.0.jar" ]; then
        echo "📦 Compilando aplicação..."
        mvn clean package -DskipTests
    fi
    
    docker-compose up -d
    echo "✅ Serviços iniciados!"
    echo "   Para visualizar logs: docker-compose logs -f"
    echo "   Para parar: docker-compose down"
}

# Função para executar testes
executar_testes() {
    echo "🧪 Executando testes..."
    mvn test
}

# Função para compilar
compilar() {
    echo "📦 Compilando aplicação..."
    mvn clean package -DskipTests
    echo "✅ Compilação concluída!"
}

# Função para limpar
limpar() {
    echo "🧹 Limpando arquivos compilados..."
    mvn clean
    echo "✅ Limpeza concluída!"
}

# Processar argumentos
case "${1:-help}" in
    dev)
        executar_dev
        ;;
    docker)
        executar_docker
        ;;
    test)
        executar_testes
        ;;
    build)
        compilar
        ;;
    clean)
        limpar
        ;;
    help|--help|-h)
        mostrar_ajuda
        ;;
    *)
        echo "❌ Comando desconhecido: $1"
        echo ""
        mostrar_ajuda
        exit 1
        ;;
esac
