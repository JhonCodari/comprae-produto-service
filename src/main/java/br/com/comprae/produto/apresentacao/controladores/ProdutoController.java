package br.com.comprae.produto.apresentacao.controladores;

import br.com.comprae.produto.aplicacao.dtos.*;
import br.com.comprae.produto.aplicacao.servicos.ProdutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador REST para operações com produtos
 */
@RestController
@RequestMapping("/api/v1/produtos")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Produtos", description = "API para gerenciamento de produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    @Operation(summary = "Criar novo produto", description = "Cria um novo produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos"),
        @ApiResponse(responseCode = "409", description = "SKU já existe")
    })
    @PostMapping
    public ResponseEntity<ProdutoDto> criarProduto(
            @Valid @RequestBody CriarProdutoDto criarProdutoDto) {
        
        log.info("Recebida solicitação para criar produto com SKU: {}", criarProdutoDto.getSku());
        ProdutoDto produto = produtoService.criarProduto(criarProdutoDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(produto);
    }

    @Operation(summary = "Buscar produto por ID", description = "Busca um produto pelo seu identificador único")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProdutoDto> buscarPorId(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        
        log.debug("Buscando produto por ID: {}", id);
        ProdutoDto produto = produtoService.buscarPorId(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Buscar produto por SKU", description = "Busca um produto pelo seu código SKU")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @GetMapping("/sku/{sku}")
    public ResponseEntity<ProdutoDto> buscarPorSku(
            @Parameter(description = "SKU do produto") @PathVariable String sku) {
        
        log.debug("Buscando produto por SKU: {}", sku);
        ProdutoDto produto = produtoService.buscarPorSku(sku);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Listar produtos", description = "Lista todos os produtos com paginação")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de produtos retornada com sucesso")
    })
    @GetMapping
    public ResponseEntity<Page<ProdutoDto>> listarProdutos(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Listando produtos com paginação: {}", pageable);
        Page<ProdutoDto> produtos = produtoService.listarProdutos(pageable);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Buscar produtos com filtros", description = "Busca produtos aplicando filtros específicos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos encontrados")
    })
    @GetMapping("/buscar")
    public ResponseEntity<Page<ProdutoDto>> buscarComFiltros(
            @Parameter(description = "Categoria do produto") @RequestParam(required = false) String categoria,
            @Parameter(description = "Marca do produto") @RequestParam(required = false) String marca,
            @Parameter(description = "Preço mínimo") @RequestParam(required = false) BigDecimal precoMinimo,
            @Parameter(description = "Preço máximo") @RequestParam(required = false) BigDecimal precoMaximo,
            @Parameter(description = "Produto ativo") @RequestParam(required = false) Boolean ativo,
            @Parameter(description = "Disponível para venda") @RequestParam(required = false) Boolean disponivelVenda,
            @Parameter(description = "ID do fornecedor") @RequestParam(required = false) String fornecedorId,
            @Parameter(description = "Texto para busca em nome e descrição") @RequestParam(required = false) String texto,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        FiltroProdutoDto filtros = FiltroProdutoDto.builder()
                .categoria(categoria)
                .marca(marca)
                .precoMinimo(precoMinimo)
                .precoMaximo(precoMaximo)
                .ativo(ativo)
                .disponivelVenda(disponivelVenda)
                .fornecedorId(fornecedorId)
                .texto(texto)
                .build();
        
        log.debug("Buscando produtos com filtros: {}", filtros);
        Page<ProdutoDto> produtos = produtoService.buscarComFiltros(filtros, pageable);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Listar produtos disponíveis", description = "Lista produtos disponíveis para venda")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos disponíveis retornados")
    })
    @GetMapping("/disponiveis")
    public ResponseEntity<Page<ProdutoDto>> buscarDisponiveis(
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Buscando produtos disponíveis para venda");
        Page<ProdutoDto> produtos = produtoService.buscarDisponiveis(pageable);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Buscar produtos por fornecedor", description = "Lista produtos de um fornecedor específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos do fornecedor retornados")
    })
    @GetMapping("/fornecedor/{fornecedorId}")
    public ResponseEntity<Page<ProdutoDto>> buscarPorFornecedor(
            @Parameter(description = "ID do fornecedor") @PathVariable String fornecedorId,
            @PageableDefault(size = 20, sort = "nome", direction = Sort.Direction.ASC) Pageable pageable) {
        
        log.debug("Buscando produtos do fornecedor: {}", fornecedorId);
        Page<ProdutoDto> produtos = produtoService.buscarPorFornecedor(fornecedorId, pageable);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Atualizar produto", description = "Atualiza os dados de um produto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProdutoDto> atualizarProduto(
            @Parameter(description = "ID do produto") @PathVariable UUID id,
            @Valid @RequestBody AtualizarProdutoDto atualizarProdutoDto) {
        
        log.info("Atualizando produto ID: {}", id);
        ProdutoDto produto = produtoService.atualizarProduto(id, atualizarProdutoDto);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Remover produto", description = "Remove (desativa) um produto do sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Produto removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerProduto(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        
        log.info("Removendo produto ID: {}", id);
        produtoService.removerProduto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Adicionar estoque", description = "Adiciona quantidade ao estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque adicionado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida")
    })
    @PostMapping("/{id}/estoque/adicionar")
    public ResponseEntity<ProdutoDto> adicionarEstoque(
            @Parameter(description = "ID do produto") @PathVariable UUID id,
            @Valid @RequestBody EstoqueDto estoqueDto) {
        
        log.info("Adicionando estoque ao produto ID: {}", id);
        ProdutoDto produto = produtoService.adicionarEstoque(id, estoqueDto);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Remover estoque", description = "Remove quantidade do estoque de um produto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estoque removido com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado"),
        @ApiResponse(responseCode = "400", description = "Quantidade inválida ou estoque insuficiente")
    })
    @PostMapping("/{id}/estoque/remover")
    public ResponseEntity<ProdutoDto> removerEstoque(
            @Parameter(description = "ID do produto") @PathVariable UUID id,
            @Valid @RequestBody EstoqueDto estoqueDto) {
        
        log.info("Removendo estoque do produto ID: {}", id);
        ProdutoDto produto = produtoService.removerEstoque(id, estoqueDto);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Produtos com estoque baixo", description = "Lista produtos com estoque abaixo do limite")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produtos com estoque baixo retornados")
    })
    @GetMapping("/estoque-baixo")
    public ResponseEntity<List<ProdutoDto>> listarProdutosComEstoqueBaixo(
            @Parameter(description = "Limite de estoque") @RequestParam(defaultValue = "10") Integer limite) {
        
        log.debug("Buscando produtos com estoque baixo. Limite: {}", limite);
        List<ProdutoDto> produtos = produtoService.listarProdutosComEstoqueBaixo(limite);
        return ResponseEntity.ok(produtos);
    }

    @Operation(summary = "Ativar produto", description = "Ativa um produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto ativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/ativar")
    public ResponseEntity<ProdutoDto> ativarProduto(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        
        log.info("Ativando produto ID: {}", id);
        ProdutoDto produto = produtoService.ativarProduto(id);
        return ResponseEntity.ok(produto);
    }

    @Operation(summary = "Desativar produto", description = "Desativa um produto no sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Produto desativado com sucesso"),
        @ApiResponse(responseCode = "404", description = "Produto não encontrado")
    })
    @PatchMapping("/{id}/desativar")
    public ResponseEntity<ProdutoDto> desativarProduto(
            @Parameter(description = "ID do produto") @PathVariable UUID id) {
        
        log.info("Desativando produto ID: {}", id);
        ProdutoDto produto = produtoService.desativarProduto(id);
        return ResponseEntity.ok(produto);
    }
}
