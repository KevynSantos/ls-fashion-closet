package com.lsfashioncloset.service;

import com.lsfashioncloset.config.MercadoPagoProperties;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class MercadoPagoPaymentLookupService {
    private final MercadoPagoProperties properties;
    private final RestClient restClient = RestClient.create("https://api.mercadopago.com");

    public MercadoPagoPaymentLookupService(MercadoPagoProperties properties) {
        this.properties = properties;
    }

    @SuppressWarnings("unchecked")
    public PaymentResult findPayment(String paymentId) {
        Map<String, Object> response = restClient.get()
            .uri("/v1/payments/{paymentId}", paymentId)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + properties.getAccessToken())
            .retrieve()
            .body(Map.class);

        if (response == null) {
            throw new IllegalStateException("Pagamento nao encontrado no Mercado Pago");
        }

        String status = stringValue(response.get("status"));
        String externalReference = stringValue(response.get("external_reference"));
        Long orderId = externalReference == null || externalReference.isBlank()
            ? null
            : Long.valueOf(externalReference);
        return new PaymentResult(paymentId, orderId, status);
    }

    private String stringValue(Object value) {
        return value == null ? null : value.toString();
    }

    public record PaymentResult(String paymentId, Long orderId, String status) {
    }
}
