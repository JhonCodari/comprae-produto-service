package br.com.comprae.produto.configuracao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

/**
 * Health Indicator customizado para verificar conectividade com Redis
 */
@Component
@ConditionalOnProperty(name = "spring.data.redis.host")
public class RedisHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(RedisHealthIndicator.class);
    
    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthIndicator(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Override
    public Health health() {
        try {
            return checkRedisConnection();
        } catch (Exception e) {
            logger.error("Redis health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("cache", "Redis")
                    .build();
        }
    }

    private Health checkRedisConnection() throws Exception {
        try (RedisConnection connection = redisConnectionFactory.getConnection()) {
            if (connection.ping() != null) {
                // Verificar informações adicionais do Redis
                try {
                    String info = connection.info("server").toString();
                    
                    return Health.up()
                            .withDetail("cache", "Redis")
                            .withDetail("connection", "OK")
                            .withDetail("ping", "PONG")
                            .withDetail("server_info", info.length() > 0 ? "Available" : "Limited")
                            .build();
                } catch (Exception e) {
                    // Se não conseguir obter info, mas ping funcionou
                    return Health.up()
                            .withDetail("cache", "Redis")
                            .withDetail("connection", "OK")
                            .withDetail("ping", "PONG")
                            .withDetail("info", "Not available")
                            .build();
                }
            }
            
            return Health.down()
                    .withDetail("cache", "Redis")
                    .withDetail("connection", "Failed to ping")
                    .build();
        }
    }
}