package com.example.trading.matching.service;

import com.example.trading.matching.model.Order;
import com.example.trading.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.Comparator;
import java.util.PriorityQueue;

@GrpcService
public class MatchingEngineServiceImpl extends MatchingEngineServiceGrpc.MatchingEngineServiceImplBase {

    @GrpcClient("trade-executor")
    private TradeExecutorServiceGrpc.TradeExecutorServiceBlockingStub tradeExecutorStub;

    private final PriorityQueue<Order> buyOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice).reversed().thenComparingLong(Order::getTimestamp)
    );

    private final PriorityQueue<Order> sellOrders = new PriorityQueue<>(
            Comparator.comparingDouble(Order::getPrice).thenComparingLong(Order::getTimestamp)
    );

    @Override
    public void matchOrder(MatchRequest request, StreamObserver<MatchResponse> responseObserver) {
        System.out.println("New Order: " + request.getSide() + " " + request.getSymbol() + " @ " + request.getPrice());

        Order incomingOrder = new Order(
                request.getOrderId(),
                request.getSide(),
                request.getSymbol(),
                request.getPrice(),
                request.getQuantity(),
                System.currentTimeMillis(),
                request.getUserId()
        );

        boolean matched = false;

        if ("BUY".equalsIgnoreCase(request.getSide())) {
            matched = matchBuyOrder(incomingOrder);
        } else if ("SELL".equalsIgnoreCase(request.getSide())) {
            matched = matchSellOrder(incomingOrder);
        } else {
            System.err.println("Unknown side: " + request.getSide());
        }

        MatchResponse response = MatchResponse.newBuilder()
                .setMatched(matched)
                .setMessage(matched ? "Order matched and (partially) filled" : "Order added to book")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private boolean matchBuyOrder(Order buyOrder) {
        boolean matched = false;

        while (!sellOrders.isEmpty() && buyOrder.getQuantity() > 0) {
            Order bestSell = sellOrders.peek();

            if (!bestSell.getSymbol().equals(buyOrder.getSymbol()) || bestSell.getPrice() > buyOrder.getPrice()) {
                break;
            }

            int tradeQty = Math.min(buyOrder.getQuantity(), bestSell.getQuantity());
            double tradePrice = bestSell.getPrice();

            // Execute trade
            executeTrade(buyOrder, bestSell, buyOrder.getSymbol(), tradeQty, tradePrice);

            buyOrder.reduceQuantity(tradeQty);
            bestSell.reduceQuantity(tradeQty);
            matched = true;

            if (bestSell.isFilled()) {
                sellOrders.poll();
            }
        }

        if (!buyOrder.isFilled()) {
            buyOrders.add(buyOrder);
            System.out.println("📥 BUY added to book: " + buyOrder.getQuantity() + " remaining");
        }

        return matched;
    }

    private boolean matchSellOrder(Order sellOrder) {
        boolean matched = false;

        while (!buyOrders.isEmpty() && sellOrder.getQuantity() > 0) {
            Order bestBuy = buyOrders.peek();

            if (!bestBuy.getSymbol().equals(sellOrder.getSymbol()) || bestBuy.getPrice() < sellOrder.getPrice()) {
                break;
            }

            int tradeQty = Math.min(sellOrder.getQuantity(), bestBuy.getQuantity());
            double tradePrice = bestBuy.getPrice();

            // Execute trade
            // Inside matchSellOrder(...)
            executeTrade(bestBuy, sellOrder, sellOrder.getSymbol(), tradeQty, tradePrice);

            sellOrder.reduceQuantity(tradeQty);
            bestBuy.reduceQuantity(tradeQty);
            matched = true;

            if (bestBuy.isFilled()) {
                buyOrders.poll();
            }
        }

        if (!sellOrder.isFilled()) {
            sellOrders.add(sellOrder);
            System.out.println("📥 SELL added to book: " + sellOrder.getQuantity() + " remaining");
        }

        return matched;
    }

    private void executeTrade(Order buyOrder, Order sellOrder, String symbol, int quantity, double price) {
        TradeRequest trade = TradeRequest.newBuilder()
                .setBuyOrderId(buyOrder.getOrderId())
                .setSellOrderId(sellOrder.getOrderId())
                .setSymbol(symbol)
                .setQuantity(quantity)
                .setPrice(price)
                .setMatchedAt(java.time.Instant.now().toString())
                .setBuyerUserId(buyOrder.getUserId())         
                .setSellerUserId(sellOrder.getUserId())       
                .build();

        TradeResponse tradeResponse = tradeExecutorStub.executeTrade(trade);
        System.out.println("Trade Executed: " + tradeResponse.getTradeId() + " | Qty: " + quantity + " @ " + price);
    }
}
