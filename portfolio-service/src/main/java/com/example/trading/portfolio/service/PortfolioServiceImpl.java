package com.example.trading.portfolio.service;

import com.example.trading.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.HashMap;
import java.util.Map;

@GrpcService
public class PortfolioServiceImpl extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    @GrpcClient("ledger-service")
    private LedgerServiceGrpc.LedgerServiceBlockingStub ledgerStub;

    @Override
    public void getPositions(PositionRequest request, StreamObserver<PositionResponse> responseObserver) {
        UserLedgerRequest ledgerRequest = UserLedgerRequest.newBuilder()
                .setUserId(request.getUserId())
                .build();

        UserLedgerResponse ledgerResponse = ledgerStub.getLedgerEntriesByUser(ledgerRequest);

        Map<String, int[]> positionMap = new HashMap<>(); // [0] = BOUGHT, [1] = SOLD

        for (LedgerEntry entry : ledgerResponse.getEntriesList()) {
            String symbol = entry.getSymbol();
            int quantity = entry.getQuantity();
            positionMap.putIfAbsent(symbol, new int[2]);

            if ("BUY".equals(entry.getSide()) && request.getUserId().equals(entry.getBuyerUserId())) {
                positionMap.get(symbol)[0] += quantity;
            } else if ("SELL".equals(entry.getSide()) && request.getUserId().equals(entry.getSellerUserId())) {
                positionMap.get(symbol)[1] += quantity;
            }
        }

        PositionResponse.Builder responseBuilder = PositionResponse.newBuilder();
        for (Map.Entry<String, int[]> entry : positionMap.entrySet()) {
            responseBuilder.addPositions(Position.newBuilder()
                    .setSymbol(entry.getKey())
                    .setBOUGHT(entry.getValue()[0])
                    .setSOLD(entry.getValue()[1])
                    .build());
        }

        responseObserver.onNext(responseBuilder.build());
        responseObserver.onCompleted();
    }
}
