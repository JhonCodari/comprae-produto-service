# Compraê Produto Service

Microserviço de produtos do ecosistema Compraê - uma plataforma completa de e-commerce integrada com sistema de co3. **Execute com perfil de desenvolvimento (H2 em memória):**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. **Acesse a aplicação:**
   - API: http://localhost:8082/api/v1/produtos
   - Configurações: http://localhost:8082/api/v1/configuracoes
   - Swagger UI: http://localhost:8082/swagger-ui.htmlão centralizada.

## 📋 Descrição

O Compraê Produto Service é responsável por gerenciar todos os produtos do ecosistema Compraê, incluindo:

- ✅ Cadastro e atualização de produtos
- ✅ Consulta de produtos com filtros avançados
- ✅ Controle de estoque
- ✅ Categorização de produtos
- ✅ Gestão de fornecedores
- ✅ Status de disponibilidade
- ✅ **Integração com Config Server** (sistema de configuração centralizada)
- ✅ **Configurações dinâmicas** via anotações @ValorConfiguracao
- ✅ **Cache inteligente** de configurações com TTL
- ✅ **Notificações em tempo real** via Kafka

## 🚀 Tecnologias

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Data JPA**
- **Spring Security**
- **PostgreSQL** (produção)
- **H2 Database** (desenvolvimento)
- **Maven**
- **Docker & Docker Compose**
- **Swagger/OpenAPI 3**
- **JUnit 5**
- **Lombok**
- **Compraê Config Client SDK v1.1.1** (configuração centralizada com cache inteligente)
- **Apache Kafka** (eventos e notificações)
- **Redis** (cache)

## 📁 Estrutura do Projeto

```
src/
├── main/
│   ├── java/br/com/comprae/produto/
│   │   ├── aplicacao/          # Camada de aplicação
│   │   │   ├── dtos/           # Data Transfer Objects
│   │   │   ├── mappers/        # Mapeadores de entidades
│   │   │   └── servicos/       # Serviços de negócio
│   │   ├── apresentacao/       # Camada de apresentação
│   │   │   ├── controladores/  # Controllers REST
│   │   │   └── excecoes/       # Tratadores de exceção
│   │   ├── configuracao/       # Configurações da aplicação
│   │   ├── dominio/           # Camada de domínio
│   │   │   └── entidades/     # Entidades JPA
│   │   └── infraestrutura/    # Camada de infraestrutura
│   │       └── repositorios/  # Repositórios JPA
│   └── resources/
│       ├── application*.properties
│       ├── application-config.properties  # Configurações do Config Client
│       └── data.sql
└── test/                      # Testes unitários e integração
```

## 🔧 Sistema de Configuração Centralizada

O produto service está integrado com o **Compraê Config Server** para gerenciamento dinâmico de configurações.

### 📋 Configurações Disponíveis

| Configuração | Descrição | Valor Padrão |
|--------------|-----------|--------------|
| `database.pool.size` | Tamanho do pool de conexões | 20 |
| `database.timeout` | Timeout de conexão (ms) | 30000 |
| `cache.produto.ttl` | TTL do cache de produtos (s) | 300 |
| `batch.size` | Tamanho do batch para operações | 50 |
| `produto.estoque.minimo` | Estoque mínimo para alertas | 5 |
| `produto.categoria.ativa` | Sistema de categorias ativo | true |
| `produto.preco.maximo` | Preço máximo permitido | 999999.99 |
| `api.timeout.externo` | Timeout APIs externas (ms) | 5000 |
| `api.retry.tentativas` | Número de tentativas retry | 3 |
| `feature.busca.avancada` | Busca avançada habilitada | true |
| `feature.recomendacao` | Sistema recomendações | false |
| `feature.desconto.automatico` | Descontos automáticos | false |

### 🔄 Uso das Configurações

```java
@Service
public class ProdutoService {
    
    @ValorConfiguracao("produto.estoque.minimo")
    private Integer estoqueMinimo;
    
    @ValorConfiguracao(value = "feature.busca.avancada", defaultValue = "true")
    private Boolean buscaAvancadaHabilitada;
    
    // As configurações são atualizadas automaticamente via Kafka
}
```

### � Novidades SDK v1.1.1

O serviço agora utiliza a versão mais recente do Compraê Config Client SDK com melhorias significativas:

#### 🎯 Cache Inteligente
- **TTL Automático**: Cache com expiração de 30 minutos
- **Eviction Policy**: Máximo de 10.000 entradas com remoção automática
- **Performance**: Até 270 operações/ms em cache sequencial
- **Estatísticas**: Métricas detalhadas de hit/miss ratio

#### 🔍 Validação Avançada
- **16 Tipos Suportados**: String, Integer, Boolean, Duration, LocalDate, etc.
- **Validação Regex**: Email, URL, IP, porta automáticas
- **Conversores Personalizados**: Extensível para tipos customizados
- **Performance**: Até 2.600 conversões/ms

#### 📊 Monitoramento
- **Health Checks**: Verificação automática de saúde do cache
- **Métricas**: Integração com Micrometer para observabilidade
- **Logging**: Sistema de logs estruturado com diferentes níveis

### �📡 Endpoints de Configuração

- `GET /api/v1/configuracoes` - Lista todas as configurações
- `GET /api/v1/configuracoes/{chave}` - Busca configuração específica
- `POST /api/v1/configuracoes/{chave}/atualizar` - Atualiza configuração
- `GET /api/v1/configuracoes/status` - Status do servidor de configuração
- `DELETE /api/v1/configuracoes/cache` - Limpa cache local
- `GET /api/v1/configuracoes/health` - **NOVO**: Health check do cache
- `GET /api/v1/configuracoes/metrics` - **NOVO**: Métricas de performance

