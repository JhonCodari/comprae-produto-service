package br.com.comprae.produto.apresentacao.excecoes;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe para padronizar respostas de erro da API
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErroResposta {

    private LocalDateTime timestamp;
    private Integer status;
    private String erro;
    private String mensagem;
    private String caminho;
    private List<String> detalhes;

    public static ErroResposta criar(Integer status, String erro, String mensagem, String caminho) {
        return ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .erro(erro)
                .mensagem(mensagem)
                .caminho(caminho)
                .build();
    }

    public static ErroResposta criar(Integer status, String erro, String mensagem, String caminho, List<String> detalhes) {
        return ErroResposta.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .erro(erro)
                .mensagem(mensagem)
                .caminho(caminho)
                .detalhes(detalhes)
                .build();
    }
}
