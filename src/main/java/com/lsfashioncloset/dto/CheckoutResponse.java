package com.lsfashioncloset.dto;

import java.math.BigDecimal;

public record CheckoutResponse(
    Long orderId,
    BigDecimal total,
    String mercadoPagoPreferenceId,
    String checkoutUrl
) {
}
