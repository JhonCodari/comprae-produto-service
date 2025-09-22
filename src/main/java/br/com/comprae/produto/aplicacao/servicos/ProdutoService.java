package br.com.comprae.produto.aplicacao.servicos;

import br.com.comprae.produto.aplicacao.dtos.*;
import br.com.comprae.produto.aplicacao.mappers.ProdutoMapper;
import br.com.comprae.produto.dominio.entidades.Produto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de negócio para operações com produtos
 */
@Service
@Slf4j
public class ProdutoService {

    private final ProdutoRepositorio produtoRepositorio;
    private final ProdutoMapper produtoMapper;
    private final MeterRegistry meterRegistry;
    
    // Metrics counters
    private final Counter produtosCriadosCounter;
    private final Counter produtosAtualizadosCounter;
    private final Counter produtosRemovidosCounter;
    private final Counter produtosBuscadosCounter;
    private final Counter estoqueAdicionadoCounter;
    private final Counter estoqueRemovidoCounter;
    private final Timer databaseOperationTimer;
    private final Timer cacheOperationTimer;

    public ProdutoService(ProdutoRepositorio produtoRepositorio, 
                         ProdutoMapper produtoMapper, 
                         MeterRegistry meterRegistry) {
        this.produtoRepositorio = produtoRepositorio;
        this.produtoMapper = produtoMapper;
        this.meterRegistry = meterRegistry;
        
        // Initialize metrics only if meterRegistry is not null
        if (meterRegistry != null) {
            this.produtosCriadosCounter = Counter.builder("produtos.criados.total")
                    .description("Total de produtos criados")
                    .register(meterRegistry);
                    
            this.produtosAtualizadosCounter = Counter.builder("produtos.atualizados.total")
                    .description("Total de produtos atualizados")
                    .register(meterRegistry);
                    
            this.produtosRemovidosCounter = Counter.builder("produtos.removidos.total")
                    .description("Total de produtos removidos")
                    .register(meterRegistry);
                    
            this.produtosBuscadosCounter = Counter.builder("produtos.buscados.total")
                    .description("Total de produtos buscados")
                    .register(meterRegistry);
                    
            this.estoqueAdicionadoCounter = Counter.builder("estoque.adicionado.total")
                    .description("Total de operações de adição de estoque")
                    .register(meterRegistry);
                    
            this.estoqueRemovidoCounter = Counter.builder("estoque.removido.total")
                    .description("Total de operações de remoção de estoque")
                    .register(meterRegistry);
                    
            this.databaseOperationTimer = Timer.builder("produto.database.operation.duration")
                    .description("Tempo de operações no banco de dados")
                    .register(meterRegistry);
                    
            this.cacheOperationTimer = Timer.builder("produto.cache.operation.duration")
                    .description("Tempo de operações de cache")
                    .register(meterRegistry);
        } else {
            // Initialize with no-op metrics for tests
            this.produtosCriadosCounter = Counter.builder("produtos.criados.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.produtosAtualizadosCounter = Counter.builder("produtos.atualizados.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.produtosRemovidosCounter = Counter.builder("produtos.removidos.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.produtosBuscadosCounter = Counter.builder("produtos.buscados.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.estoqueAdicionadoCounter = Counter.builder("estoque.adicionado.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.estoqueRemovidoCounter = Counter.builder("estoque.removido.total").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.databaseOperationTimer = Timer.builder("produto.database.operation.duration").register(io.micrometer.core.instrument.Metrics.globalRegistry);
            this.cacheOperationTimer = Timer.builder("produto.cache.operation.duration").register(io.micrometer.core.instrument.Metrics.globalRegistry);
        }
    }

    /**
     * Cria um novo produto
     */
    @Transactional
    @CircuitBreaker(name = "produto-service", fallbackMethod = "criarProdutoFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    @RateLimiter(name = "produto-service")
    public ProdutoDto criarProduto(CriarProdutoDto criarProdutoDto) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.info("Criando novo produto com SKU: {}", criarProdutoDto.getSku());

            // Verifica se já existe produto com o mesmo SKU
            if (produtoRepositorio.existsBySku(criarProdutoDto.getSku())) {
                throw new IllegalArgumentException("Já existe um produto com o SKU: " + criarProdutoDto.getSku());
            }

            Produto produto = produtoMapper.toEntity(criarProdutoDto);
            Produto produtoSalvo = produtoRepositorio.save(produto);

            produtosCriadosCounter.increment();
            log.info("Produto criado com sucesso. ID: {}, SKU: {}", produtoSalvo.getId(), produtoSalvo.getSku());
            return produtoMapper.toDto(produtoSalvo);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto criarProdutoFallback(CriarProdutoDto criarProdutoDto, Exception ex) {
        log.error("Fallback executado para criação de produto com SKU: {}. Erro: {}", 
                  criarProdutoDto.getSku(), ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.", ex);
    }

    /**
     * Busca produto por ID
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "#id")
    @CircuitBreaker(name = "produto-service", fallbackMethod = "buscarPorIdFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public ProdutoDto buscarPorId(UUID id) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.debug("Buscando produto por ID: {}", id);

            Produto produto = produtoRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

            produtosBuscadosCounter.increment();
            return produtoMapper.toDto(produto);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto buscarPorIdFallback(UUID id, Exception ex) {
        log.error("Fallback executado para busca por ID: {}. Erro: {}", id, ex.getMessage());
        throw new RuntimeException("Produto temporariamente indisponível", ex);
    }

    /**
     * Busca produto por SKU
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "produtos", key = "'sku:' + #sku")
    @CircuitBreaker(name = "produto-service", fallbackMethod = "buscarPorSkuFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public ProdutoDto buscarPorSku(String sku) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.debug("Buscando produto por SKU: {}", sku);

            Produto produto = produtoRepositorio.findBySku(sku)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com SKU: " + sku));

            produtosBuscadosCounter.increment();
            return produtoMapper.toDto(produto);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto buscarPorSkuFallback(String sku, Exception ex) {
        log.error("Fallback executado para busca por SKU: {}. Erro: {}", sku, ex.getMessage());
        throw new RuntimeException("Produto temporariamente indisponível", ex);
    }

    /**
     * Lista todos os produtos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> listarProdutos(Pageable pageable) {
        log.debug("Listando produtos com paginação: {}", pageable);

        return produtoRepositorio.findAll(pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Busca produtos com filtros
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarComFiltros(FiltroProdutoDto filtros, Pageable pageable) {
        log.debug("Buscando produtos com filtros: {}", filtros);

        // Se houver busca textual, usa busca específica
        if (filtros.getTexto() != null && !filtros.getTexto().trim().isEmpty()) {
            return produtoRepositorio.buscarPorTexto(filtros.getTexto().trim(), pageable)
                    .map(produtoMapper::toDto);
        }

        // Caso contrário, usa busca com filtros específicos
        return produtoRepositorio.findComFiltros(
                filtros.getCategoria(),
                filtros.getMarca(),
                filtros.getPrecoMinimo(),
                filtros.getPrecoMaximo(),
                filtros.getAtivo(),
                filtros.getDisponivelVenda(),
                pageable
        ).map(produtoMapper::toDto);
    }

    /**
     * Busca produtos disponíveis para venda
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarDisponiveis(Pageable pageable) {
        log.debug("Buscando produtos disponíveis para venda");

        return produtoRepositorio.findProdutosDisponiveisParaVenda(pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Busca produtos por fornecedor
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarPorFornecedor(String fornecedorId, Pageable pageable) {
        log.debug("Buscando produtos do fornecedor: {}", fornecedorId);

        return produtoRepositorio.findByFornecedorId(fornecedorId, pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Atualiza um produto
     */
    @Transactional
    @CachePut(value = "produtos", key = "#id")
    @CacheEvict(value = "produtos", key = "'sku:' + #result.sku") 
    @CircuitBreaker(name = "produto-service", fallbackMethod = "atualizarProdutoFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public ProdutoDto atualizarProduto(UUID id, AtualizarProdutoDto atualizarProdutoDto) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.info("Atualizando produto ID: {}", id);

            Produto produto = produtoRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

            produtoMapper.updateEntity(produto, atualizarProdutoDto);
            Produto produtoAtualizado = produtoRepositorio.save(produto);

            produtosAtualizadosCounter.increment();
            log.info("Produto atualizado com sucesso. ID: {}", produtoAtualizado.getId());
            return produtoMapper.toDto(produtoAtualizado);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto atualizarProdutoFallback(UUID id, AtualizarProdutoDto atualizarProdutoDto, Exception ex) {
        log.error("Fallback executado para atualização do produto ID: {}. Erro: {}", id, ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.", ex);
    }

    /**
     * Remove um produto (soft delete)
     */
    @Transactional
    @CacheEvict(value = "produtos", allEntries = true)
    @CircuitBreaker(name = "produto-service", fallbackMethod = "removerProdutoFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public void removerProduto(UUID id) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.info("Removendo produto ID: {}", id);

            Produto produto = produtoRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

            produto.setAtivo(false);
            produto.setDisponivelVenda(false);
            produtoRepositorio.save(produto);

            produtosRemovidosCounter.increment();
            log.info("Produto removido com sucesso. ID: {}", id);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public void removerProdutoFallback(UUID id, Exception ex) {
        log.error("Fallback executado para remoção do produto ID: {}. Erro: {}", id, ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.", ex);
    }

    /**
     * Adiciona estoque a um produto
     */
    @Transactional
    @CachePut(value = "produtos", key = "#id")
    @CircuitBreaker(name = "produto-service", fallbackMethod = "adicionarEstoqueFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public ProdutoDto adicionarEstoque(UUID id, EstoqueDto estoqueDto) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.info("Adicionando {} unidades ao estoque do produto ID: {}", estoqueDto.getQuantidade(), id);

            Produto produto = produtoRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

            produto.aumentarEstoque(estoqueDto.getQuantidade());
            Produto produtoAtualizado = produtoRepositorio.save(produto);

            estoqueAdicionadoCounter.increment();
            log.info("Estoque atualizado. Produto ID: {}, Nova quantidade: {}", 
                    id, produtoAtualizado.getQuantidadeEstoque());
            
            return produtoMapper.toDto(produtoAtualizado);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto adicionarEstoqueFallback(UUID id, EstoqueDto estoqueDto, Exception ex) {
        log.error("Fallback executado para adição de estoque do produto ID: {}. Erro: {}", id, ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.", ex);
    }

    /**
     * Remove estoque de um produto
     */
    @Transactional
    @CachePut(value = "produtos", key = "#id")
    @CircuitBreaker(name = "produto-service", fallbackMethod = "removerEstoqueFallback")
    @Retry(name = "produto-service")
    @Bulkhead(name = "produto-service")
    public ProdutoDto removerEstoque(UUID id, EstoqueDto estoqueDto) {
        Timer.Sample sample = meterRegistry != null ? Timer.start(meterRegistry) : null;
        try {
            log.info("Removendo {} unidades do estoque do produto ID: {}", estoqueDto.getQuantidade(), id);

            Produto produto = produtoRepositorio.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

            produto.reduzirEstoque(estoqueDto.getQuantidade());
            Produto produtoAtualizado = produtoRepositorio.save(produto);

            estoqueRemovidoCounter.increment();
            log.info("Estoque atualizado. Produto ID: {}, Nova quantidade: {}", 
                    id, produtoAtualizado.getQuantidadeEstoque());
            
            return produtoMapper.toDto(produtoAtualizado);
        } finally {
            if (sample != null) {
                sample.stop(databaseOperationTimer);
            }
        }
    }
    
    public ProdutoDto removerEstoqueFallback(UUID id, EstoqueDto estoqueDto, Exception ex) {
        log.error("Fallback executado para remoção de estoque do produto ID: {}. Erro: {}", id, ex.getMessage());
        throw new RuntimeException("Serviço temporariamente indisponível. Tente novamente mais tarde.", ex);
    }

    /**
     * Lista produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<ProdutoDto> listarProdutosComEstoqueBaixo(Integer limiteEstoque) {
        log.debug("Buscando produtos com estoque baixo. Limite: {}", limiteEstoque);

        return produtoRepositorio.findProdutosComEstoqueBaixo(limiteEstoque)
                .stream()
                .map(produtoMapper::toDto)
                .toList();
    }

    /**
     * Ativa um produto
     */
    @Transactional
    public ProdutoDto ativarProduto(UUID id) {
        log.info("Ativando produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.setAtivo(true);
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Produto ativado com sucesso. ID: {}", id);
        return produtoMapper.toDto(produtoAtualizado);
    }

    /**
     * Desativa um produto
     */
    @Transactional
    public ProdutoDto desativarProduto(UUID id) {
        log.info("Desativando produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.setAtivo(false);
        produto.setDisponivelVenda(false);
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Produto desativado com sucesso. ID: {}", id);
        return produtoMapper.toDto(produtoAtualizado);
    }
}
