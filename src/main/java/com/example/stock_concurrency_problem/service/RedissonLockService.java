package com.example.stock_concurrency_problem.service;

import io.lettuce.core.dynamic.annotation.CommandNaming;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonLockService {

    private RedissonClient redissonClient;

    private stockService stockService;

    public RedissonLockService(RedissonClient redissonClient, com.example.stock_concurrency_problem.service.stockService stockService) {
        this.redissonClient = redissonClient;
        this.stockService = stockService;
    }

    public void decrease(Long key,Long quantity){
        RLock lock = redissonClient.getLock(key.toString());
        try{
            boolean available = lock.tryLock(5,1, TimeUnit.SECONDS);

            if(!available){
                System.out.println("lock 획득 실패");
                return;
            }
            stockService.decrease(key,quantity);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
    }
}
