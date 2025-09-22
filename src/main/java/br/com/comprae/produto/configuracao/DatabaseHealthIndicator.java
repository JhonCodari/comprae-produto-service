package br.com.comprae.produto.configuracao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Health Indicator customizado para verificar conectividade com banco de dados
 */
@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseHealthIndicator.class);
    
    private final DataSource dataSource;

    public DatabaseHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try {
            return checkDatabaseConnection();
        } catch (Exception e) {
            logger.error("Database health check failed", e);
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .withDetail("database", "PostgreSQL")
                    .build();
        }
    }

    private Health checkDatabaseConnection() throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT 1");
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                // Verificar também se a tabela de produtos existe
                try (PreparedStatement countStatement = connection.prepareStatement("SELECT COUNT(*) FROM produtos")) {
                    try (ResultSet countResult = countStatement.executeQuery()) {
                        if (countResult.next()) {
                            long produtoCount = countResult.getLong(1);
                            return Health.up()
                                    .withDetail("database", "PostgreSQL")
                                    .withDetail("connection", "OK")
                                    .withDetail("produtos_count", produtoCount)
                                    .withDetail("validation_query", "SELECT 1")
                                    .build();
                        }
                    }
                }
            }
            
            return Health.down()
                    .withDetail("database", "PostgreSQL")
                    .withDetail("connection", "Failed to validate")
                    .build();
        }
    }
}