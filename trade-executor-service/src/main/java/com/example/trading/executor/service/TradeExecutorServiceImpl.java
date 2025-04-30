package com.example.trading.executor.service;

import com.example.trading.executor.entity.TradeEntity;
import com.example.trading.executor.repository.TradeRepository;
import com.example.trading.proto.LedgerEntryRequest;
import com.example.trading.proto.LedgerEntryResponse;
import com.example.trading.proto.LedgerServiceGrpc;
import com.example.trading.proto.TradeExecutorServiceGrpc;
import com.example.trading.proto.TradeRequest;
import com.example.trading.proto.TradeResponse;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

@GrpcService
public class TradeExecutorServiceImpl extends TradeExecutorServiceGrpc.TradeExecutorServiceImplBase {

    @GrpcClient("ledger-service")
    private LedgerServiceGrpc.LedgerServiceBlockingStub ledgerStub;

    @Autowired
    private TradeRepository tradeRepository;


   @Override
    public void executeTrade(TradeRequest request, StreamObserver<TradeResponse> responseObserver) {
        System.out.println("Executing trade for symbol: " + request.getSymbol());

        String tradeId = UUID.randomUUID().toString();

        TradeEntity entity = new TradeEntity(
            tradeId,
            request.getSymbol(),
            request.getQuantity(),
            request.getPrice(),
            request.getMatchedAt(),
            request.getBuyOrderId(),
            request.getSellOrderId(),
            request.getBuyerUserId(),
            request.getSellerUserId()
        );

        tradeRepository.save(entity);
        System.out.println("Trade stored in DB: " + tradeId);


        System.out.println("Executing Trade [" + tradeId + "]");
        System.out.println("→ Symbol: " + request.getSymbol());
        System.out.println("→ Quantity: " + request.getQuantity() + " @ " + request.getPrice());
        System.out.println("→ Buyer: " + request.getBuyerUserId() + " (Order: " + request.getBuyOrderId() + ")");
        System.out.println("→ Seller: " + request.getSellerUserId() + " (Order: " + request.getSellOrderId() + ")");

        // BUY side entry
        LedgerEntryRequest buyerEntry = LedgerEntryRequest.newBuilder()
        .setTradeId(tradeId + "-B")
        .setSymbol(request.getSymbol())
        .setQuantity(request.getQuantity())
        .setPrice(request.getPrice())
        .setMatchedAt(request.getMatchedAt())
        .setSide("BUY")
        .setBuyerOrderId(request.getBuyOrderId())
        .setSellerOrderId(request.getSellOrderId())
        .setBuyerUserId(request.getBuyerUserId())
        .setSellerUserId(request.getSellerUserId())
        .build();
        LedgerEntryResponse buyerResponse = ledgerStub.recordTrade(buyerEntry);
        System.out.println("Ledger BUY status: " + buyerResponse.getStatus());

        // SELL side entry
        LedgerEntryRequest sellerEntry = LedgerEntryRequest.newBuilder()
        .setTradeId(tradeId + "-S")
        .setSymbol(request.getSymbol())
        .setQuantity(request.getQuantity())
        .setPrice(request.getPrice())
        .setMatchedAt(request.getMatchedAt())
        .setSide("SELL")
        .setBuyerOrderId(request.getBuyOrderId())
        .setSellerOrderId(request.getSellOrderId())
        .setBuyerUserId(request.getBuyerUserId())
        .setSellerUserId(request.getSellerUserId())
        .build();
        LedgerEntryResponse sellerResponse = ledgerStub.recordTrade(sellerEntry);
        System.out.println("Ledger SELL status: " + sellerResponse.getStatus());


        TradeResponse response = TradeResponse.newBuilder()
                .setTradeId(tradeId)
                .setStatus("EXECUTED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}
