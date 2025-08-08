# ======================================================================
# SCRIPT PARA POPULAR CONFIGURAÇÕES DO PRODUTO SERVICE NO CONFIG SERVER
# ======================================================================

# Este script pode ser executado via cURL ou ferramenta similar
# Base URL do Config Server
CONFIG_SERVER_URL="http://localhost:8888"
NAMESPACE="comprae-produto-service"
ENVIRONMENT="dev"

echo "Populando configurações do Produto Service no Config Server..."

# Configurações do Banco de Dados
curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "database.pool.size",
    "value": "20",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Tamanho do pool de conexões do banco de dados"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "database.timeout",
    "value": "30000",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Timeout de conexão do banco de dados em ms"
  }'

# Configurações de Performance
curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "cache.produto.ttl",
    "value": "300",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "TTL do cache de produtos em segundos"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "batch.size",
    "value": "50",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Tamanho do batch para operações em lote"
  }'

# Configurações de Negócio
curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "produto.estoque.minimo",
    "value": "5",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Estoque mínimo para alertas"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "produto.categoria.ativa",
    "value": "true",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Se o sistema de categorias está ativo"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "produto.preco.maximo",
    "value": "999999.99",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Preço máximo permitido para produtos"
  }'

# Feature Flags
curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "feature.busca.avancada",
    "value": "true",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Habilita busca avançada de produtos"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "feature.recomendacao",
    "value": "false",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Habilita sistema de recomendações"
  }'

curl -X POST "$CONFIG_SERVER_URL/api/configs" \
  -H "Content-Type: application/json" \
  -H "Authorization: Basic YWRtaW46YWRtaW4xMjM=" \
  -d '{
    "key": "feature.desconto.automatico",
    "value": "false",
    "namespace": "'$NAMESPACE'",
    "environment": "'$ENVIRONMENT'",
    "description": "Habilita descontos automáticos"
  }'

echo "Configurações do Produto Service criadas com sucesso!"
echo ""
echo "Para verificar as configurações criadas, acesse:"
echo "$CONFIG_SERVER_URL/api/configs/$NAMESPACE/$ENVIRONMENT"
