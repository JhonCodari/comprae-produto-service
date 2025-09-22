package br.com.comprae.produto.configuracao;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Configuração dos padrões de resiliência usando Resilience4j
 */
@Configuration
public class ResilienceConfig {

    /**
     * Configuração do Circuit Breaker
     */
    @Bean
    public CircuitBreaker produtoServiceCircuitBreaker() {
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // 50% de falhas para abrir o circuito
                .waitDurationInOpenState(Duration.ofSeconds(30)) // 30s aguardando no estado aberto
                .slidingWindowSize(10) // Janela de 10 requisições
                .minimumNumberOfCalls(5) // Mínimo de 5 chamadas para calcular taxa de falha
                .permittedNumberOfCallsInHalfOpenState(3) // 3 chamadas permitidas no estado meio-aberto
                .slowCallRateThreshold(50) // 50% de chamadas lentas para abrir
                .slowCallDurationThreshold(Duration.ofSeconds(2)) // Chamadas > 2s são lentas
                .recordExceptions(
                        RuntimeException.class,
                        Exception.class
                )
                .ignoreExceptions(
                        IllegalArgumentException.class // Não considera erros de validação como falhas
                )
                .build();

        return CircuitBreaker.of("produto-service", config);
    }

    /**
     * Configuração do Retry
     */
    @Bean
    public Retry produtoServiceRetry() {
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(3) // Máximo 3 tentativas
                .waitDuration(Duration.ofMillis(500)) // 500ms entre tentativas
                .retryOnException(throwable -> {
                    // Retry apenas para exceções específicas
                    return throwable instanceof RuntimeException 
                            && !(throwable instanceof IllegalArgumentException);
                })
                .build();

        return Retry.of("produto-service", config);
    }

    /**
     * Configuração do Bulkhead (Isolamento de recursos)
     */
    @Bean
    public Bulkhead produtoServiceBulkhead() {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(20) // Máximo 20 chamadas concorrentes
                .maxWaitDuration(Duration.ofMillis(100)) // Máximo 100ms de espera
                .build();

        return Bulkhead.of("produto-service", config);
    }

    /**
     * Configuração do Rate Limiter
     */
    @Bean
    public RateLimiter produtoServiceRateLimiter() {
        RateLimiterConfig config = RateLimiterConfig.custom()
                .limitForPeriod(100) // 100 requisições por período
                .limitRefreshPeriod(Duration.ofSeconds(1)) // Período de 1 segundo
                .timeoutDuration(Duration.ofMillis(200)) // Timeout de 200ms para adquirir permissão
                .build();

        return RateLimiter.of("produto-service", config);
    }
}