## 🛠️ Como Executar

### Pré-requisitos

- Java 17+
- Maven 3.6+
- Docker e Docker Compose (opcional)

### Executar Localmente

#### 🎯 Método Rápido (Recomendado)

Execute o script integrado que configura tudo automaticamente:

```bash
# Windows PowerShell:
./iniciar-integrado.ps1

# Linux/macOS:
chmod +x iniciar-integrado.sh
./iniciar-integrado.sh
```

O script irá:
- ✅ Compilar o projeto
- ✅ Verificar/iniciar o Config Server
- ✅ Popular as configurações necessárias
- ✅ Detectar o ambiente (PostgreSQL ou H2)
- ✅ Iniciar o produto service

#### ⚙️ Método Manual

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/JonatasSilvaDev/comprae-produto-service.git
   cd comprae-produto-service
   ```

2. **Configure o Config Server (IMPORTANTE):**
   ```bash
   # Antes de executar o produto service, configure o Config Server:
   
   # 1. Inicie o Config Server
   cd ../comprae-config-server
   docker-compose up -d
   
   # 2. Aguarde estar disponível
   curl http://localhost:8888/actuator/health
   
   # 3. Popule as configurações do produto service
   cd ../comprae-produto-service
   
   # Windows PowerShell:
   ./scripts/popular-configuracoes.ps1
   
   # Linux/macOS:
   ./scripts/popular-configuracoes.sh
   ```

3. **Execute com perfil de desenvolvimento (H2 em memória):**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

3. **Acesse a aplicação:**
   - API: http://localhost:8080/api/v1/produtos
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - H2 Console: http://localhost:8080/h2-console

### Executar com Docker

1. **Compile a aplicação:**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Execute com Docker Compose:**
   ```bash
   docker-compose up -d
   ```

3. **Acesse a aplicação:**
   - API: http://localhost:8080/api/v1/produtos
   - Swagger UI: http://localhost:8080/swagger-ui.html

## 📚 API Endpoints

### Produtos

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/produtos` | Criar novo produto |
| GET | `/api/v1/produtos` | Listar produtos (paginado) |
| GET | `/api/v1/produtos/{id}` | Buscar produto por ID |
| GET | `/api/v1/produtos/sku/{sku}` | Buscar produto por SKU |
| PUT | `/api/v1/produtos/{id}` | Atualizar produto |
| DELETE | `/api/v1/produtos/{id}` | Remover produto |
| GET | `/api/v1/produtos/buscar` | Buscar com filtros |
| GET | `/api/v1/produtos/disponiveis` | Produtos disponíveis |
| GET | `/api/v1/produtos/fornecedor/{id}` | Produtos por fornecedor |

### Estoque

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| POST | `/api/v1/produtos/{id}/estoque/adicionar` | Adicionar estoque |
| POST | `/api/v1/produtos/{id}/estoque/remover` | Remover estoque |
| GET | `/api/v1/produtos/estoque-baixo` | Produtos com estoque baixo |

### Status

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| PATCH | `/api/v1/produtos/{id}/ativar` | Ativar produto |
| PATCH | `/api/v1/produtos/{id}/desativar` | Desativar produto |

## 📝 Exemplos de Uso

### Criar Produto

```json
POST /api/v1/produtos
{
  "nome": "Smartphone Samsung Galaxy S24",
  "descricao": "Smartphone com 256GB e câmera tripla",
  "preco": 2999.99,
  "quantidadeEstoque": 50,
  "sku": "SAMS24-256GB-BLK",
  "categoria": "Eletrônicos",
  "marca": "Samsung",
  "pesoKg": 0.168,
  "dimensoes": "14.6 x 7.1 x 0.76 cm",
  "cor": "Preto",
  "urlImagem": "https://exemplo.com/imagem.jpg",
  "fornecedorId": "fornecedor-001"
}
```

### Buscar com Filtros

```
GET /api/v1/produtos/buscar?categoria=Eletrônicos&precoMinimo=1000&precoMaximo=5000&page=0&size=10
```

## 🧪 Testes

### Executar Testes Unitários

```bash
mvn test
```

### Executar Testes de Integração

```bash
mvn test -Dtest=**/*IntegrationTest
```

### Executar Todos os Testes

```bash
mvn verify
```

## 🔧 Configuração

### Variáveis de Ambiente

| Variável | Descrição | Padrão |
|----------|-----------|---------|
| SPRING_PROFILES_ACTIVE | Perfil ativo | dev |
| SPRING_DATASOURCE_URL | URL do banco | jdbc:h2:mem:testdb |
| SPRING_DATASOURCE_USERNAME | Usuário do banco | sa |
| SPRING_DATASOURCE_PASSWORD | Senha do banco | |

### Perfis Disponíveis

- **dev**: Desenvolvimento com H2 em memória
- **docker**: Produção com PostgreSQL no Docker
- **test**: Testes com H2 em memória

## 📊 Monitoramento

### Actuator Endpoints

- `/actuator/health` - Status da aplicação
- `/actuator/info` - Informações da aplicação
- `/actuator/metrics` - Métricas da aplicação

## 🤝 Contribuição

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/NovaFeature`)
3. Commit suas mudanças (`git commit -m 'Adiciona nova feature'`)
4. Push para a branch (`git push origin feature/NovaFeature`)
5. Abra um Pull Request

## 📄 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para detalhes.

## 📞 Contato

- **Equipe Compraê** - dev@comprae.com.br
- **Website** - https://comprae.com.br

---

🛍️ **Compraê** - Transformando a experiência de compras online
