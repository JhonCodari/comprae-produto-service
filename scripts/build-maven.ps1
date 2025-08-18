# Script de build Maven para comprae-produto-service
try {
    Push-Location ".."
    Write-Host "Navegando para o diretório comprae-produto-service..." -ForegroundColor Blue
} catch {
    Write-Host "❌ Não foi possível navegar para o diretório comprae-produto-service" -ForegroundColor Red
    exit 1
}
mvn clean package -DskipTests

if ($LASTEXITCODE -eq 0) {
	Write-Host "Build Maven concluído com sucesso."
	$buildSuccess = $true
} else {
	Write-Host "Build Maven falhou."
	$buildSuccess = $false
}

return $buildSuccess

Write-Host "O resultado do build Maven é: $buildSuccess"
