package com.example.stock_concurrency_problem.service;

import com.example.stock_concurrency_problem.domain.Stock;
import com.example.stock_concurrency_problem.repository.stockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service @RequiredArgsConstructor
public class stockService {
    private final stockRepository stockRepository;

    @Transactional
    public void decrease(Long id,Long quantity){
        Stock stock = stockRepository.findById(id).orElseThrow();
        stock.decrease(quantity);
        //Test에서 assertEquals(expected,값) 이렇게 해서 검증을 진행해볼수 있습니다.
    }
}
