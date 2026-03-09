package com.example.payments;

import java.util.HashMap;
import java.util.Map;

public class App {
    public static void main(String[] args) {
        Map<String, PaymentGateway> registry = new HashMap<>();

        registry.put("fastpay", new FastPayAdapter(new FastPayClient()));
        registry.put("safecash", new SafeCashAdapter(new SafeCashClient()));

        OrderService orderService = new OrderService(registry);

        String result1 = orderService.charge("fastpay", "cust-1", 1299);
        String result2 = orderService.charge("safecash", "cust-2", 1299);

        System.out.println(result1);
        System.out.println(result2);
    }
}