package com.example.payments;

import java.util.Objects;

public class FastPayAdapter implements PaymentGateway {

    private final FastPayClient fastPay;

    public FastPayAdapter(FastPayClient fastPay) {
        this.fastPay = Objects.requireNonNull(fastPay, "fastPay");
    }

    @Override
    public String charge(String customerId, int amountCents) {
        Objects.requireNonNull(customerId, "customerId");
        return fastPay.payNow(customerId, amountCents);
    }
}