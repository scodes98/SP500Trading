package com.example.trading.ledger.repository;

import com.example.trading.ledger.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LedgerRepository extends JpaRepository<LedgerEntryEntity, String> {
    List<LedgerEntryEntity> findByBuyerUserIdOrSellerUserId(String buyerUserId, String sellerUserId);
}
