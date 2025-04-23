package com.example.trading.ledger.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.example.trading.ledger.entity.LedgerEntryEntity;
import com.example.trading.ledger.repository.LedgerRepository;
import com.example.trading.proto.LedgerEntryRequest;
import com.example.trading.proto.LedgerEntryResponse;
import com.example.trading.proto.LedgerServiceGrpc;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class LedgerServiceImpl extends LedgerServiceGrpc.LedgerServiceImplBase {

    @Autowired
    private LedgerRepository ledgerRepository;

    @Override
    public void recordTrade(LedgerEntryRequest request, StreamObserver<LedgerEntryResponse> responseObserver) {
        System.out.println("🧾 Recording trade in ledger:");
        System.out.println("→ Trade ID: " + request.getTradeId());
        System.out.println("→ Symbol: " + request.getSymbol());
        System.out.println("→ Quantity: " + request.getQuantity());
        System.out.println("→ Price: " + request.getPrice());
        System.out.println("→ Side: " + request.getSide());

        LedgerEntryEntity entity = new LedgerEntryEntity(
            request.getTradeId(),
            request.getSymbol(),
            request.getQuantity(),
            request.getPrice(),
            request.getMatchedAt(),
            request.getSide(),
            request.getBuyerOrderId(),
            request.getSellerOrderId()
        );

        ledgerRepository.save(entity);
        System.out.println("💾 Ledger entry saved for tradeId: " + request.getTradeId());


        LedgerEntryResponse response = LedgerEntryResponse.newBuilder()
                .setStatus("RECORDED")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
