package br.com.comprae.produto.configuracao;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de Métricas e Observabilidade
 */
@Configuration
public class MetricsConfig {

    private static final Logger logger = LoggerFactory.getLogger(MetricsConfig.class);

    /**
     * Customização do registry de métricas
     */
    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> {
            registry.config().commonTags(
                    "application", "produto-service",
                    "version", "2.0.0",
                    "environment", "dev"
            );
            logger.info("Métricas configuradas com tags comuns");
        };
    }

    /**
     * Aspect para métricas @Timed
     */
    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }

    /**
     * Contador de produtos criados
     */
    @Bean
    public Counter produtosCriadosCounter(MeterRegistry registry) {
        return Counter.builder("produtos.criados.total")
                .description("Total de produtos criados")
                .register(registry);
    }

    /**
     * Contador de produtos atualizados
     */
    @Bean
    public Counter produtosAtualizadosCounter(MeterRegistry registry) {
        return Counter.builder("produtos.atualizados.total")
                .description("Total de produtos atualizados")
                .register(registry);
    }

    /**
     * Contador de produtos removidos
     */
    @Bean
    public Counter produtosRemovidosCounter(MeterRegistry registry) {
        return Counter.builder("produtos.removidos.total")
                .description("Total de produtos removidos")
                .register(registry);
    }

    /**
     * Timer para operações de banco de dados
     */
    @Bean
    public Timer databaseOperationTimer(MeterRegistry registry) {
        return Timer.builder("database.operations.duration")
                .description("Tempo de execução das operações de banco de dados")
                .register(registry);
    }

    /**
     * Timer para cache operations
     */
    @Bean
    public Timer cacheOperationTimer(MeterRegistry registry) {
        return Timer.builder("cache.operations.duration")
                .description("Tempo de execução das operações de cache")
                .register(registry);
    }

    /**
     * Contador de cache hits
     */
    @Bean
    public Counter cacheHitsCounter(MeterRegistry registry) {
        return Counter.builder("cache.hits.total")
                .description("Total de cache hits")
                .register(registry);
    }

    /**
     * Contador de cache misses
     */
    @Bean
    public Counter cacheMissesCounter(MeterRegistry registry) {
        return Counter.builder("cache.misses.total")
                .description("Total de cache misses")
                .register(registry);
    }

    /**
     * Gauge para produtos ativos
     */
    @Bean
    public Gauge produtosAtivosGauge(MeterRegistry registry) {
        return Gauge.builder("produtos.ativos.count", this, MetricsConfig::getProdutosAtivosCount)
                .description("Número atual de produtos ativos")
                .register(registry);
    }

    /**
     * Gauge para estoque total
     */
    @Bean
    public Gauge estoqueTotalGauge(MeterRegistry registry) {
        return Gauge.builder("estoque.total.count", this, MetricsConfig::getEstoqueTotalCount)
                .description("Quantidade total em estoque")
                .register(registry);
    }

    // Métodos auxiliares para gauges (serão implementados com consultas reais)
    private double getProdutosAtivosCount() {
        // TODO: Implementar consulta real ao banco
        return 0.0;
    }

    private double getEstoqueTotalCount() {
        // TODO: Implementar consulta real ao banco
        return 0.0;
    }
}