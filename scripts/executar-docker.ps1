# Script simplificado para executar o Produto Service via Docker
Write-Host "🚀 Comprae Produto Service - Docker" -ForegroundColor Green

# Verificar Docker
Write-Host "Verificando Docker..." -ForegroundColor Cyan
docker --version
if ($LASTEXITCODE -ne 0) {
    Write-Host "Docker nao encontrado!" -ForegroundColor Red
    exit 1
}

# Parar containers existentes
Write-Host "Parando containers existentes..." -ForegroundColor Yellow
docker-compose -f ../docker-compose.yml down 2>$null

# Iniciar containers
Write-Host "Iniciando containers..." -ForegroundColor Cyan
docker-compose -f ../docker-compose.yml up -d

# Aguardar aplicacao
Write-Host "Aguardando aplicacao..." -ForegroundColor Yellow
$timeout = 120
$elapsed = 0

while ($elapsed -lt $timeout) {
    Start-Sleep -Seconds 3
    $elapsed += 3
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:8080/actuator/health" -UseBasicParsing -TimeoutSec 5 -ErrorAction SilentlyContinue
        if ($response.StatusCode -eq 200) {
            Write-Host ""
            Write-Host "Sucesso! Produto Service funcionando!" -ForegroundColor Green
            Write-Host "Health: http://localhost:8080/actuator/health" -ForegroundColor Cyan
            Write-Host "Swagger: http://localhost:8080/swagger-ui.html" -ForegroundColor Cyan
            Write-Host ""
            docker-compose -f ../docker-compose.yml ps
            exit 0
        }
    } catch {}
    Write-Host "." -NoNewline
}

Write-Host ""
Write-Host "Aplicacao demorou para responder. Verificando logs..." -ForegroundColor Yellow
docker-compose -f ../docker-compose.yml logs comprae-produto-service
