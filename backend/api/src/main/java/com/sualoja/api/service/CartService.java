package com.sualoja.api.service;

import com.sualoja.api.dto.request.AddToCartRequest;
import com.sualoja.api.dto.request.UpdateCartItemRequest;
import com.sualoja.api.dto.response.CartResponse;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.Cart;
import com.sualoja.api.model.entity.CartItem;
import com.sualoja.api.model.entity.ProductVariant;
import com.sualoja.api.model.entity.User;
import com.sualoja.api.repository.CartItemRepository;
import com.sualoja.api.repository.CartRepository;
import com.sualoja.api.repository.ProductVariantRepository;
import com.sualoja.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    // Busca ou cria o carrinho do usuário (cada usuário tem apenas 1 carrinho ativo)
    @Transactional
    public CartResponse obterOuCriarCarrinho(Long usuarioId) {
        User usuario = userRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Cart carrinho = cartRepository.findByUsuarioId(usuarioId)
            .orElseGet(() -> {
                // Se não existe carrinho, cria um novo
                Cart novoCarrinho = Cart.builder()
                    .usuario(usuario)
                    .build();
                return cartRepository.save(novoCarrinho);
            });

        return CartResponse.deEntidade(carrinho);
    }

    // Adiciona um produto ao carrinho (ou aumenta quantidade se já existir)
    @Transactional
    public CartResponse adicionarItem(Long usuarioId, AddToCartRequest requisicao) {
        // Verifica se a variação existe
        ProductVariant variante = productVariantRepository.findById(requisicao.varianteId())
            .orElseThrow(() -> new ResourceNotFoundException("Variação do produto não encontrada"));

        // Verifica se há estoque suficiente
        if (variante.getEstoque() < requisicao.quantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente para esta variação. Disponível: " + variante.getEstoque());
        }

        // Busca ou cria o carrinho
        Cart carrinho = cartRepository.findByUsuarioId(usuarioId)
            .orElseGet(() -> {
                User usuario = userRepository.findById(usuarioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
                return cartRepository.save(Cart.builder().usuario(usuario).build());
            });

        // Verifica se o item já existe no carrinho
        Optional<CartItem> itemExistente = cartItemRepository.findByCarrinhoIdAndVarianteProdutoId(
            carrinho.getId(), variante.getId()
        );

        if (itemExistente.isPresent()) {
            // Se já existe, aumenta a quantidade
            CartItem item = itemExistente.get();
            int novaQuantidade = item.getQuantidade() + requisicao.quantidade();
            
            // Verifica estoque novamente com a nova quantidade total
            if (variante.getEstoque() < novaQuantidade) {
                throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + variante.getEstoque());
            }
            
            item.setQuantidade(novaQuantidade);
            cartItemRepository.save(item);
        } else {
            // Se não existe, cria um novo item
            CartItem novoItem = CartItem.builder()
                .carrinho(carrinho)
                .varianteProduto(variante)
                .quantidade(requisicao.quantidade())
                .build();
            cartItemRepository.save(novoItem);
        }

        // Retorna o carrinho atualizado
        return CartResponse.deEntidade(cartRepository.findById(carrinho.getId()).get());
    }

    // Atualiza a quantidade de um item específico
    @Transactional
    public CartResponse atualizarQuantidadeItem(Long usuarioId, Long itemId, UpdateCartItemRequest requisicao) {
        CartItem item = cartItemRepository.findById(itemId)
            .orElseThrow(() -> new ResourceNotFoundException("Item do carrinho não encontrado"));

        // Garante que o item pertence ao carrinho do usuário
        if (!item.getCarrinho().getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("Este item não pertence ao seu carrinho");
        }

        // Verifica estoque
        ProductVariant variante = item.getVarianteProduto();
        if (variante.getEstoque() < requisicao.quantidade()) {
            throw new IllegalArgumentException("Estoque insuficiente. Disponível: " + variante.getEstoque());
        }

        item.setQuantidade(requisicao.quantidade());
        cartItemRepository.save(item);

        return CartResponse.deEntidade(cartRepository.findByUsuarioId(usuarioId).get());
    }

    // Remove um item específico do carrinho
        // Remove um item específico do carrinho
    @Transactional
    public CartResponse removerItem(Long usuarioId, Long itemId) {
        // 1. Busca o carrinho do usuário
        Cart carrinho = cartRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado"));

        // 2. Encontra o item dentro da lista do carrinho
        CartItem item = carrinho.getItens().stream()
            .filter(i -> i.getId().equals(itemId))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Item não encontrado no carrinho"));

        // 3. Remove o item da lista. 
        // Como a entidade Cart tem 'orphanRemoval = true', o Hibernate deletará 
        // o item do banco de dados automaticamente ao salvar o carrinho!
        carrinho.getItens().remove(item);
        
        // 4. Salva o carrinho para persistir a remoção
        cartRepository.save(carrinho);

        return CartResponse.deEntidade(carrinho);
    }

    // Limpa todo o carrinho (remove todos os itens)
    @Transactional
    public void limparCarrinho(Long usuarioId) {
        Cart carrinho = cartRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado"));

        carrinho.getItens().clear();
        cartRepository.save(carrinho);
    }
}