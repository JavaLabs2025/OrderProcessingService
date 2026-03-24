package org.example.catalog;

public class Item {
    private final String id;
    private final double price;
    private final int quantity;
    private final double weight; // вес в кг
    private final boolean isFragile; // хрупкий груз

    public Item(String id, double price, int quantity, double weight, boolean isFragile) {
        this.id = id;
        this.price = price;
        this.quantity = quantity;
        this.weight = weight;
        this.isFragile = isFragile;
    }

    // Геттеры
    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getWeight() {
        return weight;
    }

    public boolean isFragile() {
        return isFragile;
    }
}