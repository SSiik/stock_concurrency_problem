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
        stockRepository.saveAndFlush(stock);
        //Test에서 assertEquals(expected,값) 이렇게 해서 검증을 진행해볼수 있습니다.
        //물론 dirty checking을 이용할수도 있습니다.
    }

    //자바에서 제공해주는것으로, synchronized를 이용하면, 손쉽게 한개의 스레드만 접근하도록 할수 있습니다.
    //하지만 이제, 선언부 public 이후에 synchronized를 붙인다고 해도 해결되지않습니다.
    //이제 이건 @Transactional의 동작방식때문인데요,
    /*
    *  자, stockService의 decrease 메소드가 호출될때, 트랜잭션 시작메소드가 발동하고, 그리고
    *  stockService의 decrease()메소드가 호출되고 -> 정상적으로 트랜잭션 종료메소드가 발동됩니다.
    *  우린 이미 알고있지만 트랜잭션 종료시점에 DB에 update가 발생합니다 (jpa)
    *  근데 이제 여기서 문제가 발생합니다.
    *  decrease()메소드가 완료되고 실제 DB에 update하기 "전"에, 다른 쓰레드가 decrease메소드를 호출할수있습니다.
    *  다른 스레드는 갱신전값을 가져가서 이전과 동일한 문제가 발생하는겁니다.
    *  사실 @Transactional을 활용하면, 우리의 service를 wrapping한 다른 클래스에서 실행이 되거든요 그친구의 동작원리입니다.
    *  아무튼 그 사이의 간격에서 다른 요청이 들어울수가 있다는점이 굉장히 취약합니다.
    *  자 그래서 결론적으로는, "@Transactional 어노테이션을 빼면 우리가 의도하는대로 정상적으로 동작"합니다.
    *  물론 위의 decrease메소드에 dirty checking 대신에 saveAndFlush같은걸로 다시 저장을 해줘야겠죠.
    *
    * 이제 근데 synchronized도 조금 문제점이 있습니다.
    * 자바의 synchronized는, 하나의 프로세스안에서만 보장이 됩니다. 서버가 1대일때는 데이터의 접근은 서버가 1대만해서 괜찮지만
    * 서버가 2대 그 이상이면 데이터의 접근을 여러대에서 할수 있게 됩니다.. 그니까 다른 서버간에서는 동시성이슈가 발생..
    * synchronized는 각 프로세스 안에서만 보장이되기때문에 결국 여러 쓰레드에서 동시적으로 데이터에 접근을 할수있어서 race condition 발생.
    * 실제 운영중인 서비스는 대개 2대이상의 서버를 사용합니다 -> 그래서 synchronized를 사용하지 않습니다.
    * */
}
