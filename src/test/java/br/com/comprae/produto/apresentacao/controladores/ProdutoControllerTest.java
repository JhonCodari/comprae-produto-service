package br.com.comprae.produto.apresentacao.controladores;

import br.com.comprae.produto.aplicacao.dtos.CriarProdutoDto;
import br.com.comprae.produto.aplicacao.dtos.ProdutoDto;
import br.com.comprae.produto.aplicacao.servicos.ProdutoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração para o controlador de produtos
 */
@WebMvcTest(controllers = ProdutoController.class, 
            excludeAutoConfiguration = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@DisplayName("Testes do Controlador de Produtos")
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() throws Exception {
        // Arrange
        CriarProdutoDto criarDto = CriarProdutoDto.builder()
                .nome("Produto Teste")
                .descricao("Descrição teste")
                .preco(BigDecimal.valueOf(99.99))
                .quantidadeEstoque(10)
                .sku("TESTE-001")
                .categoria("Categoria Teste")
                .fornecedorId("fornecedor-001")
                .build();

        ProdutoDto produtoDto = ProdutoDto.builder()
                .id(UUID.randomUUID())
                .nome("Produto Teste")
                .sku("TESTE-001")
                .build();

        when(produtoService.criarProduto(any(CriarProdutoDto.class))).thenReturn(produtoDto);

        // Act & Assert
        mockMvc.perform(post("/api/v1/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criarDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Produto Teste"))
                .andExpect(jsonPath("$.sku").value("TESTE-001"));
    }

    @Test
    @DisplayName("Deve retornar erro de validação para dados inválidos")
    void deveRetornarErroValidacaoParaDadosInvalidos() throws Exception {
        // Arrange
        CriarProdutoDto criarDto = CriarProdutoDto.builder()
                .nome("") // Nome vazio - inválido
                .preco(BigDecimal.valueOf(-1)) // Preço negativo - inválido
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criarDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() throws Exception {
        // Arrange
        UUID id = UUID.randomUUID();
        ProdutoDto produtoDto = ProdutoDto.builder()
                .id(id)
                .nome("Produto Teste")
                .sku("TESTE-001")
                .build();

        when(produtoService.buscarPorId(id)).thenReturn(produtoDto);

        // Act & Assert
        mockMvc.perform(get("/api/v1/produtos/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.nome").value("Produto Teste"))
                .andExpect(jsonPath("$.sku").value("TESTE-001"));
    }
}
