package br.com.comprae.produto.infraestrutura.repositorios;

import br.com.comprae.produto.dominio.entidades.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositório para operações de dados da entidade Produto
 */
@Repository
public interface ProdutoRepositorio extends JpaRepository<Produto, UUID> {

    /**
     * Busca produto por SKU
     */
    Optional<Produto> findBySku(String sku);

    /**
     * Verifica se existe produto com o SKU informado
     */
    boolean existsBySku(String sku);

    /**
     * Busca produtos por categoria
     */
    Page<Produto> findByCategoriaIgnoreCase(String categoria, Pageable pageable);

    /**
     * Busca produtos por marca
     */
    Page<Produto> findByMarcaIgnoreCase(String marca, Pageable pageable);

    /**
     * Busca produtos ativos
     */
    Page<Produto> findByAtivoTrue(Pageable pageable);

    /**
     * Busca produtos disponíveis para venda
     */
    @Query("SELECT p FROM Produto p WHERE p.ativo = true AND p.disponivelVenda = true AND p.quantidadeEstoque > 0")
    Page<Produto> findProdutosDisponiveisParaVenda(Pageable pageable);

    /**
     * Busca produtos por fornecedor
     */
    Page<Produto> findByFornecedorId(String fornecedorId, Pageable pageable);

    /**
     * Busca produtos por nome (contém texto)
     */
    @Query("SELECT p FROM Produto p WHERE UPPER(p.nome) LIKE UPPER(CONCAT('%', :nome, '%'))")
    Page<Produto> findByNomeContainingIgnoreCase(@Param("nome") String nome, Pageable pageable);

    /**
     * Busca produtos por faixa de preço
     */
    Page<Produto> findByPrecoBetween(BigDecimal precoMinimo, BigDecimal precoMaximo, Pageable pageable);

    /**
     * Busca produtos com estoque baixo
     */
    @Query("SELECT p FROM Produto p WHERE p.quantidadeEstoque <= :limiteEstoque AND p.ativo = true")
    List<Produto> findProdutosComEstoqueBaixo(@Param("limiteEstoque") Integer limiteEstoque);

    /**
     * Busca produtos por múltiplos filtros
     */
    @Query("SELECT p FROM Produto p WHERE " +
           "(:categoria IS NULL OR UPPER(p.categoria) = UPPER(:categoria)) AND " +
           "(:marca IS NULL OR UPPER(p.marca) = UPPER(:marca)) AND " +
           "(:precoMinimo IS NULL OR p.preco >= :precoMinimo) AND " +
           "(:precoMaximo IS NULL OR p.preco <= :precoMaximo) AND " +
           "(:ativo IS NULL OR p.ativo = :ativo) AND " +
           "(:disponivelVenda IS NULL OR p.disponivelVenda = :disponivelVenda)")
    Page<Produto> findComFiltros(
        @Param("categoria") String categoria,
        @Param("marca") String marca,
        @Param("precoMinimo") BigDecimal precoMinimo,
        @Param("precoMaximo") BigDecimal precoMaximo,
        @Param("ativo") Boolean ativo,
        @Param("disponivelVenda") Boolean disponivelVenda,
        Pageable pageable
    );

    /**
     * Busca textual em nome e descrição
     */
    @Query("SELECT p FROM Produto p WHERE " +
           "UPPER(p.nome) LIKE UPPER(CONCAT('%', :texto, '%')) OR " +
           "UPPER(p.descricao) LIKE UPPER(CONCAT('%', :texto, '%'))")
    Page<Produto> buscarPorTexto(@Param("texto") String texto, Pageable pageable);
}
