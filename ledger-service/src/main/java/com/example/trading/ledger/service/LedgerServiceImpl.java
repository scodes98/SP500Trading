package com.example.trading.ledger.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.trading.ledger.entity.LedgerEntryEntity;
import com.example.trading.ledger.repository.LedgerRepository;
import com.example.trading.proto.LedgerEntryRequest;
import com.example.trading.proto.LedgerEntryResponse;
import com.example.trading.proto.LedgerServiceGrpc;
import com.example.trading.proto.TradeEntry;
import com.example.trading.proto.UserTradeRequest;
import com.example.trading.proto.UserTradeResponse;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import java.util.List;

@GrpcService
public class LedgerServiceImpl extends LedgerServiceGrpc.LedgerServiceImplBase {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Override
    public void recordTrade(LedgerEntryRequest request, StreamObserver<LedgerEntryResponse> responseObserver) {
        System.out.println("Recording trade in ledger:");
        System.out.println("Trade ID: " + request.getTradeId());
        System.out.println("Symbol: " + request.getSymbol());
        System.out.println("Quantity: " + request.getQuantity());
        System.out.println("Price: " + request.getPrice());
        System.out.println("Side: " + request.getSide());
        System.out.println("buyer_user_id: " + request.getBuyerUserId());
        System.out.println("seller_user_id: " + request.getSellerUserId());

        LedgerEntryEntity entity = new LedgerEntryEntity(
            request.getTradeId(),
            request.getSymbol(),
            request.getQuantity(),
            request.getPrice(),
            request.getMatchedAt(),
            request.getSide(),
            request.getBuyerOrderId(),
            request.getSellerOrderId(),
            request.getBuyerUserId(),
            request.getSellerUserId()
        );

        ledgerRepository.save(entity);
        System.out.println("Ledger entry saved for tradeId: " + request.getTradeId());


        LedgerEntryResponse response = LedgerEntryResponse.newBuilder()
                .setStatus("RECORDED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTradesByUser(UserTradeRequest request, StreamObserver<UserTradeResponse> responseObserver) {
    String userId = request.getUserId();

    System.out.println("Fetching trades for userId: " + userId);

    List<LedgerEntryEntity> userTrades = ledgerRepository.findByBuyerUserIdOrSellerUserId(userId, userId);

    System.out.println("Found trades: " + userTrades.size());

    UserTradeResponse.Builder responseBuilder = UserTradeResponse.newBuilder();

    for (LedgerEntryEntity entity : userTrades) {
        TradeEntry trade = TradeEntry.newBuilder()
                .setTradeId(entity.getTradeId())
                .setSymbol(entity.getSymbol())
                .setQuantity(entity.getQuantity())
                .setPrice(entity.getPrice())
                .setMatchedAt(entity.getMatchedAt())
                .setSide(entity.getSide())
                .setBuyerOrderId(entity.getBuyerOrderId())
                .setSellerOrderId(entity.getSellerOrderId())
                .setBuyerUserId(entity.getBuyerUserId())
                .setSellerUserId(entity.getSellerUserId())
                .build();

        responseBuilder.addTrades(trade);
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}

}
