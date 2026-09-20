package com.sualoja.api.service;

import com.sualoja.api.dto.response.OrderResponse;
import com.sualoja.api.exception.ResourceNotFoundException;
import com.sualoja.api.model.entity.*;
import com.sualoja.api.model.enums.OrderStatus;
import com.sualoja.api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final UserRepository userRepository;
    private final ProductVariantRepository productVariantRepository;

    // Finaliza o pedido (checkout) - A mágica acontece aqui!
    @Transactional
    public OrderResponse finalizarPedido(Long usuarioId) {
        // 1. Busca o carrinho do usuário
        Cart carrinho = cartRepository.findByUsuarioId(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Carrinho não encontrado"));

        // 2. Verifica se o carrinho não está vazio
        if (carrinho.getItens().isEmpty()) {
            throw new IllegalArgumentException("O carrinho está vazio");
        }

        // 3. Busca o usuário
        User usuario = userRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // 4. Cria o pedido
        Order pedido = Order.builder()
            .usuario(usuario)
            .status(OrderStatus.PENDENTE)
            .build();

        // 5. Adiciona os itens do carrinho ao pedido e dá baixa no estoque
        BigDecimal valorTotal = BigDecimal.ZERO;

        for (CartItem itemCarrinho : carrinho.getItens()) {
            ProductVariant variante = itemCarrinho.getVarianteProduto();

            // Verifica estoque antes de prosseguir
            if (variante.getEstoque() < itemCarrinho.getQuantidade()) {
                throw new IllegalArgumentException(
                    "Estoque insuficiente para o produto: " + variante.getProduto().getNome() + 
                    " - " + variante.getCor() + " Tam " + variante.getTamanho() +
                    ". Disponível: " + variante.getEstoque()
                );
            }

            // Cria o item do pedido (guarda o preço no momento da compra)
            OrderItem itemPedido = OrderItem.builder()
                .pedido(pedido)
                .varianteProduto(variante)
                .quantidade(itemCarrinho.getQuantidade())
                .precoUnitario(variante.getPreco())
                .build();

            pedido.getItens().add(itemPedido);

            // Calcula o subtotal deste item
            BigDecimal subtotal = variante.getPreco().multiply(BigDecimal.valueOf(itemCarrinho.getQuantidade()));
            valorTotal = valorTotal.add(subtotal);

            // DÁ BAIXA NO ESTOQUE (a parte mais importante!)
            variante.setEstoque(variante.getEstoque() - itemCarrinho.getQuantidade());
            productVariantRepository.save(variante);
        }

        // 6. Define o valor total do pedido
        pedido.setValorTotal(valorTotal);

        // 7. Salva o pedido (cascade salva os itens automaticamente)
        Order pedidoSalvo = orderRepository.save(pedido);

        // 8. Limpa o carrinho (remove todos os itens)
        carrinho.getItens().clear();
        cartRepository.save(carrinho);

        // 9. Retorna o pedido criado
        return OrderResponse.deEntidade(pedidoSalvo);
    }

    // Lista todos os pedidos de um usuário específico
    @Transactional(readOnly = true)
    public List<OrderResponse> buscarPedidosPorUsuario(Long usuarioId) {
        return orderRepository.findByUsuarioIdOrderByCriadoEmDesc(usuarioId)
            .stream()
            .map(OrderResponse::deEntidade)
            .toList();
    }

    // Busca um pedido específico por ID
    @Transactional(readOnly = true)
    public OrderResponse buscarPedidoPorId(Long pedidoId) {
        Order pedido = orderRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));
        return OrderResponse.deEntidade(pedido);
    }

    // Atualiza o status do pedido (apenas para ADMIN)
    @Transactional
    public OrderResponse atualizarStatusPedido(Long pedidoId, OrderStatus novoStatus) {
        Order pedido = orderRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado"));

        pedido.setStatus(novoStatus);
        return OrderResponse.deEntidade(orderRepository.save(pedido));
    }
        // Lista todos os pedidos do sistema (para o Admin)
    @Transactional(readOnly = true)
    public List<OrderResponse> buscarTodosPedidos() {
        return orderRepository.findAllByOrderByCriadoEmDesc()
            .stream()
            .map(OrderResponse::deEntidade)
            .toList();
    }
}