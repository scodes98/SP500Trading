package com.example.trading.ledger.repository;

import com.example.trading.ledger.entity.LedgerEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedgerRepository extends JpaRepository<LedgerEntryEntity, String> {
}
