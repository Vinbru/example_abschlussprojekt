package propra2.handler;

import org.assertj.core.api.Assertions;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import propra2.handler.OrderProcessHandler;
import propra2.model.OrderProcess;
import propra2.model.OrderProcessStatus;
import propra2.repositories.OrderProcessRepository;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderProcessHandlerTest {

    private OrderProcessHandler orderProcessHandler;

    @Autowired
    OrderProcessRepository orderProcessRepository;

    public OrderProcessHandlerTest() {

        orderProcessHandler = new OrderProcessHandler();
    }

    @Test
    public void updateOrderProcessTest(){
        OrderProcess orderProcess = new OrderProcess();
        orderProcess.setId(1234L);
        orderProcess.setOwnerId(5678L);
        orderProcess.setRequestId(3456L);
        orderProcess.setStatus(OrderProcessStatus.PENDING);

        orderProcessRepository.save(orderProcess);

        orderProcess.setStatus(OrderProcessStatus.DENIED);

        orderProcessHandler.updateOrderProcess(orderProcess, orderProcessRepository);

        Optional<OrderProcess> expectedOrderProcess = orderProcessRepository.findById(2L);

        Assertions.assertThat(expectedOrderProcess.get().getStatus().toString()).isEqualTo("DENIED");
    }



}

