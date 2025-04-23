package com.example.trading.portfolio.service;

import com.example.trading.proto.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.*;

@GrpcService
public class PortfolioServiceImpl extends PortfolioServiceGrpc.PortfolioServiceImplBase {

    // Dummy in-memory position map for now
    private final Map<String, List<Position>> userPositions = new HashMap<>();

    public PortfolioServiceImpl() {
        // Pre-populate some dummy data
        userPositions.put("user123", Arrays.asList(
                Position.newBuilder().setSymbol("AAPL").setQuantity(40).build(),
                Position.newBuilder().setSymbol("GOOGL").setQuantity(20).build()
        ));
    }

    @Override
    public void getPositions(PositionRequest request, StreamObserver<PositionResponse> responseObserver) {
        List<Position> positions = userPositions.getOrDefault(request.getUserId(), new ArrayList<>());

        PositionResponse response = PositionResponse.newBuilder()
                .addAllPositions(positions)
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
