package org.example.catalog;

public interface InventoryService {
    boolean inStock(String itemId, int quantity);

    void deductItem(String itemId, int quantity);

    void returnItem(String itemId, int quantity);
}
