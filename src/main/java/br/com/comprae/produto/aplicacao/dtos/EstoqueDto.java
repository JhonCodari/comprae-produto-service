package br.com.comprae.produto.aplicacao.dtos;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para operações de estoque
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstoqueDto {

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidade;

    private String motivo;
}
