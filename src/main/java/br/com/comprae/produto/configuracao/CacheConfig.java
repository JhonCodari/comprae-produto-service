package br.com.comprae.produto.configuracao;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Configuração de Cache Multi-Camada (L1: Caffeine, L2: Redis)
 */
@Configuration
@EnableCaching
public class CacheConfig {

    private static final Logger logger = LoggerFactory.getLogger(CacheConfig.class);

    @Value("${cache.caffeine.maximum-size:1000}")
    private long caffeineMaximumSize;

    @Value("${cache.caffeine.expire-after-write:300}")
    private long caffeineExpireAfterWrite;

    @Value("${cache.redis.ttl:3600}")
    private long redisTtl;

    /**
     * Cache Manager L1 - Caffeine (Local)
     */
    @Bean
    @Primary
    public CacheManager caffeineCacheManager() {
        logger.info("Configurando Caffeine Cache Manager - MaxSize: {}, ExpireAfter: {}s", 
                caffeineMaximumSize, caffeineExpireAfterWrite);
        
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(caffeineMaximumSize)
                .expireAfterWrite(caffeineExpireAfterWrite, TimeUnit.SECONDS)
                .recordStats());
                
        // Nomes dos caches
        cacheManager.setCacheNames(java.util.Arrays.asList("produtos-l1", "produtos-sku-l1", "produtos-fornecedor-l1", 
                                 "configuracoes-l1", "produtos-stats-l1"));
        
        return cacheManager;
    }

    /**
     * Cache Manager L2 - Redis (Distribuído)
     */
    @Bean
    @ConditionalOnProperty(name = "spring.data.redis.host")
    public CacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        logger.info("Configurando Redis Cache Manager - TTL: {}s", redisTtl);
        
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(redisTtl))
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();
    }

    /**
     * Configuração específica para cache de produtos
     */
    @Bean
    public RedisCacheConfiguration produtoCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // TTL específico para produtos
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .prefixCacheNameWith("produto-service:")
                .disableCachingNullValues();
    }
}