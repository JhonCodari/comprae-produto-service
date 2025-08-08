package br.com.comprae.produto.aplicacao.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * DTO para criação de produto
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CriarProdutoDto {

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    private String nome;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    private BigDecimal preco;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    private Integer quantidadeEstoque;

    @NotBlank(message = "SKU é obrigatório")
    @Size(min = 3, max = 50, message = "SKU deve ter entre 3 e 50 caracteres")
    private String sku;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(min = 2, max = 100, message = "Categoria deve ter entre 2 e 100 caracteres")
    private String categoria;

    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String marca;

    @DecimalMin(value = "0.001", message = "Peso deve ser maior que zero")
    @Digits(integer = 5, fraction = 3, message = "Peso deve ter no máximo 5 dígitos inteiros e 3 decimais")
    private BigDecimal pesoKg;

    @Size(max = 100, message = "Dimensões devem ter no máximo 100 caracteres")
    private String dimensoes;

    @Size(max = 50, message = "Cor deve ter no máximo 50 caracteres")
    private String cor;

    @Size(max = 20, message = "Tamanho deve ter no máximo 20 caracteres")
    private String tamanho;

    @Builder.Default
    private Boolean ativo = true;

    @Builder.Default
    private Boolean disponivelVenda = true;

    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String urlImagem;

    @NotBlank(message = "ID do fornecedor é obrigatório")
    private String fornecedorId;
}
