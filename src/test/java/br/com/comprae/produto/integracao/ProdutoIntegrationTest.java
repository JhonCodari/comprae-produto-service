package br.com.comprae.produto.integracao;

import br.com.comprae.produto.aplicacao.dtos.CriarProdutoDto;
import br.com.comprae.produto.dominio.entidades.Produto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração da aplicação
 */
@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("Testes de Integração do Produto Service")
class ProdutoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Test
    @DisplayName("Deve criar produto e persistir no banco")
    void deveCriarProdutoEPersistirNoBanco() throws Exception {
        // Arrange
        CriarProdutoDto criarDto = CriarProdutoDto.builder()
                .nome("Produto Integração Teste")
                .descricao("Descrição do produto de integração")
                .preco(BigDecimal.valueOf(149.99))
                .quantidadeEstoque(25)
                .sku("INTEG-001")
                .categoria("Categoria Integração")
                .marca("Marca Teste")
                .fornecedorId("fornecedor-integracao")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/produtos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(criarDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Produto Integração Teste"))
                .andExpect(jsonPath("$.sku").value("INTEG-001"))
                .andExpect(jsonPath("$.preco").value(149.99))
                .andExpect(jsonPath("$.quantidadeEstoque").value(25));

        // Verificar se foi persistido no banco
        Produto produtoSalvo = produtoRepositorio.findBySku("INTEG-001").orElse(null);
        assert produtoSalvo != null;
        assert produtoSalvo.getNome().equals("Produto Integração Teste");
        assert produtoSalvo.getPreco().compareTo(BigDecimal.valueOf(149.99)) == 0;
    }
}
