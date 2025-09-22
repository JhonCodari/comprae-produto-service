# Microserviços, Docker e Integração

## 1. Contêineres por Serviço
- Cada microserviço deve rodar em seu próprio contêiner.
- Componentes de infraestrutura (banco de dados, cache, gateway) também devem ter contêineres separados.
- Isso garante isolamento, escalabilidade e facilidade de manutenção.

## 2. Banco de Dados por Serviço
- Cada microserviço deve ter seu próprio banco de dados, rodando em contêiner separado.
- Evite compartilhar o mesmo banco entre serviços para manter o isolamento e evitar acoplamento.

**Exemplo docker-compose.yml:**
```yaml
services:
  produto-service:
    # ...configuração...
    depends_on:
      - produto-db

  produto-db:
    image: postgres:16
    environment:
      POSTGRES_DB: produto
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5433:5432"
    depends_on:
      - usuario-db
    environment:
      POSTGRES_DB: usuario
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5434:5432"

  pedido-service:
    # ...configuração...
    depends_on:
      - pedido-db

  pedido-db:
    image: postgres:16
    environment:
      POSTGRES_DB: pedido
      POSTGRES_USER: user
      POSTGRES_PASSWORD: password
    ports:
      - "5435:5432"
```

## 3. Integração entre Serviços
- A integração ocorre via APIs (REST, gRPC) ou mensageria (eventos, filas).
- Cada serviço acessa apenas seu próprio banco de dados.
- Para agregar dados de vários serviços, utilize técnicas como API Gateway, chamadas diretas entre serviços ou eventos.

## 4. BFF (Backend for Frontend)
- O BFF é uma camada intermediária que consome dados de vários microserviços e entrega uma resposta personalizada para o frontend.

**Fluxo exemplo:**
```
[Frontend] --> [BFF] --> [produto-service]
                      --> [usuario-service]
                      --> [pedido-service]
```

## Resumo
- Use um contêiner para cada serviço e cada banco de dados.
- Integração por APIs ou eventos, nunca por acesso direto ao banco de outro serviço.
- BFF pode ser usado para agregar e entregar dados ao frontend.
