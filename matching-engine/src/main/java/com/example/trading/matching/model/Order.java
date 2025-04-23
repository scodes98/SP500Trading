package com.example.trading.matching.model;

public class Order {

    private final String orderId;
    private final String side;     // "BUY" or "SELL"
    private final String symbol;
    private final double price;
    private int quantity;          // mutable for partial fills
    private final long timestamp;  // for time-priority
    private final String userId;


    public Order(String orderId, String side, String symbol, double price, int quantity, long timestamp, String userId) {
        this.orderId = orderId;
        this.side = side;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getSide() {
        return side;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    // 🧮 Reduce remaining quantity after a partial fill
    public void reduceQuantity(int fillQty) {
        this.quantity -= fillQty;
    }

    // ✅ Used to check if order is fully filled
    public boolean isFilled() {
        return this.quantity <= 0;
    }
}
