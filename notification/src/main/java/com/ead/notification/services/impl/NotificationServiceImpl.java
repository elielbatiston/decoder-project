package com.ead.notification.services.impl;

import com.ead.notification.enums.NotificationStatus;
import com.ead.notification.models.NotificationModel;
import com.ead.notification.repositories.NotificationRepository;
import com.ead.notification.services.NotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    public NotificationServiceImpl(final NotificationRepository repository) {
        this.repository = repository;
    }

    @Override
    public NotificationModel saveNotification(final NotificationModel model) {
        return repository.save(model);
    }

    @Override
    public Page<NotificationModel> findAllNotificationsByUser(final UUID userId, final Pageable pageable) {
        return repository.findAllByUserIdAndNotificationStatus(userId, NotificationStatus.CREATED, pageable);
    }

    @Override
    public Optional<NotificationModel> findByNotificationIdAndUserId(final UUID notificationId, final UUID userId) {
        return repository.findByNotificationIdAndUserId(notificationId, userId);
    }
}
