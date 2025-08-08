package br.com.comprae.produto.aplicacao.servicos;

import br.com.comprae.produto.aplicacao.dtos.*;
import br.com.comprae.produto.aplicacao.mappers.ProdutoMapper;
import br.com.comprae.produto.dominio.entidades.Produto;
import br.com.comprae.produto.infraestrutura.repositorios.ProdutoRepositorio;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Serviço de negócio para operações com produtos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProdutoService {

    private final ProdutoRepositorio produtoRepositorio;
    private final ProdutoMapper produtoMapper;

    /**
     * Cria um novo produto
     */
    @Transactional
    public ProdutoDto criarProduto(CriarProdutoDto criarProdutoDto) {
        log.info("Criando novo produto com SKU: {}", criarProdutoDto.getSku());

        // Verifica se já existe produto com o mesmo SKU
        if (produtoRepositorio.existsBySku(criarProdutoDto.getSku())) {
            throw new IllegalArgumentException("Já existe um produto com o SKU: " + criarProdutoDto.getSku());
        }

        Produto produto = produtoMapper.toEntity(criarProdutoDto);
        Produto produtoSalvo = produtoRepositorio.save(produto);

        log.info("Produto criado com sucesso. ID: {}, SKU: {}", produtoSalvo.getId(), produtoSalvo.getSku());
        return produtoMapper.toDto(produtoSalvo);
    }

    /**
     * Busca produto por ID
     */
    @Transactional(readOnly = true)
    public ProdutoDto buscarPorId(UUID id) {
        log.debug("Buscando produto por ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        return produtoMapper.toDto(produto);
    }

    /**
     * Busca produto por SKU
     */
    @Transactional(readOnly = true)
    public ProdutoDto buscarPorSku(String sku) {
        log.debug("Buscando produto por SKU: {}", sku);

        Produto produto = produtoRepositorio.findBySku(sku)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com SKU: " + sku));

        return produtoMapper.toDto(produto);
    }

    /**
     * Lista todos os produtos com paginação
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> listarProdutos(Pageable pageable) {
        log.debug("Listando produtos com paginação: {}", pageable);

        return produtoRepositorio.findAll(pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Busca produtos com filtros
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarComFiltros(FiltroProdutoDto filtros, Pageable pageable) {
        log.debug("Buscando produtos com filtros: {}", filtros);

        // Se houver busca textual, usa busca específica
        if (filtros.getTexto() != null && !filtros.getTexto().trim().isEmpty()) {
            return produtoRepositorio.buscarPorTexto(filtros.getTexto().trim(), pageable)
                    .map(produtoMapper::toDto);
        }

        // Caso contrário, usa busca com filtros específicos
        return produtoRepositorio.findComFiltros(
                filtros.getCategoria(),
                filtros.getMarca(),
                filtros.getPrecoMinimo(),
                filtros.getPrecoMaximo(),
                filtros.getAtivo(),
                filtros.getDisponivelVenda(),
                pageable
        ).map(produtoMapper::toDto);
    }

    /**
     * Busca produtos disponíveis para venda
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarDisponiveis(Pageable pageable) {
        log.debug("Buscando produtos disponíveis para venda");

        return produtoRepositorio.findProdutosDisponiveisParaVenda(pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Busca produtos por fornecedor
     */
    @Transactional(readOnly = true)
    public Page<ProdutoDto> buscarPorFornecedor(String fornecedorId, Pageable pageable) {
        log.debug("Buscando produtos do fornecedor: {}", fornecedorId);

        return produtoRepositorio.findByFornecedorId(fornecedorId, pageable)
                .map(produtoMapper::toDto);
    }

    /**
     * Atualiza um produto
     */
    @Transactional
    public ProdutoDto atualizarProduto(UUID id, AtualizarProdutoDto atualizarProdutoDto) {
        log.info("Atualizando produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produtoMapper.updateEntity(produto, atualizarProdutoDto);
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Produto atualizado com sucesso. ID: {}", produtoAtualizado.getId());
        return produtoMapper.toDto(produtoAtualizado);
    }

    /**
     * Remove um produto (soft delete)
     */
    @Transactional
    public void removerProduto(UUID id) {
        log.info("Removendo produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.setAtivo(false);
        produto.setDisponivelVenda(false);
        produtoRepositorio.save(produto);

        log.info("Produto removido com sucesso. ID: {}", id);
    }

    /**
     * Adiciona estoque a um produto
     */
    @Transactional
    public ProdutoDto adicionarEstoque(UUID id, EstoqueDto estoqueDto) {
        log.info("Adicionando {} unidades ao estoque do produto ID: {}", estoqueDto.getQuantidade(), id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.aumentarEstoque(estoqueDto.getQuantidade());
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Estoque atualizado. Produto ID: {}, Nova quantidade: {}", 
                id, produtoAtualizado.getQuantidadeEstoque());
        
        return produtoMapper.toDto(produtoAtualizado);
    }

    /**
     * Remove estoque de um produto
     */
    @Transactional
    public ProdutoDto removerEstoque(UUID id, EstoqueDto estoqueDto) {
        log.info("Removendo {} unidades do estoque do produto ID: {}", estoqueDto.getQuantidade(), id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.reduzirEstoque(estoqueDto.getQuantidade());
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Estoque atualizado. Produto ID: {}, Nova quantidade: {}", 
                id, produtoAtualizado.getQuantidadeEstoque());
        
        return produtoMapper.toDto(produtoAtualizado);
    }

    /**
     * Lista produtos com estoque baixo
     */
    @Transactional(readOnly = true)
    public List<ProdutoDto> listarProdutosComEstoqueBaixo(Integer limiteEstoque) {
        log.debug("Buscando produtos com estoque baixo. Limite: {}", limiteEstoque);

        return produtoRepositorio.findProdutosComEstoqueBaixo(limiteEstoque)
                .stream()
                .map(produtoMapper::toDto)
                .toList();
    }

    /**
     * Ativa um produto
     */
    @Transactional
    public ProdutoDto ativarProduto(UUID id) {
        log.info("Ativando produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.setAtivo(true);
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Produto ativado com sucesso. ID: {}", id);
        return produtoMapper.toDto(produtoAtualizado);
    }

    /**
     * Desativa um produto
     */
    @Transactional
    public ProdutoDto desativarProduto(UUID id) {
        log.info("Desativando produto ID: {}", id);

        Produto produto = produtoRepositorio.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado com ID: " + id));

        produto.setAtivo(false);
        produto.setDisponivelVenda(false);
        Produto produtoAtualizado = produtoRepositorio.save(produto);

        log.info("Produto desativado com sucesso. ID: {}", id);
        return produtoMapper.toDto(produtoAtualizado);
    }
}
