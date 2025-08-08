package br.com.comprae.produto.apresentacao.excecoes;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Tratador global de exceções para a API
 */
@RestControllerAdvice
@Slf4j
public class TratadorGlobalExcecoes {

    /**
     * Trata exceções de validação de argumentos
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> tratarErrosValidacao(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        log.warn("Erro de validação: {}", ex.getMessage());

        List<String> detalhes = new ArrayList<>();
        for (FieldError erro : ex.getBindingResult().getFieldErrors()) {
            detalhes.add(erro.getField() + ": " + erro.getDefaultMessage());
        }

        ErroResposta erro = ErroResposta.criar(
                HttpStatus.BAD_REQUEST.value(),
                "Erro de Validação",
                "Os dados fornecidos são inválidos",
                request.getDescription(false).replace("uri=", ""),
                detalhes
        );

        return ResponseEntity.badRequest().body(erro);
    }

    /**
     * Trata exceções de argumentos ilegais
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResposta> tratarArgumentoIlegal(
            IllegalArgumentException ex, WebRequest request) {
        
        log.warn("Argumento ilegal: {}", ex.getMessage());

        ErroResposta erro = ErroResposta.criar(
                HttpStatus.BAD_REQUEST.value(),
                "Argumento Inválido",
                ex.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.badRequest().body(erro);
    }

    /**
     * Trata exceções gerais não capturadas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> tratarErroGeral(
            Exception ex, WebRequest request) {
        
        log.error("Erro interno do servidor: ", ex);

        ErroResposta erro = ErroResposta.criar(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro Interno do Servidor",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.internalServerError().body(erro);
    }

    /**
     * Trata exceções de runtime
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResposta> tratarErroRuntime(
            RuntimeException ex, WebRequest request) {
        
        log.error("Erro de runtime: ", ex);

        ErroResposta erro = ErroResposta.criar(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Erro de Runtime",
                "Ocorreu um erro durante o processamento da solicitação",
                request.getDescription(false).replace("uri=", "")
        );

        return ResponseEntity.internalServerError().body(erro);
    }
}
