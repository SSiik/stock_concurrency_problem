package com.example.stock_concurrency_problem.repository;

import com.example.stock_concurrency_problem.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface stockRepository extends JpaRepository<Stock,Long> {
}
