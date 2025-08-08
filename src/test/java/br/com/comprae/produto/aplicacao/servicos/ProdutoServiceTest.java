package br.com.comprae.produto.aplicacao.servicos;

import br.com.comprae.produto.aplicacao.dtos.CriarProdutoDto;
import br.com.comprae.produto.aplicacao.dtos.ProdutoDto;
import br.com.comprae.produto.aplicacao.mappers.ProdutoMapper;
import br.com.comprae.produto.dominio.entidades.Produto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitários para o serviço de produtos
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Testes do Serviço de Produtos")
class ProdutoServiceTest {

    @Mock
    private ProdutoRepositorio produtoRepositorio;

    @Mock
    private ProdutoMapper produtoMapper;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve criar produto com sucesso")
    void deveCriarProdutoComSucesso() {
        // Arrange
        CriarProdutoDto criarDto = CriarProdutoDto.builder()
                .nome("Produto Teste")
                .descricao("Descrição do produto teste")
                .preco(BigDecimal.valueOf(99.99))
                .quantidadeEstoque(10)
                .sku("TESTE-001")
                .categoria("Categoria Teste")
                .fornecedorId("fornecedor-001")
                .build();

        Produto produto = Produto.builder()
                .id(UUID.randomUUID())
                .nome("Produto Teste")
                .sku("TESTE-001")
                .build();

        ProdutoDto produtoDto = ProdutoDto.builder()
                .id(produto.getId())
                .nome("Produto Teste")
                .sku("TESTE-001")
                .build();

        when(produtoRepositorio.existsBySku("TESTE-001")).thenReturn(false);
        when(produtoMapper.toEntity(criarDto)).thenReturn(produto);
        when(produtoRepositorio.save(any(Produto.class))).thenReturn(produto);
        when(produtoMapper.toDto(produto)).thenReturn(produtoDto);

        // Act
        ProdutoDto resultado = produtoService.criarProduto(criarDto);

        // Assert
        assertNotNull(resultado);
        assertEquals("Produto Teste", resultado.getNome());
        assertEquals("TESTE-001", resultado.getSku());

        verify(produtoRepositorio).existsBySku("TESTE-001");
        verify(produtoRepositorio).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao criar produto com SKU existente")
    void deveLancarExcecaoAoCriarProdutoComSkuExistente() {
        // Arrange
        CriarProdutoDto criarDto = CriarProdutoDto.builder()
                .sku("TESTE-001")
                .build();

        when(produtoRepositorio.existsBySku("TESTE-001")).thenReturn(true);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.criarProduto(criarDto)
        );

        assertEquals("Já existe um produto com o SKU: TESTE-001", exception.getMessage());
        verify(produtoRepositorio, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve buscar produto por ID com sucesso")
    void deveBuscarProdutoPorIdComSucesso() {
        // Arrange
        UUID id = UUID.randomUUID();
        Produto produto = Produto.builder()
                .id(id)
                .nome("Produto Teste")
                .build();

        ProdutoDto produtoDto = ProdutoDto.builder()
                .id(id)
                .nome("Produto Teste")
                .build();

        when(produtoRepositorio.findById(id)).thenReturn(Optional.of(produto));
        when(produtoMapper.toDto(produto)).thenReturn(produtoDto);

        // Act
        ProdutoDto resultado = produtoService.buscarPorId(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        assertEquals("Produto Teste", resultado.getNome());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar produto inexistente por ID")
    void deveLancarExcecaoAoBuscarProdutoInexistentePorId() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(produtoRepositorio.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.buscarPorId(id)
        );

        assertEquals("Produto não encontrado com ID: " + id, exception.getMessage());
    }
}
