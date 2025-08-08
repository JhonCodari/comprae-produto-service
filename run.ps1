# Script PowerShell para executar o Compraê Produto Service

param(
    [Parameter(Position=0)]
    [string]$Comando = "help"
)

Write-Host "🛍️  Compraê Produto Service - Script de Execução" -ForegroundColor Cyan
Write-Host "=================================================" -ForegroundColor Cyan

function Mostrar-Ajuda {
    Write-Host "Uso: .\run.ps1 [COMANDO]" -ForegroundColor Yellow
    Write-Host ""
    Write-Host "Comandos disponíveis:" -ForegroundColor Green
    Write-Host "  dev       - Executar em modo de desenvolvimento (H2)" -ForegroundColor White
    Write-Host "  docker    - Executar com Docker Compose (PostgreSQL)" -ForegroundColor White
    Write-Host "  test      - Executar testes" -ForegroundColor White
    Write-Host "  build     - Compilar a aplicação" -ForegroundColor White
    Write-Host "  clean     - Limpar arquivos compilados" -ForegroundColor White
    Write-Host "  help      - Mostrar esta ajuda" -ForegroundColor White
    Write-Host ""
    Write-Host "Exemplos:" -ForegroundColor Green
    Write-Host "  .\run.ps1 dev     # Executa em modo desenvolvimento" -ForegroundColor Gray
    Write-Host "  .\run.ps1 docker  # Executa com Docker" -ForegroundColor Gray
    Write-Host "  .\run.ps1 test    # Executa testes" -ForegroundColor Gray
}

function Executar-Dev {
    Write-Host "🚀 Executando em modo de desenvolvimento..." -ForegroundColor Green
    Write-Host "   - Banco H2 em memória" -ForegroundColor Gray
    Write-Host "   - Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
    Write-Host "   - H2 Console: http://localhost:8080/h2-console" -ForegroundColor Gray
    Write-Host ""
    
    mvn spring-boot:run -D"spring-boot.run.profiles=dev"
}

function Executar-Docker {
    Write-Host "🐳 Executando com Docker Compose..." -ForegroundColor Green
    Write-Host "   - PostgreSQL em container" -ForegroundColor Gray
    Write-Host "   - API: http://localhost:8080/api/v1/produtos" -ForegroundColor Gray
    Write-Host "   - Swagger UI: http://localhost:8080/swagger-ui.html" -ForegroundColor Gray
    Write-Host ""
    
    # Verificar se o JAR existe
    if (-not (Test-Path "target\comprae-produto-service-1.0.0.jar")) {
        Write-Host "📦 Compilando aplicação..." -ForegroundColor Yellow
        mvn clean package -DskipTests
    }
    
    docker-compose up -d
    Write-Host "✅ Serviços iniciados!" -ForegroundColor Green
    Write-Host "   Para visualizar logs: docker-compose logs -f" -ForegroundColor Gray
    Write-Host "   Para parar: docker-compose down" -ForegroundColor Gray
}

function Executar-Testes {
    Write-Host "🧪 Executando testes..." -ForegroundColor Green
    mvn test
}

function Compilar {
    Write-Host "📦 Compilando aplicação..." -ForegroundColor Green
    mvn clean package -DskipTests
    Write-Host "✅ Compilação concluída!" -ForegroundColor Green
}

function Limpar {
    Write-Host "🧹 Limpando arquivos compilados..." -ForegroundColor Green
    mvn clean
    Write-Host "✅ Limpeza concluída!" -ForegroundColor Green
}

# Processar comando
switch ($Comando.ToLower()) {
    "dev" {
        Executar-Dev
    }
    "docker" {
        Executar-Docker
    }
    "test" {
        Executar-Testes
    }
    "build" {
        Compilar
    }
    "clean" {
        Limpar
    }
    "help" {
        Mostrar-Ajuda
    }
    default {
        Write-Host "❌ Comando desconhecido: $Comando" -ForegroundColor Red
        Write-Host ""
        Mostrar-Ajuda
        exit 1
    }
}
