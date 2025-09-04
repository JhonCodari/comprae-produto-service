# ======================================================================
# SCRIPT DE INICIALIZAÇÃO INTEGRADA DO COMPRAÊ PRODUTO SERVICE
# ======================================================================

$ErrorActionPreference = "Stop"

Write-Host "🚀 Iniciando integração completa do Compraê Produto Service..." -ForegroundColor Green
Write-Host ""

# Função para verificar se um serviço está rodando
function Test-Service {
    param(
        [string]$Url,
        [string]$ServiceName,
        [int]$MaxAttempts = 30
    )
    
    Write-Host "Aguardando $ServiceName estar disponível..." -ForegroundColor Yellow
    
    for ($attempt = 1; $attempt -le $MaxAttempts; $attempt++) {
        try {
            $response = Invoke-WebRequest -Uri $Url -Method Get -TimeoutSec 5 -UseBasicParsing
            if ($response.StatusCode -eq 200) {
                Write-Host "✓ $ServiceName está disponível!" -ForegroundColor Green
                return $true
            }
        }
        catch {
            # Serviço ainda não está disponível
        }
        
        Write-Host "Tentativa $attempt/$MaxAttempts - Aguardando $ServiceName..." -ForegroundColor Yellow
        Start-Sleep -Seconds 3
    }
    
    Write-Host "✗ $ServiceName não ficou disponível após $($MaxAttempts * 3) segundos" -ForegroundColor Red
    return $false
}

# 1. Verificar se estamos no diretório correto
if (-not (Test-Path "pom.xml") -or -not (Test-Path "src")) {
    Write-Host "❌ Execute este script no diretório raiz do comprae-produto-service" -ForegroundColor Red
    exit 1
}

Write-Host "📦 Compilando o projeto..." -ForegroundColor Blue
try {
    mvn clean install -DskipTests -q
    Write-Host "✓ Projeto compilado com sucesso" -ForegroundColor Green
}
catch {
    Write-Host "❌ Falha na compilação do projeto" -ForegroundColor Red
    exit 1
}
Write-Host ""

# 2. Verificar se o Config Server está rodando
Write-Host "🔧 Verificando Config Server..." -ForegroundColor Blue
try {
    $response = Invoke-WebRequest -Uri "http://localhost:8888/actuator/health" -Method Get -TimeoutSec 5 -UseBasicParsing
    Write-Host "✓ Config Server já está rodando" -ForegroundColor Green
}
catch {
    Write-Host "Config Server não está rodando. Iniciando..." -ForegroundColor Yellow
    
    # Verificar se o docker-compose do config server existe
    if (Test-Path "../comprae-config-server") {
        Set-Location "../comprae-config-server"
        docker-compose up -d
        Set-Location "../comprae-produto-service"
        
        # Aguardar o Config Server ficar disponível
        if (-not (Test-Service -Url "http://localhost:8888/actuator/health" -ServiceName "Config Server")) {
            Write-Host "❌ Falha ao iniciar Config Server" -ForegroundColor Red
            exit 1
        }
    }
    else {
        Write-Host "❌ Diretório comprae-config-server não encontrado" -ForegroundColor Red
        Write-Host "Por favor, clone e configure o Config Server primeiro" -ForegroundColor Yellow
        exit 1
    }
}

Write-Host ""

# 3. Popular configurações
Write-Host "📋 Populando configurações no Config Server..." -ForegroundColor Blue
if (Test-Path "scripts/popular-configuracoes.ps1") {
    try {
        & "./scripts/popular-configuracoes.ps1"
        Write-Host "✓ Configurações populadas com sucesso" -ForegroundColor Green
    }
    catch {
        Write-Host "⚠️ Erro ao popular configurações. Continuando..." -ForegroundColor Yellow
    }
}
else {
    Write-Host "⚠️ Script de configurações não encontrado. Continuando..." -ForegroundColor Yellow
}

Write-Host ""

# 4. Verificar se PostgreSQL está disponível (opcional)
Write-Host "🗄️ Verificando PostgreSQL..." -ForegroundColor Blue
try {
    $pgTest = Test-NetConnection -ComputerName "localhost" -Port 5432 -InformationLevel Quiet
    if ($pgTest) {
        Write-Host "✓ PostgreSQL está disponível" -ForegroundColor Green
        Write-Host "Iniciando com perfil Docker (PostgreSQL)" -ForegroundColor Yellow
        $Profile = "docker"
    }
    else {
        throw "PostgreSQL não disponível"
    }
}
catch {
    Write-Host "PostgreSQL não disponível. Usando H2 em memória" -ForegroundColor Yellow
    $Profile = "dev"
}

Write-Host ""

# 5. Iniciar o serviço
Write-Host "🚀 Iniciando Compraê Produto Service..." -ForegroundColor Blue
Write-Host "Perfil ativo: $Profile" -ForegroundColor Yellow
Write-Host "Pressione Ctrl+C para parar" -ForegroundColor Yellow
Write-Host ""

# Aguardar um pouco para o Config Server estar completamente pronto
Start-Sleep -Seconds 3

# Iniciar o serviço
try {
    mvn spring-boot:run "-Dspring-boot.run.profiles=$Profile"
}
finally {
    Write-Host ""
    Write-Host "🎉 Serviço finalizado!" -ForegroundColor Green
    Write-Host ""
    Write-Host "📚 URLs que estavam disponíveis:" -ForegroundColor Blue
    Write-Host "• API Produtos: http://localhost:8082/api/v1/produtos" -ForegroundColor Yellow
    Write-Host "• Configurações: http://localhost:8082/api/v1/configuracoes" -ForegroundColor Yellow
    Write-Host "• Swagger UI: http://localhost:8082/swagger-ui.html" -ForegroundColor Yellow
    Write-Host "• Health Check: http://localhost:8082/actuator/health" -ForegroundColor Yellow
    Write-Host ""
}
