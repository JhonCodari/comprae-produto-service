# Dockerfile multi-stage para build e execução
# Stage 1: Build da aplicação
FROM maven:3.9.11-eclipse-temurin-17-alpine AS builder

WORKDIR /app

# Copiar pom.xml e baixar dependências (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fonte e fazer build
COPY src src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine

# Configurar usuário não-root para segurança
RUN addgroup -g 1001 -S spring && \
    adduser -u 1001 -S spring -G spring

WORKDIR /app

# Copiar apenas o JAR da aplicação do stage anterior
COPY --from=builder /app/target/comprae-produto-service-*.jar app.jar

# Alterar propriedade do arquivo para o usuário criado
RUN chown spring:spring app.jar

USER spring:spring

# Expor porta da aplicação
EXPOSE 8082

# Configurações de JVM otimizadas
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:+UseStringDeduplication"

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8082/actuator/health || exit 1

# Comando para executar a aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
