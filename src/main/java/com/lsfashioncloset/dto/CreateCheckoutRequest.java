package com.lsfashioncloset.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public record CreateCheckoutRequest(
    @NotBlank String customerName,
    @Email @NotBlank String customerEmail,
    @NotBlank String customerPhone,
    @NotBlank String customerDocument,
    @NotBlank String shippingZipCode,
    @NotBlank String shippingStreet,
    @NotBlank String shippingNumber,
    String shippingComplement,
    @NotBlank String shippingNeighborhood,
    @NotBlank String shippingCity,
    @NotBlank String shippingState,
    @NotEmpty List<@Valid CartItemRequest> items
) {
}
