package org.example.customer;

public class User {
    private final String id;
    private final boolean isVip;

    public User(String id, boolean isVip) {
        this.id = id;
        this.isVip = isVip;
    }

    public String getId() {
        return id;
    }

    public boolean isVip() {
        return isVip;
    }
}
