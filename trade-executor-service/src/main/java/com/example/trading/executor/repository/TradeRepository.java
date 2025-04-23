package com.example.trading.executor.repository;

import com.example.trading.executor.entity.TradeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradeRepository extends JpaRepository<TradeEntity, String> {
}
