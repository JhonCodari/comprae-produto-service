package br.com.comprae.produto.aplicacao.dtos;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para filtros de busca de produtos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiltroProdutoDto {

    private String categoria;
    private String marca;
    private BigDecimal precoMinimo;
    private BigDecimal precoMaximo;
    private Boolean ativo;
    private Boolean disponivelVenda;
    private String fornecedorId;
    private String texto; // Para busca textual em nome e descrição
}
