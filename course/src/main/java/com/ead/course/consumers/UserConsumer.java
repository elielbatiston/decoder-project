package com.ead.course.consumers;

import com.ead.course.dtos.UserEventDto;
import com.ead.course.enums.ActionType;
import com.ead.course.services.UserService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class UserConsumer {

    @Autowired
    private UserService service;

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "${ead.broker.queue.userEventQueue.name}", durable = "true"),
        exchange = @Exchange(value = "${ead.broker.exchange.userEventExchange}", type = ExchangeTypes.FANOUT)
    ))
    public void listenUserEvent(@Payload UserEventDto dto) {
        final var model = dto.convertToUserModel();

        switch (ActionType.valueOf(dto.getActionType())) {
            case CREATE:
            case UPDATE:
                service.save(model);
                break;
            case DELETE:
                service.delete(dto.getUserId());
                break;
        }
    }
}
