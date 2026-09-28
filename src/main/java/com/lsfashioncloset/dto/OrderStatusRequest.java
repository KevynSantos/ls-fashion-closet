package com.lsfashioncloset.dto;

import com.lsfashioncloset.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderStatusRequest(@NotNull OrderStatus status) {
}
