package com.example.stock_concurrency_problem.service;

import com.example.stock_concurrency_problem.repository.RedisLockRepository;
import io.lettuce.core.dynamic.annotation.Command;
import org.springframework.stereotype.Component;

@Component
public class LettuceLockService {

    private RedisLockRepository repository;
    private stockService stockService;

    public LettuceLockService(RedisLockRepository repository, stockService stockService) {
        this.repository = repository;
        this.stockService = stockService;
    }

    public void decrease(Long key, Long quantity) throws InterruptedException {
        while(!repository.lock(key)){
            Thread.sleep(50);
        }

        try{
            stockService.decrease(key,quantity);
        } finally {
            repository.unlock(key);
        }
    }
}
