package com.lsfashioncloset.service;

import com.lsfashioncloset.dto.CartItemRequest;
import com.lsfashioncloset.dto.CheckoutResponse;
import com.lsfashioncloset.dto.CreateCheckoutRequest;
import com.lsfashioncloset.model.OrderItem;
import com.lsfashioncloset.model.OrderStatus;
import com.lsfashioncloset.model.Product;
import com.lsfashioncloset.model.StoreOrder;
import com.lsfashioncloset.repository.ProductRepository;
import com.lsfashioncloset.repository.StoreOrderRepository;
import com.mercadopago.resources.preference.Preference;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final StoreOrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final MercadoPagoCheckoutService checkoutService;

    public OrderService(
        StoreOrderRepository orderRepository,
        ProductRepository productRepository,
        MercadoPagoCheckoutService checkoutService
    ) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.checkoutService = checkoutService;
    }

    public List<StoreOrder> list() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    public StoreOrder find(Long id) {
        return orderRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Venda nao encontrada"));
    }

    @Transactional
    public CheckoutResponse createCheckout(CreateCheckoutRequest request) throws Exception {
        StoreOrder order = new StoreOrder();
        order.setCustomerName(request.customerName());
        order.setCustomerEmail(request.customerEmail());
        order.setCustomerPhone(request.customerPhone());
        order.setCustomerDocument(request.customerDocument());
        order.setShippingZipCode(request.shippingZipCode());
        order.setShippingStreet(request.shippingStreet());
        order.setShippingNumber(request.shippingNumber());
        order.setShippingComplement(request.shippingComplement());
        order.setShippingNeighborhood(request.shippingNeighborhood());
        order.setShippingCity(request.shippingCity());
        order.setShippingState(request.shippingState());
        order.setShippingAddress(formatAddress(request));
        order.setStatus(OrderStatus.PENDING);

        BigDecimal total = BigDecimal.ZERO;
        for (CartItemRequest cartItem : request.items()) {
            Product product = productRepository.findById(cartItem.productId())
                .orElseThrow(() -> new EntityNotFoundException("Produto nao encontrado: " + cartItem.productId()));

            if (!product.isActive()) {
                throw new IllegalArgumentException("Produto indisponivel: " + product.getName());
            }
            if (product.getStock() < cartItem.quantity()) {
                throw new IllegalArgumentException("Estoque insuficiente para " + product.getName());
            }

            product.setStock(product.getStock() - cartItem.quantity());
            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setQuantity(cartItem.quantity());
            item.setUnitPrice(product.getPrice());
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(cartItem.quantity())));
            order.addItem(item);
            total = total.add(item.getSubtotal());
        }

        order.setTotal(total);
        StoreOrder saved = orderRepository.save(order);
        Preference preference = checkoutService.createPreference(saved);

        saved.setMercadoPagoPreferenceId(preference.getId());
        saved.setCheckoutUrl(preference.getInitPoint() != null ? preference.getInitPoint() : preference.getSandboxInitPoint());
        saved.setStatus(OrderStatus.WAITING_PAYMENT);

        return new CheckoutResponse(saved.getId(), saved.getTotal(), saved.getMercadoPagoPreferenceId(), saved.getCheckoutUrl());
    }

    @Transactional
    public StoreOrder updateStatus(Long id, OrderStatus status) {
        StoreOrder order = find(id);
        order.setStatus(status);
        return order;
    }

    @Transactional
    public StoreOrder registerPaymentResult(Long orderId, String paymentId, String status) {
        StoreOrder order = find(orderId);
        order.setPaymentId(paymentId);
        if ("approved".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.PAID);
        } else if ("rejected".equalsIgnoreCase(status) || "cancelled".equalsIgnoreCase(status) || "canceled".equalsIgnoreCase(status)) {
            order.setStatus(OrderStatus.CANCELED);
        }
        return order;
    }

    public void delete(Long id) {
        orderRepository.delete(find(id));
    }

    private String formatAddress(CreateCheckoutRequest request) {
        String complement = request.shippingComplement() == null || request.shippingComplement().isBlank()
            ? ""
            : ", " + request.shippingComplement();
        return "%s, %s%s - %s, %s/%s - CEP %s".formatted(
            request.shippingStreet(),
            request.shippingNumber(),
            complement,
            request.shippingNeighborhood(),
            request.shippingCity(),
            request.shippingState(),
            request.shippingZipCode()
        );
    }
}
