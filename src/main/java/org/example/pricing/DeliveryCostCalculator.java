package org.example.pricing;

import org.example.catalog.Item;

import java.util.List;

public class DeliveryCostCalculator {
    private static final double BASE_FEE = 200.0;

    public double calculateDelivery(List<Item> items, String deliveryZone) {
        if (items == null || items.isEmpty()) return 0.0;

        double totalDelivery = BASE_FEE;

        // Наценка за зону
        if ("ZONE_2".equals(deliveryZone)) totalDelivery += 300.0;
        else if ("ZONE_3".equals(deliveryZone)) totalDelivery += 500.0;

        for (Item item : items) {
            // За каждый кг сверх 5кг - наценка 50р с учетом количества товаров
            if (item.getWeight() > 5.0) {
                totalDelivery += (item.getWeight() - 5.0) * 50 * item.getQuantity();
            }
            if (item.isFragile()) {
                totalDelivery += 150.0 * item.getQuantity();
            }
        }
        return totalDelivery;
    }
}