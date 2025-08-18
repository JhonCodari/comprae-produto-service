# Script para build da imagem Docker do comprae-produto-service

try {
    Push-Location ".."
    Write-Host "Navegando para o diretório comprae-produto-service..." -ForegroundColor Blue
} catch {
    Write-Host "❌ Não foi possível navegar para o diretório comprae-produto-service" -ForegroundColor Red
    exit 1
}
docker build -t comprae/produto-service:latest .

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Imagem comprae/produto-service:latest criada com sucesso." -ForegroundColor Green
    $buildSuccess = $true
} else {
    Write-Host "❌ Falha ao criar a imagem comprae/produto-service:latest." -ForegroundColor Red
    $buildSuccess = $false
}

# tambem deve retornar uma variavel $buildSuccess informando se o build foi bem-sucedido
return $buildSuccess

#imprimir o valor da variavel no console
Write-Host "O resultado do build Docker é: $buildSuccess"
