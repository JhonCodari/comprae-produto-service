package br.com.comprae.produto.dominio.entidades;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidade que representa um Produto no sistema Compraê
 */
@Entity
@Table(name = "produtos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @NotBlank(message = "Nome do produto é obrigatório")
    @Size(min = 2, max = 255, message = "Nome deve ter entre 2 e 255 caracteres")
    @Column(name = "nome", nullable = false)
    private String nome;

    @Size(max = 1000, message = "Descrição deve ter no máximo 1000 caracteres")
    @Column(name = "descricao", columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Preço é obrigatório")
    @DecimalMin(value = "0.01", message = "Preço deve ser maior que zero")
    @Digits(integer = 10, fraction = 2, message = "Preço deve ter no máximo 10 dígitos inteiros e 2 decimais")
    @Column(name = "preco", nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "Quantidade em estoque é obrigatória")
    @Min(value = 0, message = "Quantidade em estoque não pode ser negativa")
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque;

    @NotBlank(message = "SKU é obrigatório")
    @Size(min = 3, max = 50, message = "SKU deve ter entre 3 e 50 caracteres")
    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @NotBlank(message = "Categoria é obrigatória")
    @Size(min = 2, max = 100, message = "Categoria deve ter entre 2 e 100 caracteres")
    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "marca")
    @Size(max = 100, message = "Marca deve ter no máximo 100 caracteres")
    private String marca;

    @Column(name = "peso_kg")
    @DecimalMin(value = "0.001", message = "Peso deve ser maior que zero")
    @Digits(integer = 5, fraction = 3, message = "Peso deve ter no máximo 5 dígitos inteiros e 3 decimais")
    private BigDecimal pesoKg;

    @Column(name = "dimensoes")
    @Size(max = 100, message = "Dimensões devem ter no máximo 100 caracteres")
    private String dimensoes;

    @Column(name = "cor")
    @Size(max = 50, message = "Cor deve ter no máximo 50 caracteres")
    private String cor;

    @Column(name = "tamanho")
    @Size(max = 20, message = "Tamanho deve ter no máximo 20 caracteres")
    private String tamanho;

    @Builder.Default
    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true;

    @Builder.Default
    @Column(name = "disponivel_venda", nullable = false)
    private Boolean disponivelVenda = true;

    @Column(name = "url_imagem")
    @Size(max = 500, message = "URL da imagem deve ter no máximo 500 caracteres")
    private String urlImagem;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao", nullable = false)
    private LocalDateTime dataAtualizacao;

    @NotBlank(message = "ID do fornecedor é obrigatório")
    @Column(name = "fornecedor_id", nullable = false)
    private String fornecedorId;

    /**
     * Verifica se o produto está disponível para venda
     */
    public boolean isDisponivelParaVenda() {
        return ativo && disponivelVenda && quantidadeEstoque > 0;
    }

    /**
     * Reduz a quantidade em estoque
     */
    public void reduzirEstoque(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        if (this.quantidadeEstoque < quantidade) {
            throw new IllegalArgumentException("Estoque insuficiente");
        }
        this.quantidadeEstoque -= quantidade;
    }

    /**
     * Aumenta a quantidade em estoque
     */
    public void aumentarEstoque(Integer quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        }
        this.quantidadeEstoque += quantidade;
    }
}
