package com.example.payments;

import java.util.Objects;

public class SafeCashAdapter implements PaymentGateway {

    private final SafeCashClient safeCash;

    public SafeCashAdapter(SafeCashClient safeCash) {
        this.safeCash = Objects.requireNonNull(safeCash, "safeCash");
    }

    @Override
    public String charge(String customerId, int amountCents) {
        Objects.requireNonNull(customerId, "customerId");
        SafeCashPayment txn = safeCash.createPayment(amountCents, customerId);
        return txn.confirm();
    }
}