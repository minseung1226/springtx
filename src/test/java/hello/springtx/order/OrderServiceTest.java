package hello.springtx.order;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
@Slf4j
class OrderServiceTest {

    @Autowired OrderService orderService;
    @Autowired OrderRepository repository;

    @Test
    void complate() throws NotEnoughtMoneyException {
        Order order=new Order();
        order.setUsername("정상");

        orderService.order(order);

        Order findOrder = repository.findById(order.getId()).get();

        assertThat(findOrder.getPayment()).isEqualTo("완료");
    }

    @Test
    void runtimeException() throws NotEnoughtMoneyException {
        Order order=new Order();
        order.setUsername("예외");

        assertThatThrownBy(()->orderService.order(order)).isInstanceOf(RuntimeException.class);

        Optional<Order> orderOptional = repository.findById(order.getId());

        assertThat(orderOptional.isEmpty()).isTrue();
    }

    @Test
    void bizException(){
        Order order=new Order();
        order.setUsername("잔고 부족");

        try {
            orderService.order(order);
        } catch (NotEnoughtMoneyException e) {
            log.info("고객에게 잔고부족을 알리고 별도 계좌 안내");
        }

        Order findOrder = repository.findById(order.getId()).get();

        assertThat(findOrder.getPayment()).isEqualTo("대기");
    }
}