# Script para ambiente de desenvolvimento local (infraestrutura + instrução para rodar app localmente)
Write-Host "🛑 Parando containers existentes..." -ForegroundColor Yellow
& "${PSScriptRoot}\infra.ps1" -Action down

Write-Host "🏗️ Iniciando infraestrutura (PostgreSQL, Redis, Kafka)..." -ForegroundColor Cyan
& "${PSScriptRoot}\infra.ps1" -Action up

Write-Host ""
Write-Host "🚀 Infraestrutura pronta! Agora execute a aplicação:" -ForegroundColor Green
Write-Host ""
Write-Host "cd .." -ForegroundColor White
Write-Host ".\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "📊 Será disponível em:" -ForegroundColor Cyan
Write-Host "   • http://localhost:8080/actuator/health" -ForegroundColor Gray
Write-Host "   • http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
Write-Host ""
Write-Host "🛑 Para parar infraestrutura: .\scripts\infra.ps1 -Action down" -ForegroundColor Yellow
