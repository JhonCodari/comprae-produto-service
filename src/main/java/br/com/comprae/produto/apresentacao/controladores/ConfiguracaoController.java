package br.com.comprae.produto.apresentacao.controladores;

import br.com.comprae.produto.configuracao.ConfiguracaoProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para visualizar configurações do Produto Service
 */
@RestController
@RequestMapping("/api/v1/configuracoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configurações", description = "API para visualizar configurações do produto service")
public class ConfiguracaoController {

    private final ConfiguracaoProdutoService configuracaoProdutoService;

    @Operation(summary = "Obter todas as configurações", description = "Retorna todas as configurações ativas do produto service")
    @GetMapping
    public ResponseEntity<Map<String, Object>> obterConfiguracoes() {
        log.info("Solicitação para obter todas as configurações do produto service");
        
        Map<String, Object> configuracoes = new HashMap<>();
        
        // Configurações de Banco de Dados
        configuracoes.put("database.pool.size", configuracaoProdutoService.getTamanhoPoolBancoDados());
        configuracoes.put("database.timeout", configuracaoProdutoService.getTimeoutBancoDados());
        
        // Configurações de Performance
        configuracoes.put("cache.produto.ttl", configuracaoProdutoService.getCacheProdutoTtl());
        configuracoes.put("batch.size", configuracaoProdutoService.getTamanhoBatch());
        
        // Configurações de Negócio
        configuracoes.put("produto.estoque.minimo", configuracaoProdutoService.getEstoqueMinimo());
        configuracoes.put("produto.categoria.ativa", configuracaoProdutoService.getCategoriaAtiva());
        configuracoes.put("produto.preco.maximo", configuracaoProdutoService.getPrecoMaximo());
        
        // Configurações de API
        configuracoes.put("api.timeout.externo", configuracaoProdutoService.getTimeoutApiExterno());
        configuracoes.put("api.retry.tentativas", configuracaoProdutoService.getTentativasRetry());
        
        // Feature Flags
        configuracoes.put("feature.busca.avancada", configuracaoProdutoService.getBuscaAvancadaHabilitada());
        configuracoes.put("feature.recomendacao", configuracaoProdutoService.getRecomendacaoHabilitada());
        configuracoes.put("feature.desconto.automatico", configuracaoProdutoService.getDescontoAutomaticoHabilitado());
        
        return ResponseEntity.ok(configuracoes);
    }

    @Operation(summary = "Obter configuração específica", description = "Retorna o valor de uma configuração específica do produto service")
    @GetMapping("/{chave}")
    public ResponseEntity<Map<String, Object>> obterConfiguracao(@PathVariable String chave) {
        
        log.info("Solicitação para obter configuração do produto service: {}", chave);
        
        Map<String, Object> resposta = new HashMap<>();
        resposta.put("chave", chave);
        resposta.put("servico", "produto-service");
        
        // Mapeia as configurações disponíveis
        switch (chave.toLowerCase()) {
            case "database.pool.size":
                resposta.put("valor", configuracaoProdutoService.getTamanhoPoolBancoDados());
                break;
            case "database.timeout":
                resposta.put("valor", configuracaoProdutoService.getTimeoutBancoDados());
                break;
            case "cache.produto.ttl":
                resposta.put("valor", configuracaoProdutoService.getCacheProdutoTtl());
                break;
            case "batch.size":
                resposta.put("valor", configuracaoProdutoService.getTamanhoBatch());
                break;
            case "produto.estoque.minimo":
                resposta.put("valor", configuracaoProdutoService.getEstoqueMinimo());
                break;
            case "produto.categoria.ativa":
                resposta.put("valor", configuracaoProdutoService.getCategoriaAtiva());
                break;
            case "produto.preco.maximo":
                resposta.put("valor", configuracaoProdutoService.getPrecoMaximo());
                break;
            case "api.timeout.externo":
                resposta.put("valor", configuracaoProdutoService.getTimeoutApiExterno());
                break;
            case "api.retry.tentativas":
                resposta.put("valor", configuracaoProdutoService.getTentativasRetry());
                break;
            case "feature.busca.avancada":
                resposta.put("valor", configuracaoProdutoService.getBuscaAvancadaHabilitada());
                break;
            case "feature.recomendacao":
                resposta.put("valor", configuracaoProdutoService.getRecomendacaoHabilitada());
                break;
            case "feature.desconto.automatico":
                resposta.put("valor", configuracaoProdutoService.getDescontoAutomaticoHabilitado());
                break;
            default:
                resposta.put("valor", null);
                resposta.put("erro", "Configuração não encontrada");
        }
        
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Informações do produto service", description = "Retorna informações básicas sobre o produto service")
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> informacoesServico() {
        log.info("Solicitação para informações do produto service");
        
        Map<String, Object> info = new HashMap<>();
        info.put("nome", "Compraê Produto Service");
        info.put("descricao", "Microserviço responsável pelo gerenciamento de produtos");
        info.put("versao", "1.0.0");
        info.put("perfil", "dev");
        info.put("ambiente", "desenvolvimento");
        info.put("porta", 8081);
        
        return ResponseEntity.ok(info);
    }

    @Operation(summary = "Health check das configurações", description = "Verifica se as configurações estão carregadas corretamente")
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Health check das configurações do produto service");
        
        Map<String, Object> health = new HashMap<>();
        
        try {
            // Verifica se as configurações essenciais estão disponíveis
            boolean databaseOk = configuracaoProdutoService.getTamanhoPoolBancoDados() > 0;
            boolean cacheOk = configuracaoProdutoService.getCacheProdutoTtl() > 0;
            boolean negocioOk = configuracaoProdutoService.getEstoqueMinimo() >= 0;
            
            boolean healthy = databaseOk && cacheOk && negocioOk;
            
            health.put("status", healthy ? "UP" : "DOWN");
            health.put("database_config", databaseOk ? "OK" : "ERROR");
            health.put("cache_config", cacheOk ? "OK" : "ERROR");
            health.put("business_config", negocioOk ? "OK" : "ERROR");
            health.put("timestamp", System.currentTimeMillis());
            
            return healthy ? ResponseEntity.ok(health) : ResponseEntity.status(503).body(health);
            
        } catch (Exception e) {
            log.error("Erro no health check das configurações", e);
            health.put("status", "DOWN");
            health.put("error", e.getMessage());
            return ResponseEntity.status(503).body(health);
        }
    }
}
