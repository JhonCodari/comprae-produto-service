package br.com.comprae.produto.aplicacao.servicos;

import br.com.comprae.produto.aplicacao.dtos.ProdutoDto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Serviço inteligente de cache para otimização de performance
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class IntelligentCacheService {

    private final ProdutoRepositorio produtoRepositorio;
    private final CacheManager cacheManager;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Aquece o cache na inicialização da aplicação
     */
    @EventListener(ApplicationReadyEvent.class)
    @Async
    public void warmUpCache() {
        log.info("Iniciando aquecimento do cache de produtos...");
        
        CompletableFuture.runAsync(() -> {
            try {
                // Aquece cache com produtos mais acessados
                warmUpTopProducts();
                
                // Aquece cache com produtos disponíveis
                warmUpAvailableProducts();
                
                log.info("Cache de produtos aquecido com sucesso");
            } catch (Exception e) {
                log.error("Erro ao aquecer cache de produtos", e);
            }
        });
    }

    /**
     * Aquece o cache com os produtos mais populares
     */
    private void warmUpTopProducts() {
        try {
            Pageable pageable = PageRequest.of(0, 50); // Top 50 produtos
            var produtos = produtoRepositorio.findProdutosDisponiveisParaVenda(pageable);
            
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null) {
                produtos.getContent().forEach(produto -> {
                    cache.put(produto.getId(), produto);
                    cache.put("sku:" + produto.getSku(), produto);
                });
            }
            
            log.debug("Cache aquecido com {} produtos populares", produtos.getNumberOfElements());
        } catch (Exception e) {
            log.warn("Erro ao aquecer cache com produtos populares", e);
        }
    }

    /**
     * Aquece o cache com produtos disponíveis para venda
     */
    private void warmUpAvailableProducts() {
        try {
            Pageable pageable = PageRequest.of(0, 100); // Top 100 produtos disponíveis
            var produtos = produtoRepositorio.findProdutosDisponiveisParaVenda(pageable);
            
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null) {
                produtos.getContent().forEach(produto -> {
                    cache.put(produto.getId(), produto);
                });
            }
            
            log.debug("Cache aquecido com {} produtos disponíveis", produtos.getNumberOfElements());
        } catch (Exception e) {
            log.warn("Erro ao aquecer cache com produtos disponíveis", e);
        }
    }

    /**
     * Invalida cache específico de um produto
     */
    public void invalidateProductCache(UUID productId, String sku) {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null) {
                cache.evict(productId);
                if (sku != null && !sku.trim().isEmpty()) {
                    cache.evict("sku:" + sku);
                }
            }
            
            log.debug("Cache invalidado para produto ID: {}, SKU: {}", productId, sku);
        } catch (Exception e) {
            log.warn("Erro ao invalidar cache do produto", e);
        }
    }

    /**
     * Invalida todo o cache de produtos
     */
    public void invalidateAllProductsCache() {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null) {
                cache.clear();
            }
            
            log.info("Todo o cache de produtos foi invalidado");
        } catch (Exception e) {
            log.warn("Erro ao invalidar todo o cache de produtos", e);
        }
    }

    /**
     * Pré-aquece o cache com um produto específico
     */
    @Async
    public CompletableFuture<Void> preWarmProduct(UUID productId) {
        return CompletableFuture.runAsync(() -> {
            try {
                var produto = produtoRepositorio.findById(productId);
                if (produto.isPresent()) {
                    org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
                    if (cache != null) {
                        cache.put(productId, produto.get());
                        cache.put("sku:" + produto.get().getSku(), produto.get());
                    }
                    log.debug("Produto pré-aquecido no cache: {}", productId);
                }
            } catch (Exception e) {
                log.warn("Erro ao pré-aquecer produto no cache", e);
            }
        });
    }

    /**
     * Limpeza periódica de cache expirado
     */
    @Scheduled(fixedRate = 300000) // A cada 5 minutos
    public void cleanupExpiredCache() {
        try {
            // Cleanup do cache local (Caffeine)
            cleanupLocalCache();
            
            // Cleanup do cache distribuído (Redis)
            cleanupDistributedCache();
            
            log.debug("Limpeza de cache expirado executada");
        } catch (Exception e) {
            log.warn("Erro durante limpeza de cache", e);
        }
    }

    /**
     * Limpa cache local expirado
     */
    private void cleanupLocalCache() {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null && cache.getNativeCache() instanceof Cache) {
                Cache<Object, Object> caffeineCache = (Cache<Object, Object>) cache.getNativeCache();
                caffeineCache.cleanUp();
            }
        } catch (Exception e) {
            log.warn("Erro ao limpar cache local", e);
        }
    }

    /**
     * Limpa cache distribuído expirado
     */
    private void cleanupDistributedCache() {
        try {
            // Busca chaves de produto no Redis que podem estar expiradas
            Set<String> keys = redisTemplate.keys("produtos::*");
            if (keys != null && !keys.isEmpty()) {
                keys.forEach(key -> {
                    try {
                        Long ttl = redisTemplate.getExpire(key);
                        if (ttl != null && ttl < 0) {
                            redisTemplate.delete(key);
                        }
                    } catch (Exception e) {
                        log.debug("Erro ao verificar TTL da chave: {}", key);
                    }
                });
            }
        } catch (Exception e) {
            log.warn("Erro ao limpar cache distribuído", e);
        }
    }

    /**
     * Obtém estatísticas do cache
     */
    public CacheStats getCacheStats() {
        try {
            org.springframework.cache.Cache cache = cacheManager.getCache("produtos");
            if (cache != null && cache.getNativeCache() instanceof Cache) {
                Cache<Object, Object> caffeineCache = (Cache<Object, Object>) cache.getNativeCache();
                com.github.benmanes.caffeine.cache.stats.CacheStats stats = caffeineCache.stats();
                
                return CacheStats.builder()
                        .hitCount(stats.hitCount())
                        .missCount(stats.missCount())
                        .hitRate(stats.hitRate())
                        .evictionCount(stats.evictionCount())
                        .loadCount(stats.loadCount())
                        .totalLoadTime(stats.totalLoadTime())
                        .build();
            }
        } catch (Exception e) {
            log.warn("Erro ao obter estatísticas do cache", e);
        }
        
        return CacheStats.empty();
    }

    /**
     * Classe para representar estatísticas do cache
     */
    public static class CacheStats {
        private final long hitCount;
        private final long missCount;
        private final double hitRate;
        private final long evictionCount;
        private final long loadCount;
        private final long totalLoadTime;

        private CacheStats(long hitCount, long missCount, double hitRate, 
                          long evictionCount, long loadCount, long totalLoadTime) {
            this.hitCount = hitCount;
            this.missCount = missCount;
            this.hitRate = hitRate;
            this.evictionCount = evictionCount;
            this.loadCount = loadCount;
            this.totalLoadTime = totalLoadTime;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static CacheStats empty() {
            return new CacheStats(0, 0, 0.0, 0, 0, 0);
        }

        // Getters
        public long getHitCount() { return hitCount; }
        public long getMissCount() { return missCount; }
        public double getHitRate() { return hitRate; }
        public long getEvictionCount() { return evictionCount; }
        public long getLoadCount() { return loadCount; }
        public long getTotalLoadTime() { return totalLoadTime; }

        public static class Builder {
            private long hitCount;
            private long missCount;
            private double hitRate;
            private long evictionCount;
            private long loadCount;
            private long totalLoadTime;

            public Builder hitCount(long hitCount) {
                this.hitCount = hitCount;
                return this;
            }

            public Builder missCount(long missCount) {
                this.missCount = missCount;
                return this;
            }

            public Builder hitRate(double hitRate) {
                this.hitRate = hitRate;
                return this;
            }

            public Builder evictionCount(long evictionCount) {
                this.evictionCount = evictionCount;
                return this;
            }

            public Builder loadCount(long loadCount) {
                this.loadCount = loadCount;
                return this;
            }

            public Builder totalLoadTime(long totalLoadTime) {
                this.totalLoadTime = totalLoadTime;
                return this;
            }

            public CacheStats build() {
                return new CacheStats(hitCount, missCount, hitRate, evictionCount, loadCount, totalLoadTime);
            }
        }
    }
}