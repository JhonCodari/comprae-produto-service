package br.com.comprae.produto.integracao;

import br.com.comprae.produto.dominio.entidades.Produto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de integração focado na camada de persistência
 * Testa operações de banco de dados sem dependências externas
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Testes de Integração - Camada de Persistência")
class ProdutoPersistenceIntegrationTest {

    @Autowired
    private ProdutoRepositorio produtoRepositorio;

    @Test
    @DisplayName("Deve salvar e recuperar produto do banco de dados")
    void deveSalvarERecuperarProduto() {
        // Arrange
        Produto produto = Produto.builder()
                .nome("Produto Integração")
                .descricao("Descrição do produto de teste")
                .preco(new BigDecimal("99.99"))
                .sku("INT-001")
                .categoria("CATEGORIA_TESTE")
                .quantidadeEstoque(5)
                .fornecedorId("FORNECEDOR-TEST-001")
                .build();

        // Act
        Produto produtoSalvo = produtoRepositorio.save(produto);
        Optional<Produto> produtoRecuperado = produtoRepositorio.findById(produtoSalvo.getId());

        // Assert
        assertThat(produtoSalvo).isNotNull();
        assertThat(produtoSalvo.getId()).isNotNull();
        assertThat(produtoRecuperado).isPresent();
        assertThat(produtoRecuperado.get().getNome()).isEqualTo("Produto Integração");
        assertThat(produtoRecuperado.get().getSku()).isEqualTo("INT-001");
        assertThat(produtoRecuperado.get().getPreco()).isEqualByComparingTo(new BigDecimal("99.99"));
    }

    @Test
    @DisplayName("Deve buscar produto por SKU")
    void deveBuscarProdutoPorSku() {
        // Arrange
        Produto produto = Produto.builder()
                .nome("Produto SKU Test")
                .descricao("Produto para teste de busca por SKU")
                .preco(new BigDecimal("150.50"))
                .sku("SKU-TEST-001")
                .categoria("SKU_CATEGORIA")
                .quantidadeEstoque(10)
                .fornecedorId("FORNECEDOR-SKU-001")
                .build();

        produtoRepositorio.save(produto);

        // Act
        Optional<Produto> produtoEncontrado = produtoRepositorio.findBySku("SKU-TEST-001");

        // Assert
        assertThat(produtoEncontrado).isPresent();
        assertThat(produtoEncontrado.get().getNome()).isEqualTo("Produto SKU Test");
        assertThat(produtoEncontrado.get().getSku()).isEqualTo("SKU-TEST-001");
    }

    @Test
    @DisplayName("Deve listar produtos por categoria")
    void deveListarProdutosPorCategoria() {
        // Arrange
        Produto produto1 = Produto.builder()
                .nome("Produto Cat 1")
                .descricao("Primeiro produto da categoria")
                .preco(new BigDecimal("100.00"))
                .sku("CAT-001")
                .categoria("ELETRÔNICOS")
                .quantidadeEstoque(3)
                .fornecedorId("FORNECEDOR-CAT-001")
                .build();

        Produto produto2 = Produto.builder()
                .nome("Produto Cat 2")
                .descricao("Segundo produto da categoria")
                .preco(new BigDecimal("200.00"))
                .sku("CAT-002")
                .categoria("ELETRÔNICOS")
                .quantidadeEstoque(7)
                .fornecedorId("FORNECEDOR-CAT-002")
                .build();

        Produto produto3 = Produto.builder()
                .nome("Produto Outra Cat")
                .descricao("Produto de categoria diferente")
                .preco(new BigDecimal("300.00"))
                .sku("OUTRA-001")
                .categoria("ROUPAS")
                .quantidadeEstoque(2)
                .fornecedorId("FORNECEDOR-ROUPAS-001")
                .build();

        produtoRepositorio.save(produto1);
        produtoRepositorio.save(produto2);
        produtoRepositorio.save(produto3);

        // Act - usando Pageable.unpaged() para buscar todos
        List<Produto> produtosEletronicos = produtoRepositorio.findByCategoriaIgnoreCase("ELETRÔNICOS", 
                Pageable.unpaged()).getContent();

        // Assert
        assertThat(produtosEletronicos).hasSize(2);
        assertThat(produtosEletronicos).extracting(Produto::getNome)
                .containsExactlyInAnyOrder("Produto Cat 1", "Produto Cat 2");
    }

    @Test
    @DisplayName("Deve atualizar quantidade de estoque")
    void deveAtualizarQuantidadeEstoque() {
        // Arrange
        Produto produto = Produto.builder()
                .nome("Produto Estoque")
                .descricao("Produto para teste de estoque")
                .preco(new BigDecimal("50.00"))
                .sku("EST-001")
                .categoria("TESTE_ESTOQUE")
                .quantidadeEstoque(10)
                .fornecedorId("FORNECEDOR-EST-001")
                .build();

        Produto produtoSalvo = produtoRepositorio.save(produto);

        // Act
        produtoSalvo.setQuantidadeEstoque(15);
        Produto produtoAtualizado = produtoRepositorio.save(produtoSalvo);
        Optional<Produto> produtoRecuperado = produtoRepositorio.findById(produtoSalvo.getId());

        // Assert
        assertThat(produtoAtualizado.getQuantidadeEstoque()).isEqualTo(15);
        assertThat(produtoRecuperado).isPresent();
        assertThat(produtoRecuperado.get().getQuantidadeEstoque()).isEqualTo(15);
    }
}