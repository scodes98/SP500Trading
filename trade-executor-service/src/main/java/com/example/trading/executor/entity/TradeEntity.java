package com.example.trading.executor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "executed_trades")
public class TradeEntity {

    @Id
    private String tradeId;

    private String symbol;
    private int quantity;
    private double price;
    private String matchedAt;
    private String buyerOrderId;
    private String sellerOrderId;
    private String buyerUserId;
    private String sellerUserId;

    public TradeEntity() {}

    public TradeEntity(String tradeId, String symbol, int quantity, double price, String matchedAt,
                       String buyerOrderId, String sellerOrderId, String buyerUserId, String sellerUserId) {
        this.tradeId = tradeId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.matchedAt = matchedAt;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
        this.buyerUserId = buyerUserId;
        this.sellerUserId = sellerUserId;
    }

    public String getTradeId() {
        return tradeId;
    }
    
    public void setTradeId(String tradeId) {
        this.tradeId = tradeId;
    }
    
    public String getSymbol() {
        return symbol;
    }
    
    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getPrice() {
        return price;
    }
    
    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getMatchedAt() {
        return matchedAt;
    }
    
    public void setMatchedAt(String matchedAt) {
        this.matchedAt = matchedAt;
    }
    
    public String getBuyerOrderId() {
        return buyerOrderId;
    }
    
    public void setBuyerOrderId(String buyerOrderId) {
        this.buyerOrderId = buyerOrderId;
    }
    
    public String getSellerOrderId() {
        return sellerOrderId;
    }
    
    public void setSellerOrderId(String sellerOrderId) {
        this.sellerOrderId = sellerOrderId;
    }
    
    public String getBuyerUserId() {
        return buyerUserId;
    }
    
    public void setBuyerUserId(String buyerUserId) {
        this.buyerUserId = buyerUserId;
    }
    
    public String getSellerUserId() {
        return sellerUserId;
    }
    
    public void setSellerUserId(String sellerUserId) {
        this.sellerUserId = sellerUserId;
    }
    
}
