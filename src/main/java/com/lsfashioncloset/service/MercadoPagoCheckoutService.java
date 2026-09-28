package com.lsfashioncloset.service;

import com.lsfashioncloset.config.AppProperties;
import com.lsfashioncloset.config.MercadoPagoProperties;
import com.lsfashioncloset.model.OrderItem;
import com.lsfashioncloset.model.StoreOrder;
import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceBackUrlsRequest;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferencePayerRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.resources.preference.Preference;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MercadoPagoCheckoutService {
    private final MercadoPagoProperties mercadoPagoProperties;
    private final AppProperties appProperties;

    public MercadoPagoCheckoutService(MercadoPagoProperties mercadoPagoProperties, AppProperties appProperties) {
        this.mercadoPagoProperties = mercadoPagoProperties;
        this.appProperties = appProperties;
    }

    public Preference createPreference(StoreOrder order) throws Exception {
        MercadoPagoConfig.setAccessToken(mercadoPagoProperties.getAccessToken());

        List<PreferenceItemRequest> items = order.getItems().stream()
            .map(this::toPreferenceItem)
            .toList();

        PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
            .success(appProperties.frontendUrl() + "/?payment=success&order=" + order.getId())
            .pending(appProperties.frontendUrl() + "/?payment=pending&order=" + order.getId())
            .failure(appProperties.frontendUrl() + "/?payment=failure&order=" + order.getId())
            .build();

        PreferenceRequest.PreferenceRequestBuilder builder = PreferenceRequest.builder()
            .items(items)
            .payer(PreferencePayerRequest.builder()
                .name(order.getCustomerName())
                .email(order.getCustomerEmail())
                .build())
            .backUrls(backUrls)
            .autoReturn("approved")
            .externalReference(order.getId().toString())
            .statementDescriptor(mercadoPagoProperties.getStatementDescriptor())
            .metadata(Map.of("order_id", order.getId()));

        if (StringUtils.hasText(mercadoPagoProperties.getNotificationUrl())) {
            builder.notificationUrl(mercadoPagoProperties.getNotificationUrl());
        }

        return new PreferenceClient().create(builder.build());
    }

    private PreferenceItemRequest toPreferenceItem(OrderItem item) {
        return PreferenceItemRequest.builder()
            .id(item.getProduct().getId().toString())
            .title(item.getProductName())
            .description(item.getProduct().getDescription())
            .quantity(item.getQuantity())
            .currencyId("BRL")
            .unitPrice(item.getUnitPrice())
            .build();
    }
}
