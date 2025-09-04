#!/bin/bash

# ======================================================================
# SCRIPT DE INICIALIZAÇÃO INTEGRADA DO COMPRAÊ PRODUTO SERVICE
# ======================================================================

set -e

echo "🚀 Iniciando integração completa do Compraê Produto Service..."
echo ""

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Função para verificar se um serviço está rodando
check_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=1
    
    echo -e "${YELLOW}Aguardando $service_name estar disponível...${NC}"
    
    while [ $attempt -le $max_attempts ]; do
        if curl -s -f "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}✓ $service_name está disponível!${NC}"
            return 0
        fi
        
        echo -e "${YELLOW}Tentativa $attempt/$max_attempts - Aguardando $service_name...${NC}"
        sleep 3
        attempt=$((attempt + 1))
    done
    
    echo -e "${RED}✗ $service_name não ficou disponível após $((max_attempts * 3)) segundos${NC}"
    return 1
}

# 1. Verificar se estamos no diretório correto
if [ ! -f "pom.xml" ] || [ ! -d "src" ]; then
    echo -e "${RED}❌ Execute este script no diretório raiz do comprae-produto-service${NC}"
    exit 1
fi

echo -e "${BLUE}📦 Compilando o projeto...${NC}"
mvn clean install -DskipTests -q
echo -e "${GREEN}✓ Projeto compilado com sucesso${NC}"
echo ""

# 2. Verificar se o Config Server está rodando
echo -e "${BLUE}🔧 Verificando Config Server...${NC}"
if ! curl -s -f "http://localhost:8888/actuator/health" > /dev/null 2>&1; then
    echo -e "${YELLOW}Config Server não está rodando. Iniciando...${NC}"
    
    # Verificar se o docker-compose do config server existe
    if [ -d "../comprae-config-server" ]; then
        cd ../comprae-config-server
        docker-compose up -d
        cd ../comprae-produto-service
        
        # Aguardar o Config Server ficar disponível
        if ! check_service "http://localhost:8888/actuator/health" "Config Server"; then
            echo -e "${RED}❌ Falha ao iniciar Config Server${NC}"
            exit 1
        fi
    else
        echo -e "${RED}❌ Diretório comprae-config-server não encontrado${NC}"
        echo -e "${YELLOW}Por favor, clone e configure o Config Server primeiro${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Config Server já está rodando${NC}"
fi

echo ""

# 3. Popular configurações
echo -e "${BLUE}📋 Populando configurações no Config Server...${NC}"
if [ -f "scripts/popular-configuracoes.sh" ]; then
    chmod +x scripts/popular-configuracoes.sh
    ./scripts/popular-configuracoes.sh
    echo -e "${GREEN}✓ Configurações populadas com sucesso${NC}"
else
    echo -e "${YELLOW}⚠️ Script de configurações não encontrado. Continuando...${NC}"
fi

echo ""

# 4. Verificar se PostgreSQL está disponível (opcional)
echo -e "${BLUE}🗄️ Verificando PostgreSQL...${NC}"
if curl -s -f "http://localhost:5432" > /dev/null 2>&1; then
    echo -e "${GREEN}✓ PostgreSQL está disponível${NC}"
    echo -e "${YELLOW}Iniciando com perfil Docker (PostgreSQL)${NC}"
    PROFILE="docker"
else
    echo -e "${YELLOW}PostgreSQL não disponível. Usando H2 em memória${NC}"
    PROFILE="dev"
fi

echo ""

# 5. Iniciar o serviço
echo -e "${BLUE}🚀 Iniciando Compraê Produto Service...${NC}"
echo -e "${YELLOW}Perfil ativo: $PROFILE${NC}"
echo -e "${YELLOW}Pressione Ctrl+C para parar${NC}"
echo ""

# Aguardar um pouco para o Config Server estar completamente pronto
sleep 3

# Iniciar o serviço
mvn spring-boot:run -Dspring-boot.run.profiles=$PROFILE

echo ""
echo -e "${GREEN}🎉 Serviço iniciado com sucesso!${NC}"
echo ""
echo -e "${BLUE}📚 URLs disponíveis:${NC}"
echo -e "${YELLOW}• API Produtos: http://localhost:8082/api/v1/produtos${NC}"
echo -e "${YELLOW}• Configurações: http://localhost:8082/api/v1/configuracoes${NC}"
echo -e "${YELLOW}• Swagger UI: http://localhost:8082/swagger-ui.html${NC}"
echo -e "${YELLOW}• Health Check: http://localhost:8082/actuator/health${NC}"
echo ""
