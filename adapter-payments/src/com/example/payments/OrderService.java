package com.example.payments;

import java.util.Map;
import java.util.Objects;

public class OrderService {
    private final Map<String, PaymentGateway> providers;

    public OrderService(Map<String, PaymentGateway> providers) {
        this.providers = Objects.requireNonNull(providers, "providers");
    }

    public String charge(String provider, String customerId, int amountCents) {
        Objects.requireNonNull(provider, "provider");
        PaymentGateway gateway = providers.get(provider);
        if (gateway == null) throw new IllegalArgumentException("unknown provider: " + provider);
        return gateway.charge(customerId, amountCents);
    }
}