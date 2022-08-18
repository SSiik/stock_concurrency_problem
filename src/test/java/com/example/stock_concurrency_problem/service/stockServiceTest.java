package com.example.stock_concurrency_problem.service;

import com.example.stock_concurrency_problem.domain.Stock;
import com.example.stock_concurrency_problem.repository.stockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class stockServiceTest {
    @Autowired private stockService stockService;
    @Autowired private stockRepository stockRepository;

    @BeforeEach
    public void before(){
        Stock stock = new Stock(1L, 100L);
        stockRepository.save(stock);
    }

    @AfterEach
    public void after(){
        stockRepository.deleteAll();
    }

    @Test
    public void decrease_test(){
        stockService.decrease(1L,1L);
        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(99,stock.getQuantity());
    }

    //동시에 요청 100개가 날라오는 테스트케이스를 작성해보겠습니다.
    @Test
    public void concurrencyRequest() throws InterruptedException {  //동시에 요청해야하므로 멀티쓰레드를 이용합니다.
        int threadCount=100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        //ExecutorService는 비동기로 실행하는 작업을, 단순화하여 사용할수 있게 도와주는, 자바의 API입니다.
        CountDownLatch latch = new CountDownLatch(threadCount); // <- 100개의 요청이 끝날때까지 기다려야하므로 CountdownLatch를 활용합니다.

        for(int i=0;i<threadCount;i++){
            executorService.submit(()->{
                try {
                    stockService.decrease(1L, 1L);
                }
                finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
        //CountdownLatch는 다른 스레드에서 수행중인 작업이 완료될때까지, 대기할수있도록 도와주는 클래스다.

        Stock stock = stockRepository.findById(1L).orElseThrow();
        assertEquals(0L,stock.getQuantity());
        //test는 실패 0개가 무조건 안나올겁니다. -> race condition이 일어났기때문
        // 둘 이상의 쓰레드가 공유자원에 접근해서 동시에 변경하려고할때 발생하는 문제입니다. -> race condition.
        // 이게 지금 서로 같은값을 참조해서 갱신을해버렸기때문에 갱신이 누락되는겁니다. 동시에 같은값을 참조해버림.
        // 결론적으로, 하나의 쓰레드가 작업이완료된후에 다른 쓰레드가 데이터에 접근할수 있도록 하면됩니다.
    }



}