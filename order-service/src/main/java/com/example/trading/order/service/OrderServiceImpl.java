package com.example.trading.order.service;

import com.example.trading.order.entity.OrderEntity;
import com.example.trading.order.repository.OrderRepository;
import com.example.trading.proto.*;
import io.grpc.StatusRuntimeException;
import net.devh.boot.grpc.client.inject.GrpcClient;
import net.devh.boot.grpc.server.service.GrpcService;
import io.grpc.stub.StreamObserver;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;



@GrpcService
public class OrderServiceImpl extends OrderServiceGrpc.OrderServiceImplBase {

    @GrpcClient("matching-engine")
    private MatchingEngineServiceGrpc.MatchingEngineServiceBlockingStub matchingEngineStub;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public void placeOrder(OrderRequest request, StreamObserver<OrderResponse> responseObserver) {
        System.out.println("➡️ Received order: " + request.getSymbol() + " " + request.getSide());

        String orderId = UUID.randomUUID().toString();

    OrderEntity entity = new OrderEntity(
        orderId,
        request.getUserId(),
        request.getSymbol(),
        request.getSide(),
        request.getQuantity(),
        request.getPrice(),
        "PLACED"
    );

    orderRepository.save(entity);


    MatchRequest matchRequest = MatchRequest.newBuilder()
            .setOrderId(orderId)
            .setSymbol(request.getSymbol())
            .setQuantity(request.getQuantity())
            .setSide(request.getSide())
            .setPrice(request.getPrice())
            .setUserId(request.getUserId()) 
            .build();

    MatchResponse matchResponse;

    try {
        System.out.println("Sending to Matching Engine...");
        matchResponse = matchingEngineStub.matchOrder(matchRequest);
        System.out.println("Matching Engine responded: " + matchResponse.getMessage());
    } catch (StatusRuntimeException e) {
        System.err.println("Matching Engine call failed: " + e.getMessage());
        responseObserver.onError(e);
        return;
    }

    String status = matchResponse.getMatched() ? "MATCHED" : "PENDING";

    OrderResponse response = OrderResponse.newBuilder()
            .setOrderId(matchRequest.getOrderId())
            .setStatus(status)
            .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
}

}
