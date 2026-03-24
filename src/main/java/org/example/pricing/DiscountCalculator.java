package org.example.pricing;

import org.example.customer.User;

import java.time.Clock;
import java.time.LocalDate;

public class DiscountCalculator {
    private final Clock clock;

    public DiscountCalculator(Clock clock) {
        this.clock = clock;
    }

    public double calculateDiscount(User user, double totalAmount) {
        if (totalAmount < 0) {
            throw new IllegalArgumentException("Total amount cannot be negative");
        }
        double discountPercentage = 0.0;
        if (user.isVip()) {
            discountPercentage += 0.10;
        }
        if (totalAmount >= 5000.0) {
            discountPercentage += 0.05;
        }
        if (discountPercentage > 0.15) {
            discountPercentage = 0.15;
        }
        return totalAmount - (totalAmount * discountPercentage);
    }

    public double applyPromoCode(double currentTotal, PromoCode promoCode) {
        if (promoCode == null) return currentTotal;

        LocalDate today = LocalDate.now(clock);
        if (today.isAfter(promoCode.getExpiryDate())) {
            throw new IllegalArgumentException("Promo code expired");
        }
        return Math.max(0, currentTotal - promoCode.getDiscountAmount());
    }
}

