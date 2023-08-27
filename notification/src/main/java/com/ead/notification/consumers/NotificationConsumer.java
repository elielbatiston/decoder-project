package com.ead.notification.consumers;

import com.ead.notification.dtos.NotificationCommandDto;
import com.ead.notification.enums.NotificationStatus;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.services.NotificationService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class NotificationConsumer {

    private final NotificationService service;

    public NotificationConsumer(final NotificationService service) {
        this.service = service;
    }

    @RabbitListener(bindings = @QueueBinding(
        value = @Queue(value = "${ead.broker.queue.notificationCommandQueue.name}", durable = "true"),
        exchange = @Exchange(value = "${ead.broker.exchange.notificationCommandExchange}", type = ExchangeTypes.TOPIC, ignoreDeclarationExceptions = "true"),
        key = "${ead.broker.key.notificationCommandKey}"
    ))
    public void listen(@Payload final NotificationCommandDto notificationCommandDto) {
        final var model = new NotificationModel();
        BeanUtils.copyProperties(notificationCommandDto, model);
        model.setCreationDate(LocalDateTime.now(ZoneId.of("UTC")));
        model.setNotificationStatus(NotificationStatus.CREATED);
        service.saveNotification(model);
    }
}
