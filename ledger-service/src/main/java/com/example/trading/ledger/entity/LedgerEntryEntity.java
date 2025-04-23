package com.example.trading.ledger.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ledger_entries")
public class LedgerEntryEntity {

    @Id
    private String tradeId;

    private String symbol;
    private int quantity;
    private double price;
    private String matchedAt;
    private String side;
    private String buyerOrderId;
    private String sellerOrderId;

    public LedgerEntryEntity() {}

    public LedgerEntryEntity(String tradeId, String symbol, int quantity, double price, String matchedAt,
                              String side, String buyerOrderId, String sellerOrderId) {
        this.tradeId = tradeId;
        this.symbol = symbol;
        this.quantity = quantity;
        this.price = price;
        this.matchedAt = matchedAt;
        this.side = side;
        this.buyerOrderId = buyerOrderId;
        this.sellerOrderId = sellerOrderId;
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
    
    public String getSide() {
        return side;
    }
    
    public void setSide(String side) {
        this.side = side;
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
    
}
