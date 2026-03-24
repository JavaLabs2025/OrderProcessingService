package org.example.order;

import org.example.catalog.InventoryService;
import org.example.catalog.Item;
import org.example.customer.User;
import org.example.payment.PaymentGateway;
import org.example.pricing.DeliveryCostCalculator;
import org.example.pricing.DiscountCalculator;
import org.example.pricing.PromoCode;

import java.util.List;
import java.util.UUID;

public class OrderProcessor {
    private final InventoryService inventoryService;
    private final PaymentGateway paymentGateway;
    private final DiscountCalculator discountCalculator;
    private final DeliveryCostCalculator deliveryCostCalculator;

    public OrderProcessor(InventoryService inventoryService,
                          PaymentGateway paymentGateway,
                          DiscountCalculator discountCalculator,
                          DeliveryCostCalculator deliveryCostCalculator) {
        this.inventoryService = inventoryService;
        this.paymentGateway = paymentGateway;
        this.discountCalculator = discountCalculator;
        this.deliveryCostCalculator = deliveryCostCalculator;
    }

    public Order processOrder(User user, List<Item> items, String deliveryZone, PromoCode promoCode) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        // 1. Проверка наличия и подсчет базовой суммы
        double rawTotal = 0.0;
        for (Item item : items) {
            if (!inventoryService.inStock(item.getId(), item.getQuantity())) {
                throw new IllegalStateException("Item " + item.getId() + " is out of stock");
            }
            rawTotal += item.getPrice() * item.getQuantity();
        }

        // 2. Расчет скидок и промокода
        double discountedPrice = discountCalculator.calculateDiscount(user, rawTotal);
        double priceAfterPromo = discountCalculator.applyPromoCode(discountedPrice, promoCode);

        // 3. Расчет доставки
        double deliveryCost = deliveryCostCalculator.calculateDelivery(items, deliveryZone);
        double finalPrice = priceAfterPromo + deliveryCost;

        // 4. Оплата с механизмом Retry (максимум 3 попытки)
        boolean paymentSuccess = false;
        int attempts = 0;
        while (attempts < 3 && !paymentSuccess) {
            attempts++;
            try {
                paymentSuccess = paymentGateway.charge(user, finalPrice);
            } catch (RuntimeException e) {
                if (attempts == 3) {
                    throw new RuntimeException("Payment gateway unavailable after 3 attempts", e);
                }
            }
        }

        if (!paymentSuccess) {
            throw new IllegalStateException("Payment rejected by gateway for user " + user.getId());
        }

        // 5. Списание со склада
        for (Item item : items) {
            inventoryService.deductItem(item.getId(), item.getQuantity());
        }

        // 6. Формирование заказа
        Order order = new Order(UUID.randomUUID().toString(), user, items);
        order.setFinalPrice(finalPrice);
        order.setStatus(OrderStatus.PAID);

        return order;
    }

    public void cancelOrder(Order order) {
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("Cannot cancel a shipped order");
        }

        if (order.getStatus() == OrderStatus.PAID) {
            paymentGateway.refund(order.getUser(), order.getFinalPrice());
        }

        for (Item item : order.getItems()) {
            inventoryService.returnItem(item.getId(), item.getQuantity());
        }

        order.setStatus(OrderStatus.CANCELLED);
    }
}