# Script PowerShell para executar o Comprae Produto Service

param(
    [Parameter(Position=0)]
    [string]$Comando = "help"
)

Write-Host "Comprae Produto Service - Script de Execucao" -ForegroundColor Cyan
Write-Host "=============================================" -ForegroundColor Cyan

function Show-Help {
    Write-Host "Uso: .\run.ps1 [COMANDO]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Comandos disponiveis:" -ForegroundColor Green
    Write-Host "  dev       - Executar em modo de desenvolvimento (H2)" -ForegroundColor White
    Write-Host "  docker    - Executar com Docker Compose (PostgreSQL)" -ForegroundColor White
    Write-Host "  test      - Executar testes" -ForegroundColor White
    Write-Host "  build     - Compilar a aplicacao" -ForegroundColor White
    Write-Host "  clean     - Limpar arquivos compilados" -ForegroundColor White
    Write-Host "  help      - Mostrar esta ajuda" -ForegroundColor White
    Write-Host ""
}

function Start-Dev {
    Write-Host "Executando em modo de desenvolvimento..." -ForegroundColor Green
    Write-Host "   - Banco H2 em memoria" -ForegroundColor Gray
    Write-Host "   - Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
    Write-Host "   - H2 Console: http://localhost:8080/h2-console" -ForegroundColor Gray
    Write-Host ""
    
    mvn spring-boot:run -D"spring-boot.run.profiles=dev"
}

function Start-Docker {
    Write-Host "Executando com Docker Compose..." -ForegroundColor Green
    Write-Host "   - PostgreSQL em container" -ForegroundColor Gray
    Write-Host "   - API: http://localhost:8080/api/v1/produtos" -ForegroundColor Gray
    Write-Host "   - Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
    Write-Host ""
    
    # Verificar se o JAR existe
    if (-not (Test-Path "target\comprae-produto-service-1.0.0.jar")) {
        Write-Host "Compilando aplicacao..." -ForegroundColor Yellow
        mvn clean package -DskipTests
    }

    docker-compose -f ../docker-compose.yml up -d
    Write-Host "Servicos iniciados!" -ForegroundColor Green
}

function Start-Tests {
    Write-Host "Executando testes..." -ForegroundColor Green
    mvn test
}

function Start-Build {
    Write-Host "Compilando aplicacao..." -ForegroundColor Green
    mvn clean package -DskipTests
    Write-Host "Compilacao concluida!" -ForegroundColor Green
}

function Start-Clean {
    Write-Host "Limpando arquivos compilados..." -ForegroundColor Green
    mvn clean
    Write-Host "Limpeza concluida!" -ForegroundColor Green
}

# Processar comando
switch ($Comando.ToLower()) {
    "dev" {
        Start-Dev
    }
    "docker" {
        Start-Docker
    }
    "test" {
        Start-Tests
    }
    "build" {
        Start-Build
    }
    "clean" {
        Start-Clean
    }
    "help" {
        Show-Help
    }
    default {
        Write-Host "Comando desconhecido: $Comando" -ForegroundColor Red
        Write-Host ""
        Show-Help
        exit 1
    }
}
