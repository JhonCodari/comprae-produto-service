package br.com.comprae.produto.configuracao;

import com.configsystem.client.anotacao.ValorConfiguracao;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Configurações centralizadas do Produto Service
 * Usa o sistema de configuração do Compraê para valores dinâmicos
 */
@Configuration
@ComponentScan("com.configsystem.client")
public class ConfiguracaoProdutoService {

    // Configurações do Banco de Dados
    @ValorConfiguracao(value = "database.pool.size", defaultValue = "20")
    private Integer tamanhoPoolBancoDados;

    @ValorConfiguracao(value = "database.timeout", defaultValue = "30000")
    private Integer timeoutBancoDados;

    // Configurações de Performance
    @ValorConfiguracao(value = "cache.produto.ttl", defaultValue = "300")
    private Integer cacheProdutoTtl;

    @ValorConfiguracao(value = "batch.size", defaultValue = "50")
    private Integer tamanhoBatch;

    // Configurações de Negócio
    @ValorConfiguracao(value = "produto.estoque.minimo", defaultValue = "5")
    private Integer estoqueMinimo;

    @ValorConfiguracao(value = "produto.categoria.ativa", defaultValue = "true")
    private Boolean categoriaAtiva;

    @ValorConfiguracao(value = "produto.preco.maximo", defaultValue = "999999.99")
    private Double precoMaximo;

    // Configurações de API
    @ValorConfiguracao(value = "api.timeout.externo", defaultValue = "5000")
    private Integer timeoutApiExterno;

    @ValorConfiguracao(value = "api.retry.tentativas", defaultValue = "3")
    private Integer tentativasRetry;

    // Features Flags
    @ValorConfiguracao(value = "feature.busca.avancada", defaultValue = "true")
    private Boolean buscaAvancadaHabilitada;

    @ValorConfiguracao(value = "feature.recomendacao", defaultValue = "false")
    private Boolean recomendacaoHabilitada;

    @ValorConfiguracao(value = "feature.desconto.automatico", defaultValue = "false")
    private Boolean descontoAutomaticoHabilitado;

    // Getters para usar nos serviços
    public Integer getTamanhoPoolBancoDados() {
        return tamanhoPoolBancoDados;
    }

    public Integer getTimeoutBancoDados() {
        return timeoutBancoDados;
    }

    public Integer getCacheProdutoTtl() {
        return cacheProdutoTtl;
    }

    public Integer getTamanhoBatch() {
        return tamanhoBatch;
    }

    public Integer getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public Boolean getCategoriaAtiva() {
        return categoriaAtiva;
    }

    public Double getPrecoMaximo() {
        return precoMaximo;
    }

    public Integer getTimeoutApiExterno() {
        return timeoutApiExterno;
    }

    public Integer getTentativasRetry() {
        return tentativasRetry;
    }

    public Boolean getBuscaAvancadaHabilitada() {
        return buscaAvancadaHabilitada;
    }

    public Boolean getRecomendacaoHabilitada() {
        return recomendacaoHabilitada;
    }

    public Boolean getDescontoAutomaticoHabilitado() {
        return descontoAutomaticoHabilitado;
    }
}
