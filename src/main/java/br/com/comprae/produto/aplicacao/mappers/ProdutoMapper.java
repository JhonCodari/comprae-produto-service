package br.com.comprae.produto.aplicacao.mappers;

import br.com.comprae.produto.aplicacao.dtos.AtualizarProdutoDto;
import br.com.comprae.produto.aplicacao.dtos.CriarProdutoDto;
import br.com.comprae.produto.aplicacao.dtos.ProdutoDto;
import br.com.comprae.produto.dominio.entidades.Produto;
import org.springframework.stereotype.Component;

/**
 * Mapper para conversões entre DTOs e entidade Produto
 */
@Component
public class ProdutoMapper {

    /**
     * Converte CriarProdutoDto para entidade Produto
     */
    public Produto toEntity(CriarProdutoDto dto) {
        return Produto.builder()
                .nome(dto.getNome())
                .descricao(dto.getDescricao())
                .preco(dto.getPreco())
                .quantidadeEstoque(dto.getQuantidadeEstoque())
                .sku(dto.getSku())
                .categoria(dto.getCategoria())
                .marca(dto.getMarca())
                .pesoKg(dto.getPesoKg())
                .dimensoes(dto.getDimensoes())
                .cor(dto.getCor())
                .tamanho(dto.getTamanho())
                .ativo(dto.getAtivo())
                .disponivelVenda(dto.getDisponivelVenda())
                .urlImagem(dto.getUrlImagem())
                .fornecedorId(dto.getFornecedorId())
                .build();
    }

    /**
     * Converte entidade Produto para ProdutoDto
     */
    public ProdutoDto toDto(Produto produto) {
        return ProdutoDto.builder()
                .id(produto.getId())
                .nome(produto.getNome())
                .descricao(produto.getDescricao())
                .preco(produto.getPreco())
                .quantidadeEstoque(produto.getQuantidadeEstoque())
                .sku(produto.getSku())
                .categoria(produto.getCategoria())
                .marca(produto.getMarca())
                .pesoKg(produto.getPesoKg())
                .dimensoes(produto.getDimensoes())
                .cor(produto.getCor())
                .tamanho(produto.getTamanho())
                .ativo(produto.getAtivo())
                .disponivelVenda(produto.getDisponivelVenda())
                .urlImagem(produto.getUrlImagem())
                .dataCriacao(produto.getDataCriacao())
                .dataAtualizacao(produto.getDataAtualizacao())
                .fornecedorId(produto.getFornecedorId())
                .disponivelParaVenda(produto.isDisponivelParaVenda())
                .build();
    }

    /**
     * Atualiza entidade Produto com dados do AtualizarProdutoDto
     * Apenas campos não nulos serão atualizados
     */
    public void updateEntity(Produto produto, AtualizarProdutoDto dto) {
        if (dto.getNome() != null) {
            produto.setNome(dto.getNome());
        }
        if (dto.getDescricao() != null) {
            produto.setDescricao(dto.getDescricao());
        }
        if (dto.getPreco() != null) {
            produto.setPreco(dto.getPreco());
        }
        if (dto.getQuantidadeEstoque() != null) {
            produto.setQuantidadeEstoque(dto.getQuantidadeEstoque());
        }
        if (dto.getCategoria() != null) {
            produto.setCategoria(dto.getCategoria());
        }
        if (dto.getMarca() != null) {
            produto.setMarca(dto.getMarca());
        }
        if (dto.getPesoKg() != null) {
            produto.setPesoKg(dto.getPesoKg());
        }
        if (dto.getDimensoes() != null) {
            produto.setDimensoes(dto.getDimensoes());
        }
        if (dto.getCor() != null) {
            produto.setCor(dto.getCor());
        }
        if (dto.getTamanho() != null) {
            produto.setTamanho(dto.getTamanho());
        }
        if (dto.getAtivo() != null) {
            produto.setAtivo(dto.getAtivo());
        }
        if (dto.getDisponivelVenda() != null) {
            produto.setDisponivelVenda(dto.getDisponivelVenda());
        }
        if (dto.getUrlImagem() != null) {
            produto.setUrlImagem(dto.getUrlImagem());
        }
        if (dto.getFornecedorId() != null) {
            produto.setFornecedorId(dto.getFornecedorId());
        }
    }
}
