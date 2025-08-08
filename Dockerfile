# Usar OpenJDK 17 como base
FROM openjdk:17-jdk-slim

# Definir diretório de trabalho
WORKDIR /app

# Copiar arquivo JAR da aplicação
COPY target/comprae-produto-service-1.0.0.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Configurar usuário não-root para segurança
RUN addgroup --system --gid 1001 spring && \
    adduser --system --uid 1001 --gid 1001 spring

USER spring:spring

# Comando para executar a aplicação
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
