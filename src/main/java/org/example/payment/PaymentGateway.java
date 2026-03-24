package org.example.payment;

import org.example.customer.User;

public interface PaymentGateway {
    // Может выбросить RuntimeException при сетевой ошибке
    boolean charge(User user, double amount);

    void refund(User user, double amount);
}