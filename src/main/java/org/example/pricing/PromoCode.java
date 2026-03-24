package org.example.pricing;

import java.time.LocalDate;


public class PromoCode {
    private final String code;
    private final double discountAmount;
    private final LocalDate expiryDate;

    public PromoCode(String code, double discountAmount, LocalDate expiryDate) {
        this.code = code;
        this.discountAmount = discountAmount;
        this.expiryDate = expiryDate;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }
}
