#!/bin/bash
# Build Maven para o Produto Service
cd .. || exit 1
if command -v mvn &>/dev/null; then
    ./mvnw clean package -DskipTests
    echo "[SUCCESS] Build Maven realizado com sucesso."
else
    echo "[ERROR] Maven não encontrado."
    exit 1
fi
cd - >/dev/null
