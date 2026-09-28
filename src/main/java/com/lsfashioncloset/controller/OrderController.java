package com.lsfashioncloset.controller;

import com.lsfashioncloset.dto.CheckoutResponse;
import com.lsfashioncloset.dto.CreateCheckoutRequest;
import com.lsfashioncloset.dto.OrderStatusRequest;
import com.lsfashioncloset.model.StoreOrder;
import com.lsfashioncloset.service.MercadoPagoPaymentLookupService;
import com.lsfashioncloset.service.OrderService;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class OrderController {
    private final OrderService orderService;
    private final MercadoPagoPaymentLookupService paymentLookupService;

    public OrderController(OrderService orderService, MercadoPagoPaymentLookupService paymentLookupService) {
        this.orderService = orderService;
        this.paymentLookupService = paymentLookupService;
    }

    @PostMapping("/checkout")
    @ResponseStatus(HttpStatus.CREATED)
    public CheckoutResponse checkout(@Valid @RequestBody CreateCheckoutRequest request) throws Exception {
        return orderService.createCheckout(request);
    }

    @GetMapping("/admin/orders")
    public List<StoreOrder> list() {
        return orderService.list();
    }

    @PatchMapping("/admin/orders/{id}/status")
    public StoreOrder updateStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusRequest request) {
        return orderService.updateStatus(id, request.status());
    }

    @DeleteMapping("/admin/orders/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        orderService.delete(id);
    }

    @PostMapping("/payments/webhook")
    public Map<String, String> webhook(
        @RequestParam Map<String, String> params,
        @RequestBody(required = false) Map<String, Object> body
    ) {
        String paymentId = extractPaymentId(params, body);
        if (paymentId != null) {
            MercadoPagoPaymentLookupService.PaymentResult result = paymentLookupService.findPayment(paymentId);
            if (result.orderId() != null) {
                orderService.registerPaymentResult(result.orderId(), result.paymentId(), result.status());
            }
        }
        return Map.of("received", "true");
    }

    @SuppressWarnings("unchecked")
    private String extractPaymentId(Map<String, String> params, Map<String, Object> body) {
        if (params.containsKey("id")) {
            return params.get("id");
        }
        if (params.containsKey("data.id")) {
            return params.get("data.id");
        }
        if (body != null && body.get("data") instanceof Map<?, ?> data) {
            Object id = data.get("id");
            return id == null ? null : id.toString();
        }
        return null;
    }
}
