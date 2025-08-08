package br.com.comprae.produto.aplicacao.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta para produto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProdutoDto {

    private UUID id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private String sku;
    private String categoria;
    private String marca;
    private BigDecimal pesoKg;
    private String dimensoes;
    private String cor;
    private String tamanho;
    private Boolean ativo;
    private Boolean disponivelVenda;
    private String urlImagem;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;
    private String fornecedorId;
    private Boolean disponivelParaVenda;
}
