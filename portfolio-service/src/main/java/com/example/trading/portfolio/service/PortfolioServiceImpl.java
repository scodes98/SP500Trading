package com.example.trading.portfolio.service;

import com.example.trading.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;

@GrpcService
public class PortfolioServiceImpl extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    @GrpcClient("ledger-service")
    private LedgerServiceGrpc.LedgerServiceBlockingStub ledgerStub;

    @Override
    public void getPositions(PositionRequest request, StreamObserver<PositionResponse> responseObserver) {
    UserTradeRequest tradeRequest = UserTradeRequest.newBuilder()
            .setUserId(request.getUserId())
            .build();

    UserTradeResponse tradeResponse = ledgerStub.getTradesByUser(tradeRequest);

    Map<String, Integer> positionMap = new HashMap<>();

    for (TradeEntry trade : tradeResponse.getTradesList()) {
        String symbol = trade.getSymbol();
        int quantity = trade.getQuantity();
        String userId = request.getUserId();

        if (userId.equals(trade.getBuyerUserId())) {
            positionMap.put(symbol, positionMap.getOrDefault(symbol, 0) + quantity);
        } else if (userId.equals(trade.getSellerUserId())) {
            positionMap.put(symbol, positionMap.getOrDefault(symbol, 0) - quantity);
        }
    }

    PositionResponse.Builder responseBuilder = PositionResponse.newBuilder();
    for (Map.Entry<String, Integer> entry : positionMap.entrySet()) {
        responseBuilder.addPositions(Position.newBuilder()
                .setSymbol(entry.getKey())
                .setQuantity(entry.getValue())
                .build());
    }

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
}

}
