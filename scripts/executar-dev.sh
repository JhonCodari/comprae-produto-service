#!/bin/bash
# Script para ambiente de desenvolvimento local (infraestrutura + instrução para rodar app localmente)
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

# Parar containers existentes
$DIR/infra.sh down

# Subir infraestrutura
$DIR/infra.sh up

# Instruções para rodar aplicação local
cat <<EOF

🚀 Infraestrutura pronta! Agora execute a aplicação:

cd ..
./mvnw spring-boot:run

📊 Será disponível em:
   • http://localhost:8080/actuator/health
   • http://localhost:8080/swagger-ui.html

🛑 Para parar infraestrutura: ./scripts/infra.sh down
EOF
