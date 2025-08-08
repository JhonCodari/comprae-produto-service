# ======================================================================
# SCRIPT PARA POPULAR CONFIGURAÇÕES DO PRODUTO SERVICE NO CONFIG SERVER
# ======================================================================

# Configurações
$CONFIG_SERVER_URL = "http://localhost:8888"
$NAMESPACE = "comprae-produto-service"
$ENVIRONMENT = "dev"
$AUTH_HEADER = "Basic " + [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes("admin:admin123"))

Write-Host "Populando configurações do Produto Service no Config Server..." -ForegroundColor Green

# Função para criar configuração
function New-Configuration {
    param(
        [string]$Key,
        [string]$Value,
        [string]$Description
    )
    
    $body = @{
        key = $Key
        value = $Value
        namespace = $NAMESPACE
        environment = $ENVIRONMENT
        description = $Description
    } | ConvertTo-Json
    
    try {
        Invoke-RestMethod -Uri "$CONFIG_SERVER_URL/api/configs" -Method POST -Body $body -ContentType "application/json" -Headers @{Authorization = $AUTH_HEADER}
        Write-Host "✓ Configuração criada: $Key = $Value" -ForegroundColor Green
    }
    catch {
        Write-Host "✗ Erro ao criar configuração: $Key - $($_.Exception.Message)" -ForegroundColor Red
    }
}

# Configurações do Banco de Dados
New-Configuration -Key "database.pool.size" -Value "20" -Description "Tamanho do pool de conexões do banco de dados"
New-Configuration -Key "database.timeout" -Value "30000" -Description "Timeout de conexão do banco de dados em ms"

# Configurações de Performance
New-Configuration -Key "cache.produto.ttl" -Value "300" -Description "TTL do cache de produtos em segundos"
New-Configuration -Key "batch.size" -Value "50" -Description "Tamanho do batch para operações em lote"

# Configurações de Negócio
New-Configuration -Key "produto.estoque.minimo" -Value "5" -Description "Estoque mínimo para alertas"
New-Configuration -Key "produto.categoria.ativa" -Value "true" -Description "Se o sistema de categorias está ativo"
New-Configuration -Key "produto.preco.maximo" -Value "999999.99" -Description "Preço máximo permitido para produtos"

# Configurações de API
New-Configuration -Key "api.timeout.externo" -Value "5000" -Description "Timeout para APIs externas em ms"
New-Configuration -Key "api.retry.tentativas" -Value "3" -Description "Número de tentativas para retry"

# Feature Flags
New-Configuration -Key "feature.busca.avancada" -Value "true" -Description "Habilita busca avançada de produtos"
New-Configuration -Key "feature.recomendacao" -Value "false" -Description "Habilita sistema de recomendações"
New-Configuration -Key "feature.desconto.automatico" -Value "false" -Description "Habilita descontos automáticos"

Write-Host ""
Write-Host "Configurações do Produto Service criadas com sucesso!" -ForegroundColor Green
Write-Host ""
Write-Host "Para verificar as configurações criadas, acesse:" -ForegroundColor Yellow
Write-Host "$CONFIG_SERVER_URL/api/configs/$NAMESPACE/$ENVIRONMENT" -ForegroundColor Cyan
