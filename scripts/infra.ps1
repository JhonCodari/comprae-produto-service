# Script auxiliar para infraestrutura do Compraê Produto Service
param(
    [ValidateSet("up", "down", "status")][string]$Action = "up"
)

function Test-Docker {
    try {
        $dockerVersion = docker --version 2>$null
        $composeVersion = docker-compose --version 2>$null
        if ($dockerVersion -and $composeVersion) {
            Write-Host "[SUCCESS] Docker e Docker Compose encontrados" -ForegroundColor Green
            return $true
        } else {
            Write-Host "[ERROR] Docker ou Docker Compose não encontrados" -ForegroundColor Red
            return $false
        }
    } catch {
        Write-Host "[ERROR] Erro ao verificar Docker: $_" -ForegroundColor Red
        return $false
    }
}

function Infra-Up {
    Write-Host "[INFO] Subindo infraestrutura (PostgreSQL, Redis, Kafka, Zookeeper)..." -ForegroundColor Cyan
    docker-compose -f ../docker-compose.yml up -d postgres redis kafka zookeeper
    Write-Host "[INFO] Aguardando infraestrutura ficar pronta..." -ForegroundColor Yellow
    Start-Sleep -Seconds 15
    Write-Host "[SUCCESS] Infraestrutura pronta!" -ForegroundColor Green
}

function Infra-Down {
    Write-Host "[INFO] Parando containers de infraestrutura..." -ForegroundColor Yellow
    docker-compose -f ../docker-compose.yml down 2>$null
    Write-Host "[SUCCESS] Infraestrutura parada." -ForegroundColor Green
}

function Infra-Status {
    Write-Host "[INFO] Status da infraestrutura:" -ForegroundColor Cyan
    docker-compose -f ../docker-compose.yml ps
}

if (-not (Test-Docker)) { exit 1 }

switch ($Action) {
    "up"    { Infra-Up }
    "down"  { Infra-Down }
    "status"{ Infra-Status }
    default  { Write-Host "Ação inválida. Use: up, down ou status." -ForegroundColor Red; exit 1 }
}
