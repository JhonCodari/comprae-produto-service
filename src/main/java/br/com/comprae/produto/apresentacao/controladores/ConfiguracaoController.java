package br.com.comprae.produto.apresentacao.controladores;

import br.com.comprae.produto.configuracao.ConfiguracaoProdutoService;
import com.configsystem.client.servico.ServicoClienteConfiguracao;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para demonstrar integração com o sistema de configuração centralizada
 */
@RestController
@RequestMapping("/api/v1/configuracoes")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Configurações", description = "API para visualizar configurações do produto service")
public class ConfiguracaoController {

    private final ConfiguracaoProdutoService configuracaoProdutoService;
    private final ServicoClienteConfiguracao servicoClienteConfiguracao;

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

    @Operation(summary = "Obter configuração específica", description = "Retorna o valor de uma configuração específica")
    @GetMapping("/{chave}")
    public ResponseEntity<Map<String, String>> obterConfiguracao(
            @PathVariable String chave,
            @RequestParam(defaultValue = "default") String valorPadrao) {
        
        log.info("Solicitação para obter configuração: {}", chave);
        
        String valor = servicoClienteConfiguracao.buscarValorConfiguracao(chave, valorPadrao);
        
        Map<String, String> resposta = new HashMap<>();
        resposta.put("chave", chave);
        resposta.put("valor", valor);
        resposta.put("fonte", valor.equals(valorPadrao) ? "padrao" : "servidor");
        
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Atualizar configuração no cache", description = "Força atualização de uma configuração específica")
    @PostMapping("/{chave}/atualizar")
    public ResponseEntity<Map<String, String>> atualizarConfiguracao(@PathVariable String chave) {
        log.info("Forçando atualização da configuração: {}", chave);
        
        servicoClienteConfiguracao.atualizarConfiguracao(chave);
        
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Configuração atualizada com sucesso");
        resposta.put("chave", chave);
        
        return ResponseEntity.ok(resposta);
    }

    @Operation(summary = "Status do servidor de configuração", description = "Verifica se o servidor de configuração está disponível")
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> statusServidorConfiguracao() {
        log.info("Verificando status do servidor de configuração");
        
        boolean disponivel = servicoClienteConfiguracao.isServidorConfigDisponivel();
        Map<String, String> cacheLocal = servicoClienteConfiguracao.obterCacheLocal();
        
        Map<String, Object> status = new HashMap<>();
        status.put("servidor_disponivel", disponivel);
        status.put("cache_local_tamanho", cacheLocal.size());
        status.put("cache_local_chaves", cacheLocal.keySet());
        
        return ResponseEntity.ok(status);
    }

    @Operation(summary = "Limpar cache local", description = "Limpa todo o cache local de configurações")
    @DeleteMapping("/cache")
    public ResponseEntity<Map<String, String>> limparCache() {
        log.info("Limpando cache local de configurações");
        
        servicoClienteConfiguracao.limparCache();
        
        Map<String, String> resposta = new HashMap<>();
        resposta.put("mensagem", "Cache local limpo com sucesso");
        
        return ResponseEntity.ok(resposta);
    }
}